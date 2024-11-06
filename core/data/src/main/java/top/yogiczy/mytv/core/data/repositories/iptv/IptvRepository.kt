package top.yogiczy.mytv.core.data.repositories.iptv

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.network.await
import top.yogiczy.mytv.core.data.repositories.FileCacheRepository
import top.yogiczy.mytv.core.data.repositories.iptv.parser.IptvParser
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.core.data.xxx.bean.QmExtraResult
import top.yogiczy.mytv.core.data.xxx.bean.QmHybridUrlsMapResult
import top.yogiczy.mytv.core.data.xxx.context.XxxContext
import top.yogiczy.mytv.core.data.xxx.okhttp.OkhttpQmExtraParamSearcher

/**
 * 直播源数据获取
 */
class IptvRepository(
    private val source: IptvSource,
) : FileCacheRepository(
    if (source.isLocal) source.url
    else "iptv-${source.url.hashCode().toUInt().toString(16)}.txt",
    source.isLocal,
) {
    private val log = Logger.create(javaClass.simpleName)

    /**
     * 2024.11.6: 尝试解析可选的qmextra header，获取自定义的配置
     */
    private fun parseQmExtraResult(okHttpResponse: Response?): QmExtraResult {
        val qmExtraResult =QmExtraResult()
        if ((okHttpResponse == null)) {
            log.e("load response null")
            return qmExtraResult
        }
        try {
            val qmextra = OkhttpQmExtraParamSearcher().searchQmExtraParam(okHttpResponse)
            //            Logger.debug("url:"+url+",qmextra"+qmextra);
            if (qmextra.isNullOrBlank()) {
                log.d("qmextra not found")
                return qmExtraResult
            }
            log.d("found qmextra:$qmextra")
            val jsonObject = try{
                JSONObject(qmextra)
            }catch (ex: Exception){
                ex.printStackTrace()
                // 尝试64位解码后，再获取json
                val qmextraDecoded = String(Base64.decode(qmextra, Base64.DEFAULT or Base64.URL_SAFE or Base64.NO_WRAP), charset("UTF-8"))
                JSONObject(qmextraDecoded)
            }
            qmExtraResult.hybridMode = jsonObject.optString("hybridMode")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return qmExtraResult
    }

    /**
     * 2024.11.6: 尝试解析可选的qmhybridmap header，获取自定义的配置
     */
    private fun parseQmHybridUrlsMapResult(okHttpResponse: Response?): QmHybridUrlsMapResult {
        val headerResult =QmHybridUrlsMapResult()
        if ((okHttpResponse == null)) {
            log.e("load response null")
            return headerResult
        }
        try {
            val headerParam = OkhttpQmExtraParamSearcher().searchExtraParam(okHttpResponse, "qmhybridmap")
            //            Logger.debug("url:"+url+",qmextra"+qmextra);
            if (headerParam.isNullOrBlank()) {
                log.d("qmextra hybrid map not found")
                return headerResult
            }
            log.d("found qmextra hybrid map:$headerParam")
            val jsonObject = try{
                JSONObject(headerParam)
            }catch (ex: Exception){
                ex.printStackTrace()
                // 尝试64位解码后，再获取json
                val qmextraDecoded = String(Base64.decode(headerParam, Base64.DEFAULT or Base64.URL_SAFE or Base64.NO_WRAP), charset("UTF-8"))
                JSONObject(qmextraDecoded)
            }
            headerResult.hybridUrlsMap = jsonObject.optString("hybridUrlsMap")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return headerResult
    }

    /**
     * 获取直播源数据
     */
    private suspend fun fetchSource(sourceUrl: String): String {
        log.d("获取远程直播源: $source")

        val client = OkHttpClient()
        val request = Request.Builder().url(sourceUrl).build()

        try {
            val response = client.newCall(request).await()

            if (!response.isSuccessful) throw Exception("${response.code}: ${response.message}")

            val bodyStr = withContext(Dispatchers.IO) {
                response.body?.string() ?: ""
            }

            // 2024.11.6: 增加额外可选的控制处理
            try {
                val qmExtraResult = parseQmExtraResult(response)
                XxxContext.fetchSourceExtraConfigCallback(qmExtraResult)
            }catch (t: Throwable){
                t.printStackTrace()
            }
            // 2024.11.6: 由于header最大长度8KB，因此混合模式url映射列表的配置，单独一个header
            try {
                val qmExtraHybridUrlsMapResult = parseQmHybridUrlsMapResult(response)
                XxxContext.fetchSourceHybridListMapCallback(qmExtraHybridUrlsMapResult)
            }catch (t: Throwable){
                t.printStackTrace()
            }

            return bodyStr
        } catch (ex: Exception) {
            log.e("获取直播源失败", ex)
            throw Exception("获取直播源失败，请检查网络连接", ex)
        }
    }

    /**
     * 获取直播源分组列表
     */
    suspend fun getChannelGroupList(cacheTime: Long): ChannelGroupList {
        try {
            val sourceData = getOrRefresh(if (source.isLocal) Long.MAX_VALUE else cacheTime) {
                fetchSource(source.url)
            }

            val parser = IptvParser.instances.first { it.isSupport(source.url, sourceData) }
            val startTime = System.currentTimeMillis()
            val groupList = parser.parse(sourceData)
            log.i(
                listOf(
                    "解析直播源（${source.name}）完成：${groupList.size}个分组",
                    "${groupList.sumOf { it.channelList.size }}个频道",
                    "${groupList.sumOf { it.channelList.sumOf { channel -> channel.urlList.size } }}条线路",
                    "耗时：${System.currentTimeMillis() - startTime}ms",
                ).joinToString()
            )

            return groupList
        } catch (ex: Exception) {
            log.e("获取直播源失败", ex)
            throw Exception(ex)
        }
    }

    override suspend fun clearCache() {
        if (source.isLocal) return
        super.clearCache()
    }
}
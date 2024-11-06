package top.yogiczy.mytv.tv

import android.app.Application
import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.json.JSONObject
import top.yogiczy.mytv.core.data.AppData
import top.yogiczy.mytv.core.data.utils.ChannelUtil
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.core.data.xxx.common.encoding.Base64Helper
import top.yogiczy.mytv.core.data.xxx.common.string.CommaSeparatorStringHelper
import top.yogiczy.mytv.core.data.xxx.config.IConfigConsts
import top.yogiczy.mytv.core.data.xxx.context.XxxContext
import top.yogiczy.mytv.tv.ui.utils.Configs

class MyTVApplication : Application() {
    private val log = Logger.create(javaClass.simpleName)
    override fun onCreate() {
        super.onCreate()

        application = this
        XxxContext.fetchSourceExtraConfigCallback = { qmExtraResult ->
            val hybridMode = qmExtraResult.hybridMode
            val commaSeparatorStringHelper = CommaSeparatorStringHelper()
            if (commaSeparatorStringHelper.isStrContainsValue(hybridMode, IConfigConsts.FALSE_STR)){
                Configs.iptvHybridMode = Configs.IptvHybridMode.DISABLE
            }else if (commaSeparatorStringHelper.isStrContainsValue(hybridMode, IConfigConsts.TRUE_STR)){
                Configs.iptvHybridMode = Configs.IptvHybridMode.HYBRID_FIRST
            }

            // 2024.11.6: 增加可选的加载超时时长设置
            val loadTimeout = qmExtraResult.loadTimeout
            if (loadTimeout > 1*1000){
                Configs.videoPlayerLoadTimeout = loadTimeout
            }
        }
        XxxContext.fetchSourceHybridListMapCallback = { qmHybridUrlsMapResult ->
            val hybridUrlsMap = qmHybridUrlsMapResult.hybridUrlsMap
            if (hybridUrlsMap?.isNotBlank() == true){
                //log.d("hybridUrlsMap json:$hybridUrlsMap")
                // 先64位解码后，再获取json
                val decoded = Base64Helper.decode(hybridUrlsMap)
                log.d("xxxHybridWebViewUrl decoded:$decoded")
                val xxxHybridWebViewUrl: Map<String, List<String>> = Json.decodeFromString(decoded)
                //Json.decodeFromStream<>()
                //log.d("xxxHybridWebViewUrl parsed:$xxxHybridWebViewUrl")
                ChannelUtil.xxxHybridWebViewUrl = xxxHybridWebViewUrl
            }
        }


        AppData.init(applicationContext)
        UnsafeTrustManager.enableUnsafeTrustManager()
    }

    companion object {
        lateinit var application: Application
    }
}

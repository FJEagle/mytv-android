package top.yogiczy.mytv.core.data.xxx.okhttp

import okhttp3.Response

/**
 * @author NieYong(ny @ xiangjiabao.com)
 * @date 2022/7/27
 * 针对qm想在下载图片等接口请求额外返回一些结果参数的场景
 * 搜索okhttp response中额外返回的参数信息
 *
 * 至于为什么要额外携带参数，增加交互性和更好地兼容，避免新增接口来获取额外信息
 */
class OkhttpQmExtraParamSearcher {
    fun searchQmExtraParam(response: Response?): String? {
        return searchExtraParam(response, QM_EXTRA_PARAM_KEY_NAME)
    }

    /**
     * 会在redirect后的location的query，以及response的header中搜索是否额外有携带
     * @param response
     * @param paramKeyName
     * @return
     */
    fun searchExtraParam(response: Response?, paramKeyName: String): String? {
        if (response == null) {
            return null
        }
        // 首先搜索response的header
        val header = response.header(QM_HEADER_PREFIX + paramKeyName)
        if (header?.isNotBlank() == true) {
            return header
        }
        //也兼容下aliyun oss的header设置
        val aliyunOssHeader = response.header(ALIYUN_OSS_HEADER_PREFIX + paramKeyName)
        if (aliyunOssHeader?.isNotBlank() == true) {
            return aliyunOssHeader
        }

        // 其次搜索location中是否query携带(针对redirect场景)
        val url = response.request.url
        val extraParam = url.queryParameter(paramKeyName)
        if (extraParam?.isNotBlank() == true) {
            return extraParam
        }

        // 最后搜索前一个response（针对redirect场景）
        return searchExtraParam(response.priorResponse, paramKeyName)
    }

    companion object {
        private const val QM_EXTRA_PARAM_KEY_NAME = "qmextra"
        private const val QM_HEADER_PREFIX = "X-QM-"
        private const val ALIYUN_OSS_HEADER_PREFIX = "x-oss-meta-"
    }
}

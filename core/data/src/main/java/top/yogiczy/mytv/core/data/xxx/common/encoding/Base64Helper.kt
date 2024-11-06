package top.yogiczy.mytv.core.data.xxx.common.encoding

import android.util.Base64

/**
 * @author NieYong(ny@xiangjiabao.com)
 * @date 2024/11/6
 * @Decription TODO
 */
object Base64Helper {

    /**
     * 2024.11.6: 兼容default和urlSafe编码
     */
    fun decode(base64Str: String): String {
        return try{
            String(Base64.decode(base64Str, Base64.DEFAULT), charset("UTF-8"))
        }catch (e: Exception){
            e.printStackTrace()
            String(Base64.decode(base64Str, Base64.URL_SAFE), charset("UTF-8"))
        }
    }
}
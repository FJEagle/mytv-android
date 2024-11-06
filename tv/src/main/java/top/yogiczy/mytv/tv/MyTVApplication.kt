package top.yogiczy.mytv.tv

import android.app.Application
import top.yogiczy.mytv.core.data.AppData
import top.yogiczy.mytv.core.data.xxx.common.string.CommaSeparatorStringHelper
import top.yogiczy.mytv.core.data.xxx.config.IConfigConsts
import top.yogiczy.mytv.core.data.xxx.context.XxxContext
import top.yogiczy.mytv.tv.ui.utils.Configs

class MyTVApplication : Application() {
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
        }


        AppData.init(applicationContext)
        UnsafeTrustManager.enableUnsafeTrustManager()
    }

    companion object {
        lateinit var application: Application
    }
}

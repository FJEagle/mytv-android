package top.yogiczy.mytv.tv

import android.app.Application
import top.yogiczy.mytv.core.data.AppData

class MyTVApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this


        AppData.init(applicationContext)
        UnsafeTrustManager.enableUnsafeTrustManager()
    }

    companion object {
        lateinit var application: Application
    }
}

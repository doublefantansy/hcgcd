package cc.hc.media

import android.app.Application
import hzkj.cc.base.IComponentApplication

/**

 * @Author chencheng
 * @Date 2020-01-19-14:53
 */
class MediaApplication : IComponentApplication {


    override fun init(application: Application?) {
        println("media_app")

//
//        DataAdapterImpl.getInstance()
//            .createDataAdapter("com.android.business.adapter.DataAdapterImpl")
//        loadLibrary()
//
//        DataAdapterImpl.getInstance().initServer("15.16.22.68", 9000)
//        var s = 1
    }

//    private fun loadLibrary() {
//        System.loadLibrary("gnustl_shared")
//        System.loadLibrary("CommonSDK")
//        System.loadLibrary("Encrypt")
//        System.loadLibrary("DPSStream")
//        System.loadLibrary("dsl")
//        System.loadLibrary("dslalien")
//        System.loadLibrary("PlatformSDK")
//        System.loadLibrary("DPRTSPSDK")
//    }
}
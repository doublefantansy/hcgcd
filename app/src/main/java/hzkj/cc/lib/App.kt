package hzkj.cc.hcgcd

import android.app.Application
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.android.business.adapter.DataAdapterImpl
import com.android.business.exception.BusinessException
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import hzkj.cc.base.IComponentApplication

/**

 * @Author chencheng
 * @Date 2020-03-03-10:49
 */
class App : Application() {
    companion object {
        val MODULESLIST =
                mutableListOf("cc.hc.media.MediaApplication", "hzkj.cc.map.MapApplication")
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)
        MMKV.initialize(this)
        modulesApplicationInit()
        Bugly.init(applicationContext, "b18c125cf6", false)
        JPushInterface.init(this)
        try {
            DataAdapterImpl.getInstance()
                    .createDataAdapter("com.android.business.adapter.DataAdapteeImpl")
//            loadLibrary()
        } catch (e: BusinessException) {
            e.printStackTrace()
        }
    }

    private fun modulesApplicationInit() {
        for (moduleImpl in MODULESLIST) {
            try {
                val clazz = Class.forName(moduleImpl)
                val obj = clazz.newInstance()
                if (obj is IComponentApplication) {
                    obj.init(this)
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        }
    }

}
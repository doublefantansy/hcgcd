package hzkj.cc.map

import android.app.Application
import com.baidu.mapapi.SDKInitializer
import hzkj.cc.base.IComponentApplication

/**

 * @Author chencheng
 * @Date 2020-03-02-13:07
 */
class MapApplication : IComponentApplication {

    override fun init(application: Application?) {
        SDKInitializer.initialize(application)
    }
}
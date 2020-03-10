package hzkj.cc.main

import androidx.lifecycle.MutableLiveData
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-02-27-16:22
 */
class MainViewModel:BaseViewModel() {
    companion object {
        var AREA = 10
    }
    var areaResposity = AreaResposity()
    var areaDatas = MutableLiveData<MutableList<hzkj.cc.main.Area>>()

    fun getAreas() {
        launchUI({
            areaDatas.value = areaResposity.getAreas()
                .data
        }, AREA)
    }
}
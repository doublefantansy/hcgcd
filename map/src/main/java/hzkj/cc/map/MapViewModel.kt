package hzkj.cc.map

import androidx.lifecycle.MutableLiveData
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-03-02-11:34
 */
class MapViewModel : BaseViewModel() {

    companion object {
        var SENNORS = 1
        var CAMERAS = 11
    }

    var sennorDatas = MutableLiveData<MutableList<Sennor>>()
    var cameraDatas = MutableLiveData<MutableList<Camera>>()

    var mapResposity = MapResposity()
    fun getSennors(areaCode: String) {
        launchUI({ sennorDatas.value = mapResposity.getSennors(areaCode).data }, SENNORS)
    }

    fun getCameras(areaCode: String) {
        launchUI({ cameraDatas.value = mapResposity.getCameras(areaCode).data }, CAMERAS)
    }
}
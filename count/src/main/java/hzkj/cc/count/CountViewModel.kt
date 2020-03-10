package hzkj.cc.count

import androidx.lifecycle.MutableLiveData
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-02-28-11:10
 */
class CountViewModel : BaseViewModel() {
    companion object {
        var COUNT = 0
//    var AREA = 10
    }

    var countDatas = MutableLiveData<MutableList<StatInfo>>()
    var countResposity = CountResposity()
    fun getCountDatas(
        startTime: String,
        endTime: String,
        areaCode: String
    ) {
        launchUI({
            countDatas.value = countResposity.getCountDatas(startTime, endTime, areaCode)
                .data
        }, COUNT)
    }


}
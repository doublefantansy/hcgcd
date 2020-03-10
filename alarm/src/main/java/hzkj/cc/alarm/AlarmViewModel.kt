package hzkj.cc.alarm

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-03-04-13:47
 */
class AlarmViewModel : BaseViewModel() {
    companion object {
        var ALARM = 1
        var ALARMIMG = 11
    }

    var alarmDatas = MutableLiveData<MutableList<SensorAlarmInfo>>()
    var alarmImg = MutableLiveData<String>()
    var imageView = MutableLiveData<ImageView>()
    private var resposity = AlarmResposity()
    fun getAlarmDatas(
        areaCode: String,
        startTime: String,
        endTime: String,
        pageNum: Int
    ) {
        launchUI({
            alarmDatas.value = resposity.getAlarmDatas(areaCode, startTime, endTime, 1, pageNum)
                .data
        }, ALARM)
    }

    fun searchAlarmImgById(
        id: Int,
        imageView: ImageView
    ) {
        launchUI({
            alarmImg.value = resposity.searchAlarmImgById(id)
                .data
            this@AlarmViewModel.imageView.value = imageView
        }, ALARMIMG)
    }
}
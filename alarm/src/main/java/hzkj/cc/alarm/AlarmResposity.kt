package hzkj.cc.alarm

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-03-04-14:20
 */
class AlarmResposity : BaseRepository() {
    suspend fun getAlarmDatas(
        areaCode: String,
        startTime: String,
        endTime: String,
        state: Int,
        pageNum: Int
    ): MyResponseData<MutableList<SensorAlarmInfo>> = request {
        RetrofitClient.getRetrofit(AlarmService::class.java)
            .getAlarmDatas(areaCode, startTime, endTime, state, pageNum)
    }

    suspend fun searchAlarmImgById(
        id: Int
    ): MyResponseData<String> = request {
        RetrofitClient.getRetrofit(AlarmService::class.java)
            .searchAlarmImgById(id)
    }
}
package hzkj.cc.alarm

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmService {
    @GET("/sensorAlarmInfos/search")
    suspend fun getAlarmDatas(
        @Query("areaCode") areaCode: String, @Query("startTime") startTime: String, @Query(
            "endTime"
        ) endTime: String, @Query(
            "state"
        ) state: Int, @Query("pageNum") pageNum: Int
    ): MyResponseData<MutableList<SensorAlarmInfo>>

    @GET("/sensorAlarmInfos/search/img")
    suspend fun searchAlarmImgById(@Query("id") id: Int): MyResponseData<String>
}
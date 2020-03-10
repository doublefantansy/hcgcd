package hzkj.cc.count

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface CountService {
    @GET("/sensorAlarmInfos/count")
    suspend fun getCountDatas(
        @Query("startTime")
        startTime: String,
        @Query("endTime")
        endTime: String,
        @Query("areaCode")
        areaCode: String
    ): MyResponseData<MutableList<StatInfo>>

}
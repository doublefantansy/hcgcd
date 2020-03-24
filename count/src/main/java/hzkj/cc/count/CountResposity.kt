package hzkj.cc.count

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-02-28-11:24
 */
class CountResposity : BaseRepository() {

    suspend fun getCountDatas(
        startTime: String,
        endTime: String,
        areaCode: String
    ): MyResponseData<StatInfo> = request {
        RetrofitClient.getRetrofit(CountService::class.java)
            .getCountDatas(startTime, endTime, areaCode)
    }
}
package hzkj.cc.main

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository
import hzkj.cc.count.AreaService

/**

 * @Author chencheng
 * @Date 2020-02-28-15:30
 */
class AreaResposity : BaseRepository() {

    suspend fun getAreas(): MyResponseData<MutableList<Area>> = request {
        RetrofitClient.getRetrofit(AreaService::class.java)
            .getAreas()
    }
}
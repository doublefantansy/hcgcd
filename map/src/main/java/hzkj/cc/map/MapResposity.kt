package hzkj.cc.map

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-03-02-13:37
 */
class MapResposity : BaseRepository() {

    suspend fun getSennors(areaCode: String): MyResponseData<MutableList<Sennor>> = request {
        RetrofitClient.getRetrofit(MapService::class.java).getSennors(areaCode)
    }

    suspend fun getCameras(areaCode: String): MyResponseData<MutableList<Camera>> = request {
        RetrofitClient.getRetrofit(MapService::class.java).getCamaras(areaCode)
    }
}
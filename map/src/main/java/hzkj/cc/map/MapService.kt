package hzkj.cc.map

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {
    @GET("/sennor/search")
    suspend fun getSennors(@Query("areaCode") areaCode: String): MyResponseData<MutableList<Sennor>>

    @GET("/camera/search")
    suspend fun getCamaras(@Query("areaCode") areaCode: String): MyResponseData<MutableList<Camera>>
}
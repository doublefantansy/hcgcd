package cc.hc.media

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaService {
    @GET("/camera/search")
    suspend fun getCameras(@Query("areaCode") areaCode: String): MyResponseData<MutableList<Camera>>

    @GET("/camera/search_by_sennor")
    suspend fun getCamarasBySennorCode(@Query("sennorCode") sennorCode: String): MyResponseData<MutableList<Camera>>
}
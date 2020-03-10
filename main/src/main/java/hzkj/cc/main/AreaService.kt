package hzkj.cc.count

import hzkj.cc.base.MyResponseData
import hzkj.cc.main.Area
import retrofit2.http.GET

interface AreaService {
  @GET("/area/all")
  suspend fun getAreas(): MyResponseData<MutableList<Area>>
}
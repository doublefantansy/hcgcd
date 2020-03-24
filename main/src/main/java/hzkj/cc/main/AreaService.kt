package hzkj.cc.main

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET

interface AreaService {
  @GET("/area/all")
  suspend fun getAreas(): MyResponseData<MutableList<Area>>
}
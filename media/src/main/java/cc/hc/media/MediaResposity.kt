package cc.hc.media

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-03-02-15:29
 */
class MediaResposity : BaseRepository() {

    suspend fun getCameras(areaCode: String): MyResponseData<MutableList<Camera>> = request {
        RetrofitClient.getRetrofit(MediaService::class.java).getCameras(areaCode)
    }
}
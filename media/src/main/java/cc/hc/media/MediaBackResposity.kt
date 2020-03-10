package cc.hc.media

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-03-05-15:19
 */
class MediaBackResposity : BaseRepository() {
    suspend fun getCamarasBySennorCode(sennorCode: String): MyResponseData<MutableList<Camera>> =
        request {
            RetrofitClient.getRetrofit(MediaService::class.java).getCamarasBySennorCode(sennorCode)
        }
}
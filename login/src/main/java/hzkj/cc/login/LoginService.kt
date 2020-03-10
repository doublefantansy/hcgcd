package hzkj.cc.login

import hzkj.cc.base.MyResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginService {
    @GET("user/login")
    suspend fun login(
        @Query("username") phone: String, @Query("password") password: String, @Query(
            "jpush"
        ) jpush: String
    ): MyResponseData<AppUser>
}
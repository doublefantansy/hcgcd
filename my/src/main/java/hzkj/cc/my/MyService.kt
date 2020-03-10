package hzkj.cc.my

import hzkj.cc.base.MyResponseData
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface MyService {
    @POST("/user/update_password")
    suspend fun updatePassword(@Body appUser: AppUser, @Query("newPassword") newPassword: String): MyResponseData<Int>
}
package hzkj.cc.my

import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-03-06-15:41
 */
class MyResoposity : BaseRepository() {
    suspend fun updatePassword(appUser: AppUser,newPassword: String)=request {
        RetrofitClient.getRetrofit(MyService::class.java).updatePassword(appUser,newPassword)
    }
}
package hzkj.cc.login

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.RetrofitClient
import hzkj.cc.base.base.BaseRepository

/**

 * @Author chencheng
 * @Date 2020-02-27-15:29
 */
class LoginResposity : BaseRepository()
{
		suspend fun login(phone:String, password:String,jPush:String):MyResponseData<AppUser> = request {
				RetrofitClient.getRetrofit(LoginService::class.java)
						.login(phone, password, jPush)
		}
}
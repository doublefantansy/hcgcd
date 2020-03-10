package hzkj.cc.base.base

import hzkj.cc.base.MyResponseData
import hzkj.cc.base.TokenInvalidException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class BaseRepository
{
    suspend fun <T : Any> request(call: suspend () -> MyResponseData<T>): MyResponseData<T>
    {
        return withContext(Dispatchers.IO) { call.invoke() }.apply {
            //这儿可以对返回结果errorCode做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回errorCode 为 100，下面代码实现,再到baseActivity通过观察error来处理
            when (code)
            {
                313 -> throw TokenInvalidException()
            }
        }
    }
    //    class TokenInvalidException(msg: String = "token invalid") : Exception(msg)
}
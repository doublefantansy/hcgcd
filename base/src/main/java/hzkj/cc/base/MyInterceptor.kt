package hzkj.cc.base

import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyInterceptor : Interceptor {
    lateinit var request: Request
    private lateinit var response: Response
    override fun intercept(chain: Interceptor.Chain): Response {
        request = chain.request()
        if (!request.url().toString().contains(RetrofitClient.url + "user/login")) {
            request = request.newBuilder()
                .also {
                    if (MMKV.defaultMMKV().decodeString("token") != null) it.header(
                        "Authorization", MMKV.defaultMMKV().decodeString("token")
                    )
                }
                .method(request.method(), request.body())
                .build()
        } else {
            response = chain.proceed(request)
            MMKV.defaultMMKV()
                .putString("token", response.header("Authorization"))
            return response
        }
        return chain.proceed(request)
    }
}
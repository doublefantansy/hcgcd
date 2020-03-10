package hzkj.cc.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //    val url = "http://222.177.213.6:21555/jxapi/"
    val url = "http://15.16.22.101:8080/"

    fun <S> getRetrofit(serviceClass: Class<S>, baseUrl: String = url): S {
        var s = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().addInterceptor(MyInterceptor()).build())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return s
            .create(serviceClass)
    }
}
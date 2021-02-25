package xyz.ramil.searchingit.data.net

import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.ramil.searchingit.App.Companion.API
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiManager {

    init {
        updateHost(API)
    }

    lateinit var apiService: ApiService

    fun updateHost(host: String) {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()
                val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method, original.body)
                        .build()
                return chain.proceed(request)
            }
        })
        val client: OkHttpClient = httpClient.build()
        val mRetrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
        apiService = mRetrofit.create(ApiService::class.java)
    }

    fun search(q: String, page: Int, perPage: Int): Single<xyz.ramil.searchingit.data.model.Response> {
        return apiService.search(q, page, perPage)
    }
}
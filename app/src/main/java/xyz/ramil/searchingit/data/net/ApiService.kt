package xyz.ramil.searchingit.data.net

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import xyz.ramil.searchingit.data.model.Response

interface ApiService {
    @GET("search/users")
    fun search(@Query("q") q: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<Response>
}
package net.kathir.pagination.api

import net.kathir.pagination.model.ResponseTopMovies
import net.kathir.pagination.utils.API_KEY
import net.kathir.pagination.utils.APP_LANGUAGE
import net.kathir.pagination.utils.MOVIE_TOP_RATED_API
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET(MOVIE_TOP_RATED_API)
    fun getTopRatedMovies(@Query("api_key") apiKey : String = API_KEY

                        ,@Query("language") language : String = APP_LANGUAGE

                        ,@Query("page") pageIndex: Int) : Call<ResponseTopMovies>
}
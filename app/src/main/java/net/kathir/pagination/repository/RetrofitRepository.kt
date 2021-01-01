package net.kathir.pagination.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import net.kathir.pagination.api.API
import net.kathir.pagination.api.ApiClient
import net.kathir.pagination.model.ResponseTopMovies
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitRepository private constructor(app: Application)  {

    private var instanceApi : API

    init {
        ApiClient.init(app)
        instanceApi = ApiClient.instance
    }

    companion object
    {
        private var retrofitRepository: RetrofitRepository?=null
        @Synchronized
        fun getInstance(app: Application): RetrofitRepository?{
            if(retrofitRepository == null)
            {
                retrofitRepository = RetrofitRepository(app)

            }
            return retrofitRepository
        }
    }

    fun loadPage(topMoviesResponse: MutableLiveData<Any>, page: Int)
    {
        instanceApi.getTopRatedMovies(pageIndex = page).enqueue(object : Callback<ResponseTopMovies>{
            override fun onFailure(call: Call<ResponseTopMovies>, t: Throwable) {
                topMoviesResponse.value = t
            }

            override fun onResponse(
                call: Call<ResponseTopMovies>,
                response: Response<ResponseTopMovies>
            ) {
                if(response.isSuccessful)
                {
                    topMoviesResponse.value = response.body()
                }
                else
                {
                    topMoviesResponse.value = "error"
                }
            }

        })
    }
}
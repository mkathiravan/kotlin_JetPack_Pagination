package net.kathir.pagination.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.kathir.pagination.repository.RetrofitRepository

class HomeViewModel(application: Application) : AndroidViewModel(application)
{
    private var retrofitRepository: RetrofitRepository? = RetrofitRepository.getInstance(application)

    private var _topMoviesFirstPageResponse = MutableLiveData<Any>()

    private var _topMoviesNextPageResponse = MutableLiveData<Any>()

    fun requestFirstPageTopMovies(page: Int)
    {
        retrofitRepository!!.loadPage(_topMoviesFirstPageResponse, page)

    }

    fun requestFirstNextPageMovies(page: Int)
    {
        retrofitRepository!!.loadPage(_topMoviesNextPageResponse,page)
    }

    val topMoviesFirstPageResponse : LiveData<Any> get() = _topMoviesFirstPageResponse

    val topMoviesNextPageResponse : LiveData<Any> get() = _topMoviesNextPageResponse
}
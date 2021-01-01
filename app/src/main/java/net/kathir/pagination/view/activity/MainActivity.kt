package net.kathir.pagination.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import net.kathir.pagination.R
import net.kathir.pagination.databinding.ActivityMainBinding
import net.kathir.pagination.model.ResponseTopMovies
import net.kathir.pagination.model.ResultItem
import net.kathir.pagination.utils.CheckValidation
import net.kathir.pagination.utils.MessageLog
import net.kathir.pagination.utils.PaginationScrollListener
import net.kathir.pagination.view.adapter.PaginationAdapter
import net.kathir.pagination.viewModel.HomeViewModel
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mAdapter: PaginationAdapter

    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this

        homeViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(HomeViewModel::class.java)

        initMyOrderRecyclerView()
        observerDataRequest()
    }

    private fun initMyOrderRecyclerView() {
        //attach adapter to  recycler
        mAdapter = PaginationAdapter(this@MainActivity)
        binding.adapterTopMovies = mAdapter
        binding.recyclerMyOrders.setHasFixedSize(true)
        binding.recyclerMyOrders.itemAnimator = DefaultItemAnimator()

        loadFirstPage()

        binding.recyclerMyOrders.addOnScrollListener(object : PaginationScrollListener(binding.recyclerMyOrders.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                Handler(Looper.myLooper()!!).postDelayed({
                    loadNextPage()
                }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    private fun observerDataRequest(){
        homeViewModel.topMoviesFirstPageResponse.observe(this) {
            if (it is ResponseTopMovies) {
                hideErrorView()
                val results: MutableList<ResultItem> = fetchResults(it) as MutableList<ResultItem>
                binding.mainProgress.visibility = View.GONE
                mAdapter.addAll(results)
                totalPages = it.totalPages!!

                if (currentPage <= totalPages) mAdapter.addLoadingFooter()
                else isLastPage = true
            }else if (it is Throwable){
                showErrorView(it)
            }else{
                MessageLog.setLogCat("TAG_TEST" , "Error First Page")
            }
        }

        homeViewModel.topMoviesNextPageResponse.observe(this) {
            if (it is ResponseTopMovies) {

                val results = fetchResults(it) as MutableList<ResultItem>
                mAdapter.removeLoadingFooter()
                isLoading = false
                mAdapter.addAll(results)

                if (currentPage != totalPages) mAdapter.addLoadingFooter()
                else isLastPage = true

            }else if (it is Throwable){
                mAdapter.showRetry(true, fetchErrorMessage(it))
            }else{
                MessageLog.setLogCat("TAG_TEST" , "Error First Page")
            }

        }
    }

    private fun fetchResults(moviesTopModel: ResponseTopMovies): List<ResultItem>? {
        return moviesTopModel.results
    }

    private fun loadFirstPage() {
        hideErrorView()
        if (CheckValidation.isConnected(this)) {
            homeViewModel.requestFirstPageTopMovies(currentPage)
        }else{
            showErrorView(null)
        }
    }

    fun loadNextPage() {
        if (CheckValidation.isConnected(this)) {
            homeViewModel.requestFirstNextPageMovies(currentPage)
        }else{
            mAdapter.showRetry(true, fetchErrorMessage(null))
        }
    }

    private fun showErrorView(throwable: Throwable?) {
        if (binding.lyError.errorLayout.visibility == View.GONE) {
            binding.lyError.errorLayout.visibility = View.VISIBLE
            binding.mainProgress.visibility = View.GONE

            if (!CheckValidation.isConnected(this)) {
                binding.lyError.errorTxtCause.setText(R.string.error_msg_no_internet)
            } else {
                if (throwable is TimeoutException) {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_timeout)
                } else {
                    binding.lyError.errorTxtCause.setText(R.string.error_msg_unknown)
                }
            }
        }
    }

    private fun hideErrorView() {
        if (binding.lyError.errorLayout.visibility == View.VISIBLE) {
            binding.lyError.errorLayout.visibility = View.GONE
            binding.mainProgress.visibility = View.VISIBLE
        }
    }

    private fun fetchErrorMessage(throwable: Throwable?): String {
        var errorMsg: String = resources.getString(R.string.error_msg_unknown)

        if (!CheckValidation.isConnected(this)) {
            errorMsg = resources.getString(R.string.error_msg_no_internet)
        } else if (throwable is TimeoutException) {
            errorMsg = resources.getString(R.string.error_msg_timeout)
        }

        return errorMsg
    }

}
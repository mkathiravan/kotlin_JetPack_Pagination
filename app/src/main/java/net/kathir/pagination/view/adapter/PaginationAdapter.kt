package net.kathir.pagination.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.kathir.pagination.BR
import net.kathir.pagination.R
import net.kathir.pagination.databinding.ItemLoadingBinding
import net.kathir.pagination.databinding.ItemMovieBinding
import net.kathir.pagination.model.ResultItem
import net.kathir.pagination.view.activity.MainActivity
import net.kathir.pagination.view.`interface`.PaginationAdapterCallback


class PaginationAdapter(private var mActivity: MainActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
    PaginationAdapterCallback{

    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var moviesModels: MutableList<ResultItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemMovieBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_movie, parent, false)
            TopMoviesVH(binding)
        }else{
            val binding: ItemLoadingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            LoadingVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = moviesModels[position]
        if(getItemViewType(position) == item){
            val myOrderVH: TopMoviesVH = holder as TopMoviesVH
            myOrderVH.itemRowBinding.movieProgress.visibility = View.VISIBLE
            myOrderVH.bind(model)
        }else{
            val loadingVH: LoadingVH = holder as LoadingVH
            if (retryPageLoad) {
                loadingVH.itemRowBinding.loadmoreErrorlayout.visibility = View.VISIBLE
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.GONE

                if(errorMsg != null) loadingVH.itemRowBinding.loadmoreErrortxt.text = errorMsg
                else loadingVH.itemRowBinding.loadmoreErrortxt.text = mActivity.getString(R.string.error_msg_unknown)

            } else {
                loadingVH.itemRowBinding.loadmoreErrorlayout.visibility = View.GONE
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.VISIBLE
            }

            loadingVH.itemRowBinding.loadmoreRetry.setOnClickListener{
                showRetry(false, "")
                retryPageLoad()
            }
            loadingVH.itemRowBinding.loadmoreErrorlayout.setOnClickListener{
                showRetry(false, "")
                retryPageLoad()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (moviesModels.size > 0) moviesModels.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            item
        }else {
            if (position == moviesModels.size - 1 && isLoadingAdded) {
                loading
            } else {
                item
            }
        }
    }

    override fun retryPageLoad() {
        mActivity.loadNextPage()
    }


    class TopMoviesVH(binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemMovieBinding = binding
        fun bind(obj: Any?) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
        }
    }

    class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(moviesModels.size - 1)
        this.errorMsg = errorMsg
    }

    fun addAll(movies: MutableList<ResultItem>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: ResultItem) {
        moviesModels.add(moive)
        notifyItemInserted(moviesModels.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(ResultItem())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position: Int =moviesModels.size -1
        val movie: ResultItem = moviesModels[position]


        if(movie != null){
            moviesModels.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
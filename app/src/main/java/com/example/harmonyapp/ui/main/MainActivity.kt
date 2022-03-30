package com.example.harmonyapp.ui.main

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.harmonyapp.base.BaseActivity
import com.example.harmonyapp.data.model.ImageListItem
import com.example.harmonyapp.databinding.ActivityMainBinding
import com.example.harmonyapp.databinding.ItemImageBinding
import com.example.harmonyapp.ui.detail.FullScreenActivity
import com.example.harmonyapp.ui.main.adapter.ImageAdapter
import com.example.harmonyapp.ui.main.adapter.PagingLoadStateAdapter

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalPagingApi
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    ImageAdapter.ImageItemClickListener {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var imageAdapter: ImageAdapter

    override fun getVM(): MainViewModel = mainViewModel

    override fun bindVM(binding: ActivityMainBinding, vm: MainViewModel) =
        with(binding) {
            val rLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            imageAdapter.refresh()

            with(imageAdapter) {
                rvImages.apply {
                    postponeEnterTransition()
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                    layoutManager = rLayoutManager
                }
                rvImages.adapter = withLoadStateHeaderAndFooter(
                    header = PagingLoadStateAdapter(this),
                    footer = PagingLoadStateAdapter(this))

                swipeRefreshLayout.setOnRefreshListener { refresh() }
                imageClickListener = this@MainActivity

                with(vm) {
                    launchOnLifecycleScope {
                        imageResponse.collectLatest { submitData(it) }
                    }
                    launchOnLifecycleScope {
                        loadStateFlow.collectLatest {
                            swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                        }
                    }
                }

                launchOnLifecycleScope {
                    imageAdapter.loadStateFlow.collect { loadState ->
                        // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                        val errorState = loadState.source.append as? LoadState.Error
                            ?: loadState.source.prepend as? LoadState.Error
                            ?: loadState.append as? LoadState.Error
                            ?: loadState.prepend as? LoadState.Error
                            ?: loadState.source.refresh as? LoadState.Error
                        errorState?.let {
                            Toast.makeText(
                                this@MainActivity,
                                "\uD83D\uDE28 Wooops ${it.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }


            val edit  = this.idEdit
            edit.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(editable: Editable?) {
                    editable?.let {

                        Toast.makeText(
                            this@MainActivity,
                            it.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        if(it.isNotBlank()) {
                            viewModel.onImageLimitChange(it.toString().toInt())
                        }
                        else
                            viewModel.onImageLimitChange(0)
                        imageAdapter.refresh()

                        // i need to do something here!!!!  manipulating the size of the list and return the new amount of items shown


                    }
                }
            })

        }


    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onImageClicked(binding: ItemImageBinding, item: ImageListItem) {
        //Navigate to detail
        val intent = Intent(this@MainActivity, FullScreenActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }
}
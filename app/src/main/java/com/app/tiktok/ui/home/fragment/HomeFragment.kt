package com.app.tiktok.ui.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import com.app.tiktok.model.ResultData
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.home.adapter.StoriesPagerAdapter
import com.app.tiktok.ui.home.adapter.VerticalCubeTransformer
import com.app.tiktok.ui.main.viewmodel.MainViewModel
import com.app.tiktok.ui.story.StoryBunchViewModel
import com.app.tiktok.utils.Constants
import com.app.tiktok.work.PreCachingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val homeViewModel by activityViewModels<MainViewModel>()
    private val storyBunchViewModel by activityViewModels<StoryBunchViewModel>()
    //private lateinit var storiesPagerAdapter: StoriesPagerAdapter

    companion object{
        var viewPager2: ViewPager2? = null
        var storiesPagerAdapter: StoriesPagerAdapter? = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager2 = view_pager_stories
        viewPager2?.cameraDistance = 1000000000000000000000000000f
        home_fragment_parent.cameraDistance = 1000000000000000000000000000f
        viewPager2?.setPageTransformer(VerticalCubeTransformer())
        viewPager2?.offscreenPageLimit = 4

        val storiesData = homeViewModel.getDataList()

        storiesData.observe(viewLifecycleOwner, Observer { value ->
            when(value) {
                is ResultData.Loading -> {
                    Log.d("lag", "Result data is loading")
                }
                is ResultData.Success -> {
                    Log.d("lag", "Result data SUCCESS")

                    if (!value.data.isNullOrEmpty()) {
                        val dataList = value.data

                        //We add a filter here to get posts that have parentId = 0; meaning they have no parents
                        //and that they are the parent posts
                        val parentPostsList = dataList.filter { it.parentId == 0L }
                        Log.d("lag", "AFter filter")

                        if(parentPostsList is MutableList){
                            storiesPagerAdapter = StoriesPagerAdapter(this, parentPostsList)
                        }
                        view_pager_stories.adapter = storiesPagerAdapter

                        view_pager_stories.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                storyBunchViewModel.setDraggable(true)
                                super.onPageSelected(position)
                            }
                        })
                        //startPreCaching(dataList)
                    }
                }
            }
        })
    }

    private fun startPreCaching(dataList: ArrayList<StoriesDataModel>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.storyUrl
        }
        val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(requireContext())
            .enqueue(preCachingWork)
    }
}
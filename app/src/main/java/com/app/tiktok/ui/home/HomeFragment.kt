package com.app.tiktok.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.main.MainViewModel
import com.app.tiktok.ui.story.GalleriesViewModel
import com.app.tiktok.utils.Constants
import com.app.tiktok.work.PreCachingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class  HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val homeViewModel by activityViewModels<MainViewModel>()
    private val galleriesViewModel by activityViewModels<GalleriesViewModel>()

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
        viewPager2?.setPageTransformer(
            VerticalCubeTransformer()
        )
        viewPager2?.offscreenPageLimit = 3

        initializeGalleries()
    }

    private fun initializeGalleries(){
        galleriesViewModel.galleries.observe(viewLifecycleOwner, Observer { galleries ->
            if (!galleries.isNullOrEmpty()) {

                Log.d("observer", "Home Fragment observed Galleries")
                storiesPagerAdapter =
                    StoriesPagerAdapter(
                        this,
                        galleries
                    )

                view_pager_stories.adapter =
                    storiesPagerAdapter

//                view_pager_stories.registerOnPageChangeCallback(object :
//                    ViewPager2.OnPageChangeCallback() {
//                    override fun onPageSelected(position: Int) {
//                        galleriesViewModel.setDraggable(true)
//                        super.onPageSelected(position)
//                    }
//                })
                //startPreCaching(dataList)
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
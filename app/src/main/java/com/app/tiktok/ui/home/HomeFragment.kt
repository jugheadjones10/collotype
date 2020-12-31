package com.app.tiktok.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.app.tiktok.R
import com.app.tiktok.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class  HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val galleriesViewModel by activityViewModels<GalleriesViewModel>()

    companion object{
        var viewPager2: ViewPager2? = null
        var storiesPagerAdapter: StoriesPagerAdapter? = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager2 = view_pager_stories
        viewPager2?.setPageTransformer(
            VerticalCubeTransformer()
        )
        viewPager2?.offscreenPageLimit = 3

        initializeGalleries()
    }

    private fun initializeGalleries(){
        galleriesViewModel.galleries.observe(viewLifecycleOwner, Observer { galleries ->
            if (!galleries.isNullOrEmpty()) {
                storiesPagerAdapter =
                    StoriesPagerAdapter(
                        this,
                        galleries
                    )

                view_pager_stories.adapter =
                    storiesPagerAdapter
            }
        })
    }

}
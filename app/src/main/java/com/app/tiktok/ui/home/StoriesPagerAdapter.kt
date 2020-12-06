package com.app.tiktok.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.tiktok.model.Gallery
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.story.StoryBunchFragment
import com.app.tiktok.ui.story.StoryViewFragment

class StoriesPagerAdapter(fragment: Fragment, val dataList: MutableList<Gallery> = mutableListOf()) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryBunchFragment.newInstance(dataList[position], Integer.toString(position));
    }
}
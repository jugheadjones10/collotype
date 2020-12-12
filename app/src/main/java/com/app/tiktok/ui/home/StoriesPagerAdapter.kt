package com.app.tiktok.ui.home

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.app.tiktok.model.Gallery
import com.app.tiktok.ui.story.StoryBunchFragment

class StoriesPagerAdapter(fragment: Fragment, val dataList: MutableList<Gallery> = mutableListOf()) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
//        return BlankFragment.newInstance()
        return StoryBunchFragment.newInstance(dataList[position], Integer.toString(position));
    }
}
package com.app.tiktok.ui.home

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.app.tiktok.model.Gallery
import com.app.tiktok.ui.ad.AdFragment
import com.app.tiktok.ui.rivallive.RivalLiveFragment
import com.app.tiktok.ui.story.StoryBunchFragment
import com.app.tiktok.utils.Constants

class StoriesPagerAdapter(fragment: Fragment, val dataList: MutableList<Gallery> = mutableListOf()) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        if(dataList[position].type.equals(Constants.TYPE_GALLERY)){
            return StoryBunchFragment.newInstance(dataList[position], Integer.toString(position));
        }else if(dataList[position].type.equals(Constants.TYPE_RIVAL_LIVE)){
            return RivalLiveFragment.newInstance(dataList[position], Integer.toString(position));
        }else{
            return AdFragment.newInstance(dataList[position], Integer.toString(position));
        }
    }
}
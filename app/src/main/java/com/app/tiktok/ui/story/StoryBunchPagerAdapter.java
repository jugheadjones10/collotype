package com.app.tiktok.ui.story;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.StoriesDataModel;

import java.util.List;

public class StoryBunchPagerAdapter extends FragmentStateAdapter {

    private List<StoriesDataModel> mPostData;

    public StoryBunchPagerAdapter(Fragment storyBunchFragment, List<StoriesDataModel> mPostData){
        super(storyBunchFragment);
        this.mPostData = mPostData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StoryViewFragment.Companion.newInstance(mPostData.get(position), mPostData.get(0));
    }

    @Override
    public int getItemCount() {
        return mPostData.size();
    }
}

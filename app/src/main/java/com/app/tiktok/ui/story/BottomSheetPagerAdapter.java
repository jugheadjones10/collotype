package com.app.tiktok.ui.story;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.StoriesDataModel;

import java.util.List;

class BottomSheetPagerAdapter extends FragmentStateAdapter {

    private StoriesDataModel parentPost;

    public BottomSheetPagerAdapter(Fragment fragment, StoriesDataModel parentPost){
        super(fragment);
        this.parentPost = parentPost;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
//                return BlankFragment.newInstance();
                return AllPostsFragment.newInstance(parentPost);
            case 1:
                return AllGoalsFragment.newInstance(parentPost);
            case 2:
                return BlankFragment.newInstance();
            case 3:
                return BlankFragment.newInstance();
            case 4:
                return BlankFragment.newInstance();
            default:
                return BlankFragment.newInstance();
                // code block
        }
    }


    @Override
    public int getItemCount() {
        return 5;
    }
}

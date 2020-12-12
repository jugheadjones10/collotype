package com.app.tiktok.ui.story;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.StoriesDataModel;

import java.util.List;

class BottomSheetPagerAdapter extends FragmentStateAdapter {

    private Gallery gallery;
    private String galleryPosition;

    public BottomSheetPagerAdapter(Fragment fragment, String galleryPosition, Gallery gallery){
        super(fragment);
        this.galleryPosition = galleryPosition;
        this.gallery = gallery;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
                return AllPostsFragment.newInstance(galleryPosition);
//                return BlankFragment.newInstance();
            case 1:
//                return BlankFragment.newInstance();
            return AllGoalsFragment.newInstance(galleryPosition, gallery.getId());
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

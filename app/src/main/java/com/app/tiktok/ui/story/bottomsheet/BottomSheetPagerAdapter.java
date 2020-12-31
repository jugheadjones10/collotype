package com.app.tiktok.ui.story.bottomsheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.ui.story.BlankFragment;
import com.app.tiktok.ui.story.bottomsheet.goals.AllGoalsFragment;
import com.app.tiktok.ui.story.bottomsheet.posts.AllPostsFragment;

class BottomSheetPagerAdapter extends FragmentStateAdapter {

    private final Gallery gallery;
    private final String galleryPosition;

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
            case 1:
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

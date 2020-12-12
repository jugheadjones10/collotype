package com.app.tiktok.ui.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.ui.story.BlankFragment;
import com.app.tiktok.ui.user.events.UserEventsFragment;
import com.app.tiktok.ui.user.galleries.UserGalleriesFragment;
import com.app.tiktok.ui.user.posts.UserPostsFragment;

class UserPagerAdapter extends FragmentStateAdapter {

    private long userId;

    public UserPagerAdapter(Fragment fragment, long userId){
        super(fragment);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
                return UserPostsFragment.newInstance(userId);
            case 1:
                return UserGalleriesFragment.newInstance(userId);
            case 2:
                return UserEventsFragment.newInstance(userId);
            default:
                return BlankFragment.newInstance();
            // code block
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}
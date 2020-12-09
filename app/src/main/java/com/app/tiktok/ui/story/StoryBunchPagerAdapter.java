package com.app.tiktok.ui.story;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;

import java.util.List;

public class StoryBunchPagerAdapter extends FragmentStateAdapter {

    private List<Post> posts;
    private Gallery gallery;

    public StoryBunchPagerAdapter(Fragment storyBunchFragment, List<Post> posts, Gallery gallery){
        super(storyBunchFragment);
        this.posts = posts;
        this.gallery = gallery;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        return BlankFragment.newInstance();
        return StoryViewFragment.Companion.newInstance(posts.get(position), gallery);
    }

   @Override
    public int getItemCount() {
        return posts.size();
    }
}

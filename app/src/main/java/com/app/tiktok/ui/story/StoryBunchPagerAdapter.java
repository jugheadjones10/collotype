package com.app.tiktok.ui.story;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.ui.story.post.StoryViewFragment;

import java.util.List;

public class StoryBunchPagerAdapter extends FragmentStateAdapter {

    private final List<Post> posts;
    private final Gallery gallery;
    private final String galleryPosition;

    public StoryBunchPagerAdapter(Fragment storyBunchFragment, List<Post> posts, Gallery gallery, String galleryPosition){
        super(storyBunchFragment);
        this.posts = posts;
        this.gallery = gallery;
        this.galleryPosition = galleryPosition;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StoryViewFragment.Companion.newInstance(posts.get(position), gallery, galleryPosition);
    }

   @Override
    public int getItemCount() {
        return posts.size();
    }
}

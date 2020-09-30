package com.app.tiktok.ui.story;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentStoryBunchBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.utils.Constants;
import com.app.tiktok.utils.ExtensionsKt;
import com.app.tiktok.utils.ImageExtensionsKt;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryBunchFragment extends Fragment {

    private FragmentStoryBunchBinding binding;
    private StoryBunchViewModel viewModel;
    private StoryBunchPagerAdapter pagerAdapter;
    private LayoutManager layoutManager;

    private int positionInBigDaddyDataList;
    private StoriesDataModel parentPost;
    private List<StoriesDataModel> childrenPosts;

    public static StoryBunchFragment newInstance(StoriesDataModel storiesDataModel, int position) {
        StoryBunchFragment storyBunchFragment = new StoryBunchFragment();

        Bundle args = new Bundle();

        args.putParcelable(Constants.KEY_STORY_DATA, storiesDataModel);
        args.putInt(Constants.POSITION_INT, position);

        storyBunchFragment.setArguments(args);

        return storyBunchFragment;
    }

    // Listeners
    private final ViewPager2.OnPageChangeCallback viewPagerChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);


            //binding.thumbnailsRecyclerView.smoothScrollToPosition(position);
            //layoutManager.smoothScrollToPosition(binding.thumbnailsRecyclerView, layoutManager.onSaveInstanceState().);

            binding.thumbnailsRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(binding.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position) != null ) {
                        View view = binding.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                        view.callOnClick();
                    }
                }
            }, 50);


        }
    };

    public interface OnBottomItemClickListener{
        void onBottomItemClicked(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_story_bunch, container, false);

        //Get Arguments
        parentPost = getArguments().getParcelable(Constants.KEY_STORY_DATA);
        positionInBigDaddyDataList = getArguments().getInt(Constants.POSITION_INT);

        //Initialize View Model
        viewModel = new ViewModelProvider(this).get(StoryBunchViewModel.class);

        //Listen to clicks on thumbnail in recycler view
//        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer position) {
//                binding.postsViewPager.setCurrentItem(position);
//            }
//        });

        setChildrenPosts();

        initializeRecyclerView();
        initializeViewPager();
        setData();

        return binding.getRoot();
    }

    private void setChildrenPosts(){
        //Own self is added so it is displayed at the bottom
        childrenPosts = viewModel.getDataList(parentPost.getStoryId());
        childrenPosts.add(0, parentPost);
    }

    private void initializeRecyclerView(){
        layoutManager = new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.HORIZONTAL ,false);
        binding.thumbnailsRecyclerView.setLayoutManager(layoutManager);
        binding.thumbnailsRecyclerView.setAdapter(new BottomPostsAdapter(childrenPosts, getContext(), new OnBottomItemClickListener() {
            @Override
            public void onBottomItemClicked(int position) {
                binding.postsViewPager.setCurrentItem(position);
            }
        }));

        //This removes recyclerView blinking on selected item change
        binding.thumbnailsRecyclerView.getItemAnimator().setChangeDuration(0);
    }

    private void initializeViewPager(){
        //Pass in everything first. Later we may need to filter.
        pagerAdapter = new StoryBunchPagerAdapter(this, childrenPosts);

        binding.postsViewPager.setAdapter(pagerAdapter);
        binding.postsViewPager.registerOnPageChangeCallback(viewPagerChangeCallback);
    }

    private void setData(){
        binding.groupMemberOne.setImageResource(R.drawable.profile_scarlett);
        binding.groupMemberTwo.setImageResource(R.drawable.profile_victoria);
        binding.groupMemberThree.setImageResource(R.drawable.profile_chris);

        Glide.with(this)
                .load(parentPost.getStoryThumbUrl())
                .centerCrop()
                .into(binding.imageViewGroupPic);

        binding.groupName2.setText(parentPost.getGroupName());
        binding.followersCount2.setText(ExtensionsKt.formatNumberAsReadableFormat(parentPost.getFollowersCount()));
    }

}




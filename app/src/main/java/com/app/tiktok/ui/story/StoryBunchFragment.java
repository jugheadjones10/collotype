package com.app.tiktok.ui.story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.app.tiktok.ui.home.fragment.HomeFragment;
import com.app.tiktok.utils.Constants;
import com.app.tiktok.utils.ExtensionsKt;
import com.app.tiktok.utils.ImageExtensionsKt;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryBunchFragment extends Fragment {

    private FragmentStoryBunchBinding binding;
    private StoryBunchViewModel viewModel;
    private StoryBunchPagerAdapter pagerAdapter;
    private LayoutManager layoutManager;

    private StoriesDataModel parentPost;
    private List<StoriesDataModel> childrenPosts;

    public static StoryBunchFragment newInstance(StoriesDataModel storiesDataModel) {
        StoryBunchFragment storyBunchFragment = new StoryBunchFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_STORY_DATA, storiesDataModel);
        storyBunchFragment.setArguments(args);

        return storyBunchFragment;
    }

        // Listeners
    private final ViewPager2.OnPageChangeCallback viewPagerChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

//            binding.thumbnailsRecyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    binding.thumbnailsRecyclerView.smoothScrollToPosition(position);
//                }
//            }, 1_000);

            binding.thumbnailsRecyclerView.smoothScrollToPosition(position);
            //layoutManager.smoothScrollToPosition(binding.thumbnailsRecyclerView, layoutManager.onSaveInstanceState().);


            //This thing crashes if recycler view hasn't loaded yet/5555
            binding.thumbnailsRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if(binding.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position) != null ) {
                        View view = binding.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                        view.callOnClick();
//                    }
                }
            }, 50);
        }
    };

    private final OnBottomItemClickListener recyclerViewClickCallback = new OnBottomItemClickListener() {
        @Override
        public void onBottomItemClicked(int position) {
            binding.postsViewPager.setCurrentItem(position);
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

        //Initialize View Model
        viewModel = new ViewModelProvider(this).get(StoryBunchViewModel.class);

        //Set height of bottom view
        setBottomSheetHeight();

        setChildrenPosts();
        initializeRecyclerView();
        initializeViewPager();
        setData();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setBottomSheetHeight(){

        final ViewTreeObserver observer= binding.botSheet.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int botSheetHeight = binding.heightMeasurer.getHeight();
                Log.d("bot", " " + botSheetHeight);

                ViewGroup.LayoutParams layoutParams = binding.botSheet.getLayoutParams();
                layoutParams.height = botSheetHeight;
                binding.botSheet.setLayoutParams(layoutParams);

                ViewTreeObserver innerObserver = binding.botSheet.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setChildrenPosts(){
        //Own self is added so it is displayed at the bottom
        childrenPosts = viewModel.getDataList(parentPost.getStoryId());
        childrenPosts.add(0, parentPost);
    }

    private void initializeRecyclerView(){
        layoutManager = new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.HORIZONTAL ,false);
        binding.thumbnailsRecyclerView.setLayoutManager(layoutManager);
        binding.thumbnailsRecyclerView.setAdapter(new BottomPostsAdapter(childrenPosts, getContext(), recyclerViewClickCallback));

        //This removes recyclerView blinking on selected item change
        binding.thumbnailsRecyclerView.getItemAnimator().setChangeDuration(0);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.botSheet);
        bottomSheetBehavior.setGestureInsetBottomIgnored(true);

        binding.thumbnailsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(false);
                }else if(e.getAction() == MotionEvent.ACTION_UP){
                    if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                        HomeFragment.Companion.getViewPager2().setUserInputEnabled(true);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                binding.thumbnailsRecyclerView.setTranslationY(slideOffset * dpToPx(100));
                transitionBottomSheetBackgroundColor(slideOffset);
            }
        });
    }

    private void transitionBottomSheetBackgroundColor(float slideOffset) {
        int colorFrom = getResources().getColor(R.color.colorTransparent);
        int colorTo = getResources().getColor(R.color.colorBlack);
        binding.topBarBg.setBackgroundColor(interpolateColor(slideOffset,
                colorFrom, colorTo));
    }

    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    private int interpolateColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
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

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    private int dpToPx(int dp) {
        float density = getContext()
                .getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

}




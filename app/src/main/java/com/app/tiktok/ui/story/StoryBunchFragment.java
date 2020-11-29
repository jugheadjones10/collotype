package com.app.tiktok.ui.story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentStoryBunchBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.home.fragment.HomeFragment;
import com.app.tiktok.ui.home.fragment.HomeFragmentDirections;
import com.app.tiktok.ui.user.UserDataModel;
import com.app.tiktok.utils.Constants;
import com.app.tiktok.utils.ExtensionsKt;
import com.app.tiktok.utils.ImageExtensionsKt;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryBunchFragment extends Fragment {

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    public FragmentStoryBunchBinding binding;
    private StoryBunchViewModel viewModel;
    private StoryBunchPagerAdapter pagerAdapter;
    private LayoutManager layoutManager;

    public int bigSquareLength;

    public BottomSheetBehavior bottomSheetBehavior;
    private BottomPostsAdapter bottomPostsAdapter;
    private StoriesDataModel parentPost;
    private List<StoriesDataModel> childrenPosts;

    public static StoryBunchFragment newInstance(StoriesDataModel storiesDataModel) {
        StoryBunchFragment storyBunchFragment = new StoryBunchFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_STORY_DATA, storiesDataModel);
        storyBunchFragment.setArguments(args);

        return storyBunchFragment;
    }

    private static StoryBunchFragment instance;
    public static StoryBunchFragment getInstance(){
        return instance;
    }


    // Set bottom recycler view item as selected when view pager is swiped
    private final ViewPager2.OnPageChangeCallback viewPagerChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Log.d("infinite", "OnPageChangeCallback");
            binding.layoutBotSheet.thumbnailsRecyclerView.smoothScrollToPosition(position);

//            This thing crashes if recycler view hasn't loaded yet/5555
            binding.layoutBotSheet.thumbnailsRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //DOES THIS CHECK ACTUALLY HAVE ANY EFFECT
//                    if(binding.layoutBotSheet.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position) != null ) {
//                        View view = binding.layoutBotSheet.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position).itemView;
//                        view.callOnClick();
//                    }

                    if(binding.layoutBotSheet.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position) != null ) {
                        View view = binding.layoutBotSheet.thumbnailsRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                        bottomPostsAdapter.notifyItemChanged(bottomPostsAdapter.getSelectedPos());
                        bottomPostsAdapter.setSelectedPos(position);
                        bottomPostsAdapter.notifyItemChanged(position);
                    }
                }
            }, 50);
        }

    };

    //Set view pager post when bottom recycler view item is clicked
    private final OnBottomItemClickListener recyclerViewClickCallback = new OnBottomItemClickListener() {
        @Override
        public void onBottomItemClicked(int position) {
            binding.postsViewPager.setCurrentItem(position, false);
        }
    };

    public interface OnBottomItemClickListener{
        void onBottomItemClicked(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_story_bunch, container, false);

        Log.d("lifecyclecheck", "StoryBunchFragment onCreateView");
        instance = this;

        //Get Arguments
        parentPost = getArguments().getParcelable(Constants.KEY_STORY_DATA);

        //Initialize View Model
        viewModel = new ViewModelProvider(getActivity()).get(StoryBunchViewModel.class);

        //Set bottom sheet behaviour
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBotSheet.botSheet);

        binding.postsViewPager.setCameraDistance(1000000000000000000000000000f);
        binding.storyBunchParent.setCameraDistance(1000000000000000000000000000f);

        //Get data from view model
        setChildrenPosts();

        setHeights();

        initializeBottomSheetFragment();
        initializeViewPager();
        initializeNestedScrollViewBehaviour();
        injectDataIntoView();

        return binding.getRoot();
    }

    private void setChildrenPosts(){
        //Own self is added so it is displayed at the bottom
        childrenPosts = viewModel.getDataList(parentPost.getStoryId());

        List<StoriesDataModel> postsWithProcess = new ArrayList<>();
        List<StoriesDataModel> childrenPostsCopy = new ArrayList<>(childrenPosts);

        for(StoriesDataModel storiesDataModel : childrenPosts){
            if(storiesDataModel.getProcessPostIds().size() > 0){
                childrenPostsCopy.remove(storiesDataModel);
                postsWithProcess.add(storiesDataModel);
            }
        }

        List<StoriesDataModel> finalList = new ArrayList<StoriesDataModel>(postsWithProcess);
        finalList.addAll(childrenPostsCopy);

        finalList.add(0, parentPost);
        childrenPosts = finalList;
    }

    private void setHeights(){

        final ViewTreeObserver observer= binding.layoutBotSheet.botSheet.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("lag", "First global layout run");

                // I don't understand onGlobalLayout. What exactly does it listen for?
                int squareLength = binding.storyBunchParent.getWidth()/4;
                bigSquareLength = squareLength;

                setSquareDependentLengths(squareLength);

                ViewTreeObserver innerObserver = binding.layoutBotSheet.botSheet.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setSquareDependentLengths(int squareLength){
        //Set peek height with square length
        bottomSheetBehavior.setPeekHeight(squareLength);

        //Set view pager bottom margin with square length
        MarginLayoutParams viewPagerMarginParams = (MarginLayoutParams)binding.postsViewPager.getLayoutParams();
        viewPagerMarginParams.bottomMargin = squareLength;
        binding.postsViewPager.setLayoutParams(viewPagerMarginParams);

        ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
        recyclerViewLayoutParams.height = squareLength;
        binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);

        //Below are methods that need squareLength
        initializeRecyclerView(squareLength);
        initializeBottomSheetBehaviour(squareLength);
    }

    private void initializeBottomSheetFragment(){
        Fragment bottomSheetFragment = BottomSheetFragment.newInstance(parentPost);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.bot_sheet_fragment, bottomSheetFragment, "BottomSheetFragment");
        transaction.commit();
    }

    private void initializeRecyclerView(int squareLength){
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL ,false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = squareLength;
                lp.height = squareLength;

                return true;
            }
        };

        binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutManager(layoutManager);
        bottomPostsAdapter = new BottomPostsAdapter(childrenPosts, getContext(), recyclerViewClickCallback);
        binding.layoutBotSheet.thumbnailsRecyclerView.setAdapter(bottomPostsAdapter);

        //This removes recyclerView blinking on selected item change
        binding.layoutBotSheet.thumbnailsRecyclerView.getItemAnimator().setChangeDuration(0);

        binding.layoutBotSheet.thumbnailsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(false);

//                    if(getChildFragmentManager().findFragmentByTag("BottomSheetFragment") == null) {
//                        initializeBottomSheetFragment();
//                    }

                }else if(e.getAction() == MotionEvent.ACTION_UP){
                    //This clause prevents home fragment view pager 2 from getting disabled if user clicks on recycler view items
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
    }

    private void initializeBottomSheetBehaviour(int squareLength){
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(true);

                    ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
                    recyclerViewLayoutParams.height = squareLength;
                    binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);
                }else{
                    ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
                    recyclerViewLayoutParams.height = 0;
                    binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                int thirtyPx = Utility.INSTANCE.dpToPx(BottomSheetFragment.TAB_ITEM_WIDTH, getContext());
                int tabVerticalPadding = (squareLength - thirtyPx) / 2;

                binding.layoutBotSheet.botSheet.setVisibility(View.VISIBLE);

                TabLayout tabLayout = getChildFragmentManager().findFragmentById(R.id.bot_sheet_fragment).getView().findViewById(R.id.tab_layout);
                ViewGroup.LayoutParams tabLayoutParams = tabLayout.getLayoutParams();
                tabLayoutParams.height = (int) (thirtyPx * slideOffset);
                tabLayout.setLayoutParams(tabLayoutParams);

                //How come I don't need to set layout params again after specifying margins? Unlike in the setHeights method
                MarginLayoutParams tabMarginParams = (MarginLayoutParams) tabLayout.getLayoutParams();
                tabMarginParams.topMargin = (int) ((tabVerticalPadding + binding.topBarContainer.getHeight()) * slideOffset);
                tabMarginParams.bottomMargin = (int) (tabVerticalPadding * slideOffset);
                tabMarginParams.leftMargin = (int) (Utility.INSTANCE.dpToPx(50, getContext()) * slideOffset);
                tabMarginParams.rightMargin = (int) (Utility.INSTANCE.dpToPx(50, getContext()) * slideOffset);

                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    ViewGroup.LayoutParams tabViewLayoutParams = tabLayout.getTabAt(i).getCustomView().getLayoutParams();
                    tabViewLayoutParams.width = (int) (Utility.INSTANCE.dpToPx(BottomSheetFragment.TAB_ITEM_WIDTH, getContext()) * slideOffset);
                    tabViewLayoutParams.height = (int) (Utility.INSTANCE.dpToPx(BottomSheetFragment.TAB_ITEM_WIDTH, getContext()) * slideOffset);
                    tabLayout.getTabAt(i).getCustomView().setLayoutParams(tabViewLayoutParams);
                }

                tabLayout.setSelectedTabIndicatorHeight((int) (Utility.INSTANCE.dpToPx(BottomSheetFragment.TAB_ITEM_WIDTH, getContext()) * slideOffset));

                transitionBottomSheetBackgroundColor(slideOffset);
            }
        });
    }

    private void initializeViewPager(){
        Log.d("lag", "IN view pager intitaize");

        //Pass in everything first. Later we may need to filter.
        pagerAdapter = new StoryBunchPagerAdapter(this, childrenPosts);

        //binding.postsViewPager.setOffscreenPageLimit(1);
        binding.postsViewPager.setAdapter(pagerAdapter);
        binding.postsViewPager.registerOnPageChangeCallback(viewPagerChangeCallback);
    }

    private void initializeNestedScrollViewBehaviour(){
        viewModel.getDraggable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean draggable) {
                Log.d("observe", draggable + "");
                if(draggable != null){
                    bottomSheetBehavior.setDraggable(draggable);
                }
            }
        });
    }

    int i;
    private void injectDataIntoView(){
        Log.d("lag", "IN inject data into view");

        //Set data for member profile images
        LayoutInflater layoutInflator = getActivity().getLayoutInflater();

        if(parentPost.getMemberIds() != null){
            for (int j = 0; j < parentPost.getMemberIds().size(); j++) {
                UserDataModel userDataModel = viewModel.getUser(parentPost.getMemberIds().get(j));

                String profileUrl = userDataModel.getUserProfilePicUrl();
                ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, binding.groupMembers, false);
                Glide.with(this)
                        .load(profileUrl)
                        .thumbnail(0.25f)
                        .override(25,25)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(view);
                binding.groupMembers.addView(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                        HomeFragmentDirections.ActionNavigationHomeToUserFragment2 action =
                                HomeFragmentDirections.actionNavigationHomeToUserFragment2(userDataModel);
                        navController.navigate(action);
                    }
                });
            }
        }else{
//            for (i = 0; i < parentPost.getMemberIds().size(); i++) {
//
//                UserDataModel user = viewModel.getUser(parentPost.getMemberIds().get(i));
//
//                String profileUrl = user.getUserProfilePicUrl();
//                ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, binding.groupMembers, false);
//                Glide.with(this)
//                        .load(profileUrl)
//                        .thumbnail(0.25f)
//                        .override(25,25)
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .into(view);
//                binding.groupMembers.addView(view);
//            }
        }

        Glide.with(this)
                .load(parentPost.getStoryThumbUrl())
                .thumbnail(0.25f)
                .override(55,55)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imageViewGroupPic);

        binding.groupName2.setText(parentPost.getGroupName());
        binding.followersCount2.setText(ExtensionsKt.formatNumberAsReadableFormat(parentPost.getFollowersCount()));
    }

    private void transitionBottomSheetBackgroundColor(float slideOffset) {
        int colorFrom = getResources().getColor(R.color.colorTransparent);
        int colorTo = getResources().getColor(R.color.colorBlack);
        binding.layoutBotSheet.botSheet.setBackgroundColor(interpolateColor(slideOffset,
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

}




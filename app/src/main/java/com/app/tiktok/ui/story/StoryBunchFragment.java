package com.app.tiktok.ui.story;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentStoryBunchBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.galleryinfo.GalleryInfoFragment;
import com.app.tiktok.ui.home.GalleriesViewModel;
import com.app.tiktok.ui.home.HomeFragment;
import com.app.tiktok.ui.recommended.RecommendedFragment;
import com.app.tiktok.ui.story.bottomsheet.BottomSheetFragment;
import com.app.tiktok.ui.user.UserFragment;
import com.app.tiktok.utils.Constants;
import com.app.tiktok.utils.ExtensionsKt;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.concurrent.Executor;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoryBunchFragment extends Fragment implements BottomPostsAdapter.BottomItemClicked {

    float y1 = 0;
    float y2 = 0;
    float dy = 0;
    String direction;

    public FragmentStoryBunchBinding binding;

    private PostsViewModel postsViewModel;

    private NavController navController;
    private String position;
    private Gallery gallery;

    private StoryBunchPagerAdapter pagerAdapter;
    private LayoutManager layoutManager;

    public int squareLength;

    public BottomSheetBehavior bottomSheetBehavior;
    private BottomPostsAdapter bottomPostsAdapter;
    private List<StoriesDataModel> childrenPosts;

    public ConstraintLayout inflatedTopBar;

    public static StoryBunchFragment newInstance(Gallery gallery, String position) {
        StoryBunchFragment storyBunchFragment = new StoryBunchFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_STORY_DATA, gallery);
        args.putString(Constants.KEY_GALLERY_POSITION, position);
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

    @Override
    public void onBottomItemClicked(int clickPosition) {
        Log.d("recycled",  "Recycler ITEM has been CLICKED ! " + clickPosition);
        binding.postsViewPager.setCurrentItem(clickPosition, false);
    }

    public interface OnBottomItemClickListener{
        void onBottomItemClicked(int position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        squareLength = getResources().getDisplayMetrics().widthPixels/4;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_story_bunch, container, false);

        Log.d("lifecyclecheck", "StoryBunchFragment onCreateView");
        instance = this;

        //Get Arguments
        gallery = getArguments().getParcelable(Constants.KEY_STORY_DATA);
        position = getArguments().getString(Constants.KEY_GALLERY_POSITION);

        //Initialize navController
//        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        //Initialize View Model
        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);

        binding.postsViewPager.setCameraDistance(1000000000000000000000000000f);
        binding.storyBunchParent.setCameraDistance(1000000000000000000000000000f);

        //Get data from view model
        if(gallery.getId() != GalleriesViewModel.adPageId){
            getPosts();
            //Observe "draggable" from postsViewModel
            initializeNestedScrollViewBehaviour();
        }else{
            initializeAdvertisement();
        }

        return binding.getRoot();
    }

    private void initializeAdvertisement(){

        binding.icExploreImage.setVisibility(View.INVISIBLE);

        Glide.with(this)
                .load(gallery.getUrl())
                .thumbnail(0.25f)
                .override(250, 450)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.advert);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Set bottom sheet behaviour
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBotSheet.botSheet);
        bottomSheetBehavior.setGestureInsetBottomIgnored(true);

        if(gallery.getId() != GalleriesViewModel.adPageId){
            initializeTopBar();
            setBottomSheetHeights();
            initializeBottomSheetBehaviour();
            injectDataIntoView();
            initializeBottomSheetFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("grin", "StoryBunchFragment destroyed at position : " + position);
        binding.layoutBotSheet.thumbnailsRecyclerView.setAdapter(null);
    }

    private void getPosts(){
        //Own self is added so it is displayed at the bottom
        Executor executor = MyApp.Companion.getExecutorService();

        postsViewModel.getPosts(gallery.getId(), executor).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts != null){
                    Log.d("gruel", "View Pager getting initiazlied at  : " + position);
                    initializeRecyclerView(posts);
                    initializeViewPager(posts);
                }
            }
        });
    }

    private void initializeNestedScrollViewBehaviour(){
        postsViewModel.getDraggable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean draggable) {
                if(draggable != null){
                    Log.d("findingnemo", "Draggable property in StoryBunchFragment : " + draggable);
                    bottomSheetBehavior.setDraggable(draggable);
                }
            }
        });

        postsViewModel.getEnableInteractions().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enableInteractions) {
                if(enableInteractions != null){
                    Log.d("findingnemo", "Enable interactions property in StoryBunchFragment : " + enableInteractions);

                    Log.d("hey", getChildFragmentManager().getBackStackEntryCount() + "");
                    //TODO better way to get around the below hack? Prevent touch events on fragment from getting intercepted.
                    if(getChildFragmentManager().getBackStackEntryCount() == 0) {
                        HomeFragment.Companion.getViewPager2().setUserInputEnabled(enableInteractions);
                        postsViewModel.setDraggable(enableInteractions);
                    }
                }
            }
        });
    }

    private void initializeTopBar(){
        if(gallery.getCollab()){
            binding.topBarStub.getViewStub().setLayoutResource(R.layout.include_gallery_top_bar_collab);
            inflatedTopBar = (ConstraintLayout) binding.topBarStub.getViewStub().inflate();
        }else{
            binding.topBarStub.getViewStub().setLayoutResource(R.layout.include_gallery_top_bar);
            inflatedTopBar = (ConstraintLayout) binding.topBarStub.getViewStub().inflate();
        }
    }


    private void setBottomSheetHeights(){
        //Set peek height with square length
        bottomSheetBehavior.setPeekHeight(squareLength);

        //Set view pager bottom margin with square length
        MarginLayoutParams viewPagerMarginParams = (MarginLayoutParams)binding.postsViewPager.getLayoutParams();
        viewPagerMarginParams.bottomMargin = squareLength;
        binding.postsViewPager.setLayoutParams(viewPagerMarginParams);

        MarginLayoutParams icExploreMarginParams = (MarginLayoutParams)binding.icExploreImage.getLayoutParams();
        icExploreMarginParams.bottomMargin = squareLength + Utility.INSTANCE.dpToPx(15, requireContext());
        binding.icExploreImage.setLayoutParams(icExploreMarginParams);

        ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
        recyclerViewLayoutParams.height = squareLength;
        binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);
    }

    private void initializeBottomSheetBehaviour(){
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    Log.d("frog", "Bottom sheet sate collapsed");
                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(true);

                    ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
                    recyclerViewLayoutParams.height = squareLength;
                    binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);
                }else{
                    ViewGroup.LayoutParams recyclerViewLayoutParams = binding.layoutBotSheet.thumbnailsRecyclerView.getLayoutParams();
                    recyclerViewLayoutParams.height = 0;
                    binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutParams(recyclerViewLayoutParams);
//                    if(newState == BottomSheetBehavior.STATE_DRAGGING){
//
//                        postsViewModel.setExpanding(true);
//                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                int thirtyPx = Utility.INSTANCE.dpToPx(BottomSheetFragment.TAB_ITEM_WIDTH, getContext());
                int tabVerticalPadding = (squareLength - thirtyPx) / 2;

                binding.layoutBotSheet.botSheet.setVisibility(View.VISIBLE);

                if(getChildFragmentManager().findFragmentById(R.id.bot_sheet_fragment) != null) {
                    TabLayout tabLayout = getChildFragmentManager().findFragmentById(R.id.bot_sheet_fragment).getView().findViewById(R.id.tab_layout);
                    ViewGroup.LayoutParams tabLayoutParams = tabLayout.getLayoutParams();
                    tabLayoutParams.height = (int) (thirtyPx * slideOffset);
                    tabLayout.setLayoutParams(tabLayoutParams);


                    //How come I don't need to set layout params again after specifying margins? Unlike in the setHeights method
                    MarginLayoutParams tabMarginParams = (MarginLayoutParams) tabLayout.getLayoutParams();
                    tabMarginParams.topMargin = (int) ((tabVerticalPadding + inflatedTopBar.getHeight()) * slideOffset);
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
                }

                transitionBottomSheetBackgroundColor(slideOffset);
            }
        });
    }

    private void injectDataIntoView(){

        if(gallery.getCollab()){

            ShapeableImageView collaboratorOne = inflatedTopBar.findViewById(R.id.collaborator_one);
            ShapeableImageView collaboratorTwo = inflatedTopBar.findViewById(R.id.collaborator_two);

            postsViewModel.getCollaborators(gallery.getCollaborators()).observe(getViewLifecycleOwner(), new Observer<List<Gallery>>() {
                @Override
                public void onChanged(List<Gallery> galleries) {
                    if(galleries != null){

                        Glide.with(StoryBunchFragment.this)
                                .load(galleries.get(0).getUrl())
                                .thumbnail(0.25f)
                                .override(Utility.INSTANCE.dpToPx(55, requireContext()))
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(collaboratorOne);

                        Glide.with(StoryBunchFragment.this)
                                .load(galleries.get(1).getUrl())
                                .thumbnail(0.25f)
                                .override(Utility.INSTANCE.dpToPx(55, requireContext()))
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(collaboratorTwo);

                        TextView collaboratorOneName = inflatedTopBar.findViewById(R.id.collab_one_name);
                        TextView collaboratorTwoName = inflatedTopBar.findViewById(R.id.collab_two_name);
                        collaboratorOneName.setText(galleries.get(0).getName());
                        collaboratorTwoName.setText(galleries.get(1).getName());

                        if(!collaboratorOne.hasOnClickListeners()){
                            collaboratorOne.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToDetailedGallery(galleries.get(0));
                                }
                            });
                        }
                        if(!collaboratorTwo.hasOnClickListeners()){
                            collaboratorTwo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToDetailedGallery(galleries.get(1));
                                }
                            });
                        }
                    }
                }
            });


        }else{
            //Set data for member profile images
            LayoutInflater layoutInflator = getActivity().getLayoutInflater();

            postsViewModel.getMembers(gallery.getId()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> users) {
                    if(users != null){
                        LinearLayout groupMembers = inflatedTopBar.findViewById(R.id.group_members);

                        if(users != null){
                            for (User user: users) {
                                String profileUrl = user.getUrl();
                                ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, groupMembers, false);

                                Glide.with(StoryBunchFragment.this)
                                        .load(profileUrl)
                                        .thumbnail(0.25f)
                                        .override(Utility.INSTANCE.dpToPx(25, requireContext()))
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .into(view);

                                groupMembers.addView(view);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Fragment userFragment = UserFragment.newInstance(position, user);
                                        navigateToFragment(userFragment, R.id.user_fragment_container, "UserFragment");
                                    }
                                });
                            }
                        }
                    }
                }
            });

            ShapeableImageView galleryPic = inflatedTopBar.findViewById(R.id.image_view_group_pic);

            Glide.with(this)
                    .load(gallery.getUrl())
                    .thumbnail(0.25f)
                    .override(Utility.INSTANCE.dpToPx(55, requireContext()))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(galleryPic);

            if(!galleryPic.hasOnClickListeners()){
                galleryPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToDetailedGallery(gallery);
                    }
                });
            }

            TextView groupName = inflatedTopBar.findViewById(R.id.battle_title);
            groupName.setText(gallery.getName());
        }



        TextView followersCount = inflatedTopBar.findViewById(R.id.followers_count2);
        followersCount.setText(ExtensionsKt.formatNumberAsReadableFormat(gallery.getFollowersCount()));

        binding.icExploreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRecommended();
            }
        });

    }


    private void initializeBottomSheetFragment(){
        if(getChildFragmentManager().findFragmentByTag("BottomSheetFragment") == null) {
            Fragment bottomSheetFragment = BottomSheetFragment.newInstance(position, gallery);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.bot_sheet_fragment, bottomSheetFragment, "BottomSheetFragment");
            transaction.commit();
        }
    }


    private void initializeRecyclerView(List<Post> posts){
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL ,false);

        binding.layoutBotSheet.thumbnailsRecyclerView.setLayoutManager(layoutManager);

        bottomPostsAdapter = new BottomPostsAdapter(posts, getContext(), this);
        binding.layoutBotSheet.thumbnailsRecyclerView.setAdapter(bottomPostsAdapter);

        //This removes recyclerView blinking on selected item change
        binding.layoutBotSheet.thumbnailsRecyclerView.getItemAnimator().setChangeDuration(0);
        binding.layoutBotSheet.thumbnailsRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                if(holder.getItemViewType() == BottomPostsAdapter.NORMAL_ITEM){
                    BottomPostsAdapter.BottomPostViewHolder viewHolder = (BottomPostsAdapter.BottomPostViewHolder) holder;
                    Glide.with(StoryBunchFragment.this).clear(viewHolder.binding.bottomPostImage);
                }else if(holder.getItemViewType() == BottomPostsAdapter.ENLARGED_ITEM){
                    BottomPostsAdapter.EnlargedBottomPostViewHolder viewHolder = (BottomPostsAdapter.EnlargedBottomPostViewHolder) holder;
                    Glide.with(StoryBunchFragment.this).clear(viewHolder.binding.bottomPostImage);
                }
            }
        });
        binding.layoutBotSheet.thumbnailsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("findingnemo", "Bottom Recycler view DOWN ");

                    HomeFragment.Companion.getViewPager2().setUserInputEnabled(false);
                }else if(e.getAction() == MotionEvent.ACTION_UP){
                    Log.d("findingnemo", "Bottom Recycler view UP ");
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

    private void initializeViewPager(List<Post> posts){
        Log.d("lag", "IN view pager intitaize");
        
        new Thread(new Runnable() {
            public void run() {
                //Not sure if the asynchronous feature below is required.
                //Pass in everything first. Later we may need to filter.
                pagerAdapter = new StoryBunchPagerAdapter(StoryBunchFragment.this, posts, gallery, position);

                binding.postsViewPager.post(new Runnable() {
                    public void run() {
                        binding.postsViewPager.setAdapter(pagerAdapter);
                        binding.postsViewPager.registerOnPageChangeCallback(viewPagerChangeCallback);
                    }
                });
            }
        }).start();
    }

    private void goToDetailedGallery(Gallery gallery){
        if(!GalleriesViewModel.isGalleryForbidden(gallery.getId())) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("GalleryInfoFragment");
            if(fragment == null) {
                Fragment galleryInfoFragment = GalleryInfoFragment.newInstance(position, gallery, inflatedTopBar.getHeight());
                navigateToFragment(galleryInfoFragment, R.id.detailed_gallery_info, "GalleryInfoFragment");
            }
        }
    }

    private void goToRecommended(){
        Fragment fragment = getChildFragmentManager().findFragmentByTag("RecommendedFragment");
        if(fragment == null) {
            Fragment recommendedFragment = RecommendedFragment.newInstance(position, gallery, inflatedTopBar.getHeight());
            navigateToFragment(recommendedFragment, R.id.detailed_gallery_info, "RecommendedFragment");
        }
    }

    private void transitionBottomSheetBackgroundColor(float slideOffset) {
        int colorFrom = getResources().getColor(R.color.colorTransparent);
        int colorTo = getResources().getColor(R.color.colorBlack);
        binding.layoutBotSheet.botSheet.setBackgroundColor(interpolateColor(slideOffset, colorFrom, colorTo));
    }

    private void navigateToFragment(Fragment fragment, int fragmentHost, String fragmentTag){

        postsViewModel.setEnableInteractions(false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(fragmentHost, fragment, fragmentTag);

        //Do I need the below line?
        transaction.addToBackStack(null);
        transaction.commit();
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




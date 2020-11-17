package com.app.tiktok.ui.story;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutAllUserPostsBinding;
import com.app.tiktok.databinding.LayoutGoalsBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.databinding.LayoutUserHeaderBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.home.fragment.HomeFragment;
import com.app.tiktok.ui.home.fragment.HomeFragmentDirections;
import com.app.tiktok.ui.user.UserDataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;
import androidx.viewpager2.widget.ViewPager2;


import java.util.ArrayList;
import java.util.List;

class AllGoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<StoriesDataModel> postsList;
    private StoriesDataModel parentPost;
    private Context mContext;
    private int squareLength;
    private StoryBunchViewModel viewModel;
    private NavController navController;

    RecycledViewPool viewPool;

    public AllGoalsAdapter(Context mContext, int squareLength, List<StoriesDataModel> postsList, StoriesDataModel parentPost, StoryBunchViewModel viewModel, NavController navController){
        this.mContext = mContext;
        this.squareLength = squareLength;
        this.postsList = postsList;
        this.parentPost = parentPost;
        this.viewModel = viewModel;
        this.navController = navController;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutGoalsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_goals, parent,false);
        binding.goalsRecyclerView.setRecycledViewPool(viewPool);

        return new GoalsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GoalsViewHolder goalsViewHolder = (GoalsViewHolder)holder;
        StoriesDataModel parentProcessPost = postsList.get(position);

        for (int j = 0; j < parentPost.getMemberIds().size(); j++) {
            UserDataModel userDataModel = viewModel.getUser(parentPost.getMemberIds().get(j));

            String profileUrl = userDataModel.getUserProfilePicUrl();
            ShapeableImageView view = (ShapeableImageView)LayoutInflater.from(mContext).inflate(
                    R.layout.include_member_profile,
                    ((GoalsViewHolder) holder).binding.groupMembers,
                    false);

            Glide.with(mContext)
                    .load(profileUrl)
                    .thumbnail(0.25f)
                    .override(25,25)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);

            ((GoalsViewHolder) holder).binding.groupMembers.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragmentDirections.ActionNavigationHomeToUserFragment2 action =
                            HomeFragmentDirections.actionNavigationHomeToUserFragment2(userDataModel);
                    navController.navigate(action);
                }
            });
        }

//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL ,false){
//            @Override
//            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
//                // force height of viewHolder here, this will override layout_height from xml
//                lp.width = squareLength;
//                lp.height = squareLength;
//
//                return true;
//            }
//        };

        goalsViewHolder.binding.setCaption(parentProcessPost.getStoryDescription());
        goalsViewHolder.binding.setPeriod("2021.11.21 ~ 2021.12.27");
//        goalsViewHolder.binding.goalsRecyclerView.setLayoutManager(layoutManager);

        GestureListener gestureListener = new GestureListener(goalsViewHolder.binding.goalsRecyclerView);
        GestureDetectorCompat myGestureListener = new GestureDetectorCompat(mContext, gestureListener);

        goalsViewHolder.binding.goalsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGestureListener.onTouchEvent(event);
                return false;
            }
        });

        goalsViewHolder.binding.goalsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch(e.getAction()) {

                    case (MotionEvent.ACTION_DOWN):
                        break;
                    case (MotionEvent.ACTION_MOVE): {
                        break;
                    }
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
                        goalsViewHolder.binding.goalsRecyclerView.requestDisallowInterceptTouchEvent(false);
                        break;
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


        List<StoriesDataModel> processPosts = new ArrayList<>();

        List<Long> processPostIds = parentProcessPost.getProcessPostIds();
        Log.d("process", processPostIds.toString());

        for (int i = 0; i < processPostIds.size(); i++) {
            StoriesDataModel processPost = viewModel.getPost(processPostIds.get(i));

            processPosts.add(new StoriesDataModel(
                0,
                0,
                "",
                "",
                 new ArrayList<>(),
                 0L,
                  processPost.getStoryUrl(),
                 new ArrayList<>(),
                processPost.getStoryDescription(),
                0,
                0,
                null,
                null,
                null
            ));
        }

        Log.d("process", processPosts.toString());
        BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(processPosts, mContext, true, squareLength);
        goalsViewHolder.binding.goalsRecyclerView.setAdapter(bottomPostsAdapter);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{
        RecyclerView targetRecyclerView;

        GestureListener(RecyclerView targetRecyclerView){
            this.targetRecyclerView = targetRecyclerView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            targetRecyclerView.requestDisallowInterceptTouchEvent(true);
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(Math.abs(velocityY) > Math.abs(velocityX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(Math.abs(distanceY) > Math.abs(distanceX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder{

        LayoutGoalsBinding binding;

        public GoalsViewHolder(@NonNull LayoutGoalsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

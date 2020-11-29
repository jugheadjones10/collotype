package com.app.tiktok.ui.user;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutAllUserPostsBinding;
import com.app.tiktok.databinding.LayoutGoalsBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.databinding.LayoutUserHeaderBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.home.fragment.HomeFragmentDirections;
import com.app.tiktok.ui.story.BottomPostsAdapter;
import com.app.tiktok.ui.story.StoryBunchViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

class UserGalleriesAdapter extends RecyclerView.Adapter<UserGalleriesAdapter.UserGalleryViewHolder> {

    private List<DataItem> dataItems;
    private Context mContext;
    private int squareLength;
    private StoryBunchViewModel viewModel;
    private NavController navController;

    RecyclerView.RecycledViewPool viewPool;

    public UserGalleriesAdapter(Context mContext, int squareLength, List<DataItem> dataItems, StoryBunchViewModel viewModel, NavController navController){
        this.mContext = mContext;
        this.squareLength = squareLength;
        this.dataItems = dataItems;
        this.viewModel = viewModel;
        this.navController = navController;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public UserGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutUserGalleryBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_user_gallery, parent,false);
        binding.galleryRecyclerView.setRecycledViewPool(viewPool);
        return new UserGalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGalleryViewHolder viewHolder, int position) {

        UserGallery userGallery = (UserGallery)dataItems.get(position);


        viewHolder.binding.groupMembers.removeAllViews();
        for (int j = 0; j < userGallery.parentStory.getMemberIds().size(); j++) {
            UserDataModel userDataModel = viewModel.getUser(userGallery.parentStory.getMemberIds().get(j));

            LayoutInflater layoutInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            String profileUrl = userDataModel.getUserProfilePicUrl();
            ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, viewHolder.binding.groupMembers, false);
            Glide.with(mContext)
                    .load(profileUrl)
                    .thumbnail(0.25f)
                    .override(25,25)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
            viewHolder.binding.groupMembers.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeFragmentDirections.ActionNavigationHomeToUserFragment2 action =
                            HomeFragmentDirections.actionNavigationHomeToUserFragment2(userDataModel);
                    navController.navigate(action);
                }
            });
        }

        Glide.with(mContext)
                .load(userGallery.parentStory.getStoryThumbUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.imageViewGroupPic);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL ,false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = squareLength;
                lp.height = squareLength;

                return true;
            }
        };

        GestureListener gestureListener = new GestureListener(viewHolder.binding.galleryRecyclerView);
        GestureDetectorCompat myGestureListener = new GestureDetectorCompat(mContext, gestureListener);
        viewHolder.binding.galleryRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGestureListener.onTouchEvent(event);
                return false;
            }
        });

        viewHolder.binding.setGalleryData(userGallery.parentStory);
        viewHolder.binding.galleryRecyclerView.setLayoutManager(layoutManager);
        BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(userGallery.storiesDataModels, mContext, false, squareLength);
        viewHolder.binding.galleryRecyclerView.setAdapter(bottomPostsAdapter);
        viewHolder.binding.galleryRecyclerView.getItemAnimator().setChangeDuration(0);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    class UserGalleryViewHolder extends RecyclerView.ViewHolder{

        LayoutUserGalleryBinding binding;

        public UserGalleryViewHolder(@NonNull LayoutUserGalleryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{
        RecyclerView targetRecyclerView;

        GestureListener(RecyclerView targetRecyclerView){
            this.targetRecyclerView = targetRecyclerView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("mama", "onDown");
            targetRecyclerView.requestDisallowInterceptTouchEvent(true);
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("mama", "onFling");
            if(Math.abs(velocityY) > Math.abs(velocityX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("mama", "onScroll");
            if(Math.abs(distanceY) > Math.abs(distanceX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}

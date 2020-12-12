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
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutUserPostsBinding;
import com.app.tiktok.ui.galleryinfo.GalleryInfoRecyclerDataItem;

import java.util.List;

class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.UserPostsViewHolder> {

    private List<DataItem> dataItems;
    private Context mContext;
    private GalleryInfoRecyclerDataItem.GalleriesViewModel viewModel;
    private NavController navController;

    RecyclerView.RecycledViewPool viewPool;

    public UserPostsAdapter(Context mContext, List<DataItem> dataItems, GalleryInfoRecyclerDataItem.GalleriesViewModel viewModel, NavController navController){
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.viewModel = viewModel;
        this.navController = navController;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public UserPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutUserPostsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_user_posts, parent,false);
        binding.postRecyclerView.setRecycledViewPool(viewPool);
        return new UserPostsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostsViewHolder viewHolder, int position) {

        UserGallery userGallery = (UserGallery)dataItems.get(position);

        GestureListener gestureListener = new GestureListener(viewHolder.binding.postRecyclerView);
        GestureDetectorCompat myGestureListener = new GestureDetectorCompat(mContext, gestureListener);
        viewHolder.binding.postRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myGestureListener.onTouchEvent(event);
                return false;
            }
        });

//        CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
//        layoutManager.setMaxVisibleItems(2);
//        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
//        viewHolder.binding.postRecyclerView.addOnScrollListener(new CenterScrollListener());
//
//        viewHolder.binding.postRecyclerView.setLayoutManager(layoutManager);
//        UserPostAdapter userPostsAdapter = new UserPostAdapter(mContext, userGallery.storiesDataModels, userGallery.parentStory);
//        viewHolder.binding.postRecyclerView.scrollToPosition(userGallery.storiesDataModels.size()/2);
//        viewHolder.binding.postRecyclerView.setAdapter(userPostsAdapter);
//        viewHolder.binding.postRecyclerView.getItemAnimator().setChangeDuration(0);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    class UserPostsViewHolder extends RecyclerView.ViewHolder{

        LayoutUserPostsBinding binding;

        public UserPostsViewHolder(@NonNull LayoutUserPostsBinding binding) {
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

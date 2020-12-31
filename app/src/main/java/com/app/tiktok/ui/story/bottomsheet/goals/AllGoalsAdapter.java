package com.app.tiktok.ui.story.bottomsheet.goals;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutGoalsBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.story.PostsViewModel;
import com.app.tiktok.ui.story.BottomPostsAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;


import java.util.List;

class AllGoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<List<Post>> processPosts;
    private final List<User> members;
    private final Context mContext;
    private final int squareLength;
    private final PostsViewModel postsViewModel;

    RecycledViewPool viewPool;

    public AllGoalsAdapter(Context mContext, PostsViewModel postsViewModel, List<List<Post>> processPosts, List<User> members){
        this.mContext = mContext;
        this.squareLength = mContext.getResources().getDisplayMetrics().widthPixels/4;
        this.processPosts = processPosts;
        this.members = members;
        this.postsViewModel = postsViewModel;
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

        List<Post> processPostsRow = processPosts.get(position);

        for (User user: members) {

            String profileUrl = user.getUrl();
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

                }
            });
        }

        goalsViewHolder.binding.setCaption(processPostsRow.get(0).getProcessTitle());
        goalsViewHolder.binding.setPeriod("2021.11.21 ~ 2021.12.27");

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
                        postsViewModel.setDraggable(false);
                        break;
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_UP):
                        postsViewModel.setDraggable(true);

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


        BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(processPostsRow, mContext, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL ,false);
        goalsViewHolder.binding.goalsRecyclerView.setLayoutManager(layoutManager);
        goalsViewHolder.binding.goalsRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                if(holder.getItemViewType() == BottomPostsAdapter.NORMAL_ITEM){
                    BottomPostsAdapter.BottomPostViewHolder viewHolder = (BottomPostsAdapter.BottomPostViewHolder) holder;
                    Glide.with(mContext).clear(viewHolder.binding.bottomPostImage);
                }else{
                    BottomPostsAdapter.EnlargedBottomPostViewHolder viewHolder = (BottomPostsAdapter.EnlargedBottomPostViewHolder) holder;
                    Glide.with(mContext).clear(viewHolder.binding.bottomPostImage);
                }
            }
        });
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
        return processPosts.size();
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder{

        LayoutGoalsBinding binding;

        public GoalsViewHolder(@NonNull LayoutGoalsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

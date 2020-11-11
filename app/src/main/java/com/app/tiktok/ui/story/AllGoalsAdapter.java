package com.app.tiktok.ui.story;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutAllUserPostsBinding;
import com.app.tiktok.databinding.LayoutGoalsBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.databinding.LayoutUserHeaderBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.user.UserDataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;


import java.util.ArrayList;
import java.util.List;

class AllGoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<StoriesDataModel> postsList;
    private StoriesDataModel parentPost;
    private Context mContext;
    private int squareLength;
    private StoryBunchViewModel viewModel;

    RecycledViewPool viewPool;

    public AllGoalsAdapter(Context mContext, int squareLength, List<StoriesDataModel> postsList, StoriesDataModel parentPost, StoryBunchViewModel viewModel){
        this.mContext = mContext;
        this.squareLength = squareLength;
        this.postsList = postsList;
        this.parentPost = parentPost;
        this.viewModel = viewModel;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutGoalsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_goals, parent,false);
        //binding.goalsRecyclerView.setRecycledViewPool(viewPool);

        return new GoalsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GoalsViewHolder goalsViewHolder = (GoalsViewHolder)holder;

        StoriesDataModel parentProcessPost = postsList.get(position);

        UserDataModel userA = viewModel.getUser(parentPost.getMemberIds().get(0));
        UserDataModel userB = viewModel.getUser(parentPost.getMemberIds().get(1));

        Glide.with(mContext)
                .load(userA.getUserProfilePicUrl())
                .thumbnail(0.25f)
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(goalsViewHolder.binding.groupMemberOne);

        Glide.with(mContext)
                .load(userB.getUserProfilePicUrl())
                .thumbnail(0.25f)
                .override(150, 150)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(goalsViewHolder.binding.groupMemberTwo);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL ,false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = squareLength;
                lp.height = squareLength;

                return true;
            }
        };

        goalsViewHolder.binding.setCaption(parentProcessPost.getStoryDescription());
        goalsViewHolder.binding.setPeriod("2021.11.21 ~ 2021.12.27");
        goalsViewHolder.binding.goalsRecyclerView.setLayoutManager(layoutManager);

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
        BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(processPosts, mContext, true);
        goalsViewHolder.binding.goalsRecyclerView.setAdapter(bottomPostsAdapter);
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

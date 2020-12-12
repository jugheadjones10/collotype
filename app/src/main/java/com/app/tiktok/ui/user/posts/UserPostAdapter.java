package com.app.tiktok.ui.user.posts;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemUserPostBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {

    private List<StoriesDataModel> storiesDataModels;
    private Context mContext;
    private StoriesDataModel parentStory;
    private NavController navController;

    RecyclerView.RecycledViewPool viewPool;

    public UserPostAdapter(Context mContext, List<StoriesDataModel> storiesDataModels, StoriesDataModel parentStory){
        this.mContext = mContext;
        this.storiesDataModels = storiesDataModels;
        this.parentStory = parentStory;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserPostBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_post, parent,false);
        return new UserPostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder viewHolder, int position) {

        StoriesDataModel storiesDataModel = storiesDataModels.get(position);

        Glide.with(mContext)
                .load(storiesDataModel.getStoryUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.postImage);

        Glide.with(mContext)
                .load(parentStory.getStoryThumbUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.galleryProfileImage);

        viewHolder.binding.setGalleryData(parentStory);
        viewHolder.binding.setPostData(storiesDataModel);
    }


    @Override
    public int getItemCount() {
        return storiesDataModels.size();
    }

    class UserPostViewHolder extends RecyclerView.ViewHolder{

        ItemUserPostBinding binding;

        public UserPostViewHolder(@NonNull ItemUserPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
package com.app.tiktok.ui.user.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemUserPostAltBinding;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

class UserPostsAdapterAlt extends RecyclerView.Adapter<UserPostsAdapterAlt.UserPostViewHolder> {

    private List<UserPost> userPosts;
    private Context mContext;
    private NavController navController;

    RecyclerView.RecycledViewPool viewPool;

    public UserPostsAdapterAlt(Context mContext, List<UserPost> userPosts){
        this.mContext = mContext;
        this.userPosts = userPosts;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserPostAltBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_post_alt, parent,false);
        return new UserPostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder viewHolder, int position) {
        UserPost userPost = userPosts.get(position);
//        viewHolder.binding.setUserPost(userPost);

        String urlType = Utility.INSTANCE.extractS3URLfileType(userPost.getPost().getUrl());
        if(Utility.INSTANCE.isImage(urlType)){
            Glide.with(mContext)
                    .load(userPost.getPost().getUrl())
                    .thumbnail(0.25f)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.postImage);
        }else {

        }

        Glide.with(mContext)
                .load(userPost.getGallery().getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.galleryProfileImage);
    }


    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    class UserPostViewHolder extends RecyclerView.ViewHolder{

        ItemUserPostAltBinding binding;

        public UserPostViewHolder(@NonNull ItemUserPostAltBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
package com.app.tiktok.ui.user;

import android.app.Activity;
import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;

import com.app.tiktok.R;
import com.app.tiktok.databinding.LayoutAllUserPostsBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.databinding.LayoutUserHeaderBinding;
import com.app.tiktok.ui.home.fragment.HomeFragmentDirections;
import com.app.tiktok.ui.story.BottomPostsAdapter;
import com.app.tiktok.ui.story.StoryBunchViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private int ITEM_VIEW_TYPE_USER_HEADER = 0;
    private int ITEM_VIEW_TYPE_ALL_USER_POSTS = 1;
    private int ITEM_VIEW_TYPE_USER_GALLERY = 2;

    private List<DataItem> dataItems;
    private Context mContext;
    private int squareLength;
    private StoryBunchViewModel viewModel;
    private NavController navController;

    RecycledViewPool viewPool;

    public UserAdapter(Context mContext, int squareLength, List<DataItem> dataItems, StoryBunchViewModel viewModel, NavController navController){
        this.mContext = mContext;
        this.squareLength = squareLength;
        this.dataItems = dataItems;
        this.viewModel = viewModel;
        this.navController = navController;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("type", "what's the tyep : " + viewType);
        if(viewType == ITEM_VIEW_TYPE_USER_HEADER){
            LayoutUserHeaderBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_user_header, parent,false);
            return new UserHeaderViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_ALL_USER_POSTS){
            LayoutAllUserPostsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_all_user_posts, parent,false);
            binding.allUserPostsRecyclerView.setRecycledViewPool(viewPool);
            return new AllUserPostsViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_USER_GALLERY){
            LayoutUserGalleryBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_user_gallery, parent,false);
            binding.galleryRecyclerView.setRecycledViewPool(viewPool);
            return new UserGalleryViewHolder(binding);
        }else{
            throw new ClassCastException("Unknown viewType ${viewType}");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d("type", "what's the  position in bind : " + position);
        Log.d("type", "what's the  viewtype in bind : " + holder.getItemViewType());

        if(holder.getItemViewType() == ITEM_VIEW_TYPE_USER_HEADER){
            UserHeaderViewHolder viewHolder = (UserHeaderViewHolder)holder;
            UserHeader userHeader = (UserHeader)dataItems.get(position);

            Log.d("type", holder.getItemViewType() + userHeader.userDataModel.getUsername());

            viewHolder.binding.setUserData(userHeader.userDataModel);
            Glide.with(mContext)
                    .load(userHeader.userDataModel.getUserProfilePicUrl())
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.userProfile);

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_ALL_USER_POSTS){
            AllUserPostsViewHolder viewHolder = (AllUserPostsViewHolder)holder;
            AllUserPosts allUserPosts = (AllUserPosts)dataItems.get(position);

            Log.d("type", holder.getItemViewType() + allUserPosts.storiesDataModels.get(0).getGroupName());

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL ,false){
                @Override
                public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                    // force height of viewHolder here, this will override layout_height from xml
                    lp.width = squareLength;
                    lp.height = squareLength;

                    return true;
                }
            };

            viewHolder.binding.allUserPostsRecyclerView.setLayoutManager(layoutManager);
            BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(allUserPosts.storiesDataModels, mContext, null);
            viewHolder.binding.allUserPostsRecyclerView.setAdapter(bottomPostsAdapter);
            viewHolder.binding.allUserPostsRecyclerView.getItemAnimator().setChangeDuration(0);

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_USER_GALLERY){

            UserGalleryViewHolder viewHolder = (UserGalleryViewHolder)holder;
            UserGallery userGallery = (UserGallery)dataItems.get(position);

            Log.d("type", holder.getItemViewType() + userGallery.parentStory.getGroupName());

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

            viewHolder.binding.setGalleryData(userGallery.parentStory);

            viewHolder.binding.galleryRecyclerView.setLayoutManager(layoutManager);
            BottomPostsAdapter bottomPostsAdapter = new BottomPostsAdapter(userGallery.storiesDataModels, mContext, null);
            viewHolder.binding.galleryRecyclerView.setAdapter(bottomPostsAdapter);
            viewHolder.binding.galleryRecyclerView.getItemAnimator().setChangeDuration(0);
        }
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return ITEM_VIEW_TYPE_USER_HEADER;
        }else if(position == 1){
            return ITEM_VIEW_TYPE_ALL_USER_POSTS;
        }else{
            return ITEM_VIEW_TYPE_USER_GALLERY;
        }
    }

    public void setDataItems(final List<DataItem> dataItems) {
        if (this.dataItems == null) {
            this.dataItems = dataItems;
            notifyItemRangeInserted(0, dataItems.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return UserAdapter.this.dataItems.size();
                }

                @Override
                public int getNewListSize() {
                    return dataItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return UserAdapter.this.dataItems.get(oldItemPosition).id ==
                            dataItems.get(newItemPosition).id;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return UserAdapter.this.dataItems.get(oldItemPosition).id ==
                            dataItems.get(newItemPosition).id;
                }
            });

            this.dataItems = dataItems;
            notifyDataSetChanged();
            result.dispatchUpdatesTo(this);
        }
    }

    class UserHeaderViewHolder extends RecyclerView.ViewHolder{

        LayoutUserHeaderBinding binding;

        public UserHeaderViewHolder(@NonNull LayoutUserHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class AllUserPostsViewHolder extends RecyclerView.ViewHolder{

        LayoutAllUserPostsBinding binding;

        public AllUserPostsViewHolder(@NonNull LayoutAllUserPostsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class UserGalleryViewHolder extends RecyclerView.ViewHolder{

        LayoutUserGalleryBinding binding;

        public UserGalleryViewHolder(@NonNull LayoutUserGalleryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

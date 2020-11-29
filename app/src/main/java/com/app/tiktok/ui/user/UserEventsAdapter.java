package com.app.tiktok.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemUserEventBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.ui.home.fragment.HomeFragmentDirections;
import com.app.tiktok.ui.story.BottomPostsAdapter;
import com.app.tiktok.ui.story.StoryBunchViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

class UserEventsAdapter extends RecyclerView.Adapter<UserEventsAdapter.UserEventViewHolder>{

    private List<DataItem> dataItems;
    private Context mContext;
    private StoryBunchViewModel viewModel;
    private NavController navController;
    private long userId;

    RecyclerView.RecycledViewPool viewPool;

    public UserEventsAdapter(Context mContext, List<DataItem> dataItems, StoryBunchViewModel viewModel, NavController navController, long userId){
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.viewModel = viewModel;
        this.navController = navController;
        this.userId = userId;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public UserEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserEventBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_event, parent,false);
        return new UserEventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserEventViewHolder viewHolder, int position) {

        UserGallery userGallery = (UserGallery)dataItems.get(position);

        UserDataModel userDataModel = viewModel.getUser(userId);
        UserDataModel otherUserModel = viewModel.getUser(userGallery.parentStory.getMemberIds().get(0));
        List<UserDataModel> users = new ArrayList<>(List.of(userDataModel, otherUserModel));

        viewHolder.binding.groupMembers.removeAllViews();
        LayoutInflater layoutInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(UserDataModel user: users){
            String profileUrl = user.getUserProfilePicUrl();
            ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, viewHolder.binding.groupMembers, false);
            Glide.with(mContext)
                    .load(profileUrl)
                    .thumbnail(0.25f)
                    .override(25,25)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
            viewHolder.binding.groupMembers.addView(view);
        }

        Glide.with(mContext)
                .load(userGallery.parentStory.getStoryThumbUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.eventImage);
    }


    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    class UserEventViewHolder extends RecyclerView.ViewHolder{

        ItemUserEventBinding binding;

        public UserEventViewHolder(@NonNull ItemUserEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

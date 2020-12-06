package com.app.tiktok.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemUserEventBinding;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.story.UtilViewModel;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

class UserEventsAdapter extends RecyclerView.Adapter<UserEventsAdapter.UserEventViewHolder>{

    private List<UserGallery> userEvents;
    private Context mContext;
    private UtilViewModel utilViewModel;
    private NavController navController;
    private long userId;

    RecyclerView.RecycledViewPool viewPool;

    public UserEventsAdapter(Context mContext, List<UserGallery> userEvents, UtilViewModel utilViewModel, NavController navController, long userId){
        this.mContext = mContext;
        this.userEvents = userEvents;
        this.utilViewModel = utilViewModel;
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

        UserGallery userGallery = userEvents.get(position);

        User userDataModel = utilViewModel.getUser(userId);
        User otherUserModel = utilViewModel.getUser(userGallery.gallery.getMembers().get(0));
        List<User> users = new ArrayList<>(List.of(userDataModel, otherUserModel));

        viewHolder.binding.groupMembers.removeAllViews();
        LayoutInflater layoutInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(User user: users){
            String profileUrl = user.getUrl();
            ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, viewHolder.binding.groupMembers, false);
            Glide.with(mContext)
                    .load(profileUrl)
                    .thumbnail(0.25f)
                    .override(25,25)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
            viewHolder.binding.groupMembers.addView(view);
        }

        String urlType = Utility.INSTANCE.extractS3URLfileType(userGallery.gallery.getUrl());
        if(Utility.INSTANCE.isImage(urlType)){
            Glide.with(mContext)
                .load(userGallery.gallery.getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(viewHolder.binding.eventImage);
        }else {

        }
    }


    @Override
    public int getItemCount() {
        return userEvents.size();
    }

    class UserEventViewHolder extends RecyclerView.ViewHolder{

        ItemUserEventBinding binding;

        public UserEventViewHolder(@NonNull ItemUserEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

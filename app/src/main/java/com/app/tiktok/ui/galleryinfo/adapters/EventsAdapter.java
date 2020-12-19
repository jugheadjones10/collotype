package com.app.tiktok.ui.galleryinfo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemGalleryInfoEventBinding;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<HydratedEvent> events;
    private Context mContext;

    public EventsAdapter(Context mContext, List<HydratedEvent> events) {
        this.events = events;
        this.mContext = mContext;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        ItemGalleryInfoEventBinding binding;

        public EventViewHolder(ItemGalleryInfoEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGalleryInfoEventBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_gallery_info_event, parent, false);

        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        HydratedEvent event = events.get(position);

        if(position%2 == 0){
            holder.binding.eventReserveButton.setVisibility(View.GONE);
            holder.binding.eventWatchButton.setVisibility(View.VISIBLE);
        }else{
            holder.binding.eventReserveButton.setVisibility(View.VISIBLE);
            holder.binding.eventWatchButton.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(event.getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.binding.eventImage);
        holder.binding.groupMembers.removeAllViews();

        LayoutInflater layoutInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(User user: event.getHosts()){
            String profileUrl = user.getUrl();
            ShapeableImageView view = (ShapeableImageView)layoutInflator.inflate(R.layout.include_member_profile, holder.binding.groupMembers, false);
            Glide.with(mContext)
                    .load(profileUrl)
                    .thumbnail(0.25f)
                    .override(25,25)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view);
            holder.binding.groupMembers.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

}
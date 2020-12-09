package com.app.tiktok.ui.galleryinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemGalleryInfoMemberBinding;
import com.app.tiktok.databinding.ItemPastEventsBinding;
import com.app.tiktok.databinding.ItemProductsBinding;
import com.app.tiktok.databinding.ItemUpcomingEventsBinding;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.littlemango.stacklayoutmanager.StackLayoutManager;

import java.util.List;

class GalleryInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private int ITEM_VIEW_TYPE_MEMBERS = 0;
    private int ITEM_VIEW_TYPE_UPCOMING_EVENTS = 1;
    private int ITEM_VIEW_TYPE_PAST_EVENTS = 2;
    private int ITEM_VIEW_TYPE_PRODUCTS = 3;

    private List<GalleryInfoRecyclerDataItem> dataItems;
    private Context mContext;
    private NavController navController;

    public GalleryInfoAdapter(Context mContext, List<GalleryInfoRecyclerDataItem> dataItems, NavController navController){
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.navController = navController;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_MEMBERS) {
            ItemGalleryInfoMemberBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_gallery_info_member, parent,false);
            return new GalleryMemberViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_UPCOMING_EVENTS){
            ItemUpcomingEventsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_upcoming_events, parent,false);
            return new UpcomingEventsViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_PAST_EVENTS){
            ItemPastEventsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_past_events, parent,false);
            return new PastEventsViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_PRODUCTS){
            ItemProductsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_products, parent,false);
            return new GalleryProductsViewHolder(binding);

        }else{
            throw new ClassCastException("Unknown viewType ${viewType}");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == ITEM_VIEW_TYPE_MEMBERS) {

            GalleryMemberViewHolder viewHolder = (GalleryMemberViewHolder)holder;
            GalleryInfoMemberRow memberRow = (GalleryInfoMemberRow)dataItems.get(position);

            viewHolder.binding.username.setText(memberRow.user.getUsername());
            Glide.with(mContext)
                    .load(memberRow.user.getUrl())
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.memberImage);

            StackLayoutManager manager = new StackLayoutManager(StackLayoutManager.ScrollOrientation.LEFT_TO_RIGHT);
            manager.setPagerMode(false);
            manager.setItemOffset(45);
            manager.setItemChangedListener(new StackLayoutManager.ItemChangedListener() {
                @Override
                public void onItemChanged(int position) {
                    viewHolder.binding.galleryName.setText(memberRow.galleries.get(position).getName());
                }
            });
            viewHolder.binding.memberGalleriesRecyclerView.setLayoutManager(manager);
            viewHolder.binding.memberGalleriesRecyclerView.setAdapter(new MemberGalleryAdapter(mContext, memberRow.galleries));

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_UPCOMING_EVENTS){

            UpcomingEventsViewHolder viewHolder = (UpcomingEventsViewHolder)holder;
            GalleryInfoEventsRow eventsRow = (GalleryInfoEventsRow)dataItems.get(position);

            viewHolder.binding.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            viewHolder.binding.horizontalRecyclerView.setAdapter(new EventsAdapter(mContext, eventsRow.events));

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_PAST_EVENTS){

            PastEventsViewHolder viewHolder = (PastEventsViewHolder)holder;
            GalleryInfoEventsRow eventsRow = (GalleryInfoEventsRow)dataItems.get(position);

            viewHolder.binding.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            viewHolder.binding.horizontalRecyclerView.setAdapter(new EventsAdapter(mContext, eventsRow.events));

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_PRODUCTS){

            GalleryProductsViewHolder viewHolder = (GalleryProductsViewHolder)holder;
            GalleryInfoProductsRow productsRow = (GalleryInfoProductsRow)dataItems.get(position);

            viewHolder.binding.horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
            viewHolder.binding.horizontalRecyclerView.setAdapter(new ProductsAdapter(mContext, productsRow.products));

        }
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position < 2){
            return ITEM_VIEW_TYPE_MEMBERS;
        }else if(position == 2) {
            return ITEM_VIEW_TYPE_UPCOMING_EVENTS;
        }else if(position == 3){
            return ITEM_VIEW_TYPE_PAST_EVENTS;
        }else{
            return ITEM_VIEW_TYPE_PRODUCTS;
        }
    }

    class GalleryMemberViewHolder extends RecyclerView.ViewHolder{

        ItemGalleryInfoMemberBinding binding;

        public GalleryMemberViewHolder(@NonNull ItemGalleryInfoMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class UpcomingEventsViewHolder extends RecyclerView.ViewHolder{

        ItemUpcomingEventsBinding binding;

        public UpcomingEventsViewHolder(@NonNull ItemUpcomingEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class PastEventsViewHolder extends RecyclerView.ViewHolder{

        ItemPastEventsBinding binding;

        public PastEventsViewHolder(@NonNull ItemPastEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class GalleryProductsViewHolder extends RecyclerView.ViewHolder{

        ItemProductsBinding binding;

        public GalleryProductsViewHolder(@NonNull ItemProductsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
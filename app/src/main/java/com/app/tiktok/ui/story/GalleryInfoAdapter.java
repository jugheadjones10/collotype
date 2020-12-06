package com.app.tiktok.ui.story;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.GalleryInfoCurrentEventsBinding;
import com.app.tiktok.databinding.GalleryInfoPastEventsBinding;
import com.app.tiktok.databinding.ItemProcessPostBinding;
import com.app.tiktok.databinding.ItemProductBinding;
import com.app.tiktok.databinding.ItemUserEventBinding;
import com.app.tiktok.databinding.LayoutAllUserPostsBinding;
import com.app.tiktok.databinding.LayoutUserGalleryBinding;
import com.app.tiktok.databinding.LayoutUserHeaderBinding;
import com.app.tiktok.ui.user.DataItem;
import com.app.tiktok.ui.user.UserEvent;
import com.app.tiktok.ui.user.UserProduct;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

class GalleryInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private int ITEM_VIEW_TYPE_MEMBERS_INFO = 0;
    private int ITEM_VIEW_TYPE_UPCOMING_EVENTS_INFO = 1;
    private int ITEM_VIEW_TYPE_PAST_EVENTS_INFO = 2;
    private int ITEM_VIEW_TYPE_PRODUCTS_INFO = 3;

    private List<DataItem> dataItems;
    private Context mContext;
    private NavController navController;

    public GalleryInfoAdapter(Context mContext, List<DataItem> dataItems, NavController navController){
        this.mContext = mContext;
        this.dataItems = dataItems;
        this.navController = navController;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("type", "what's the tyep : " + viewType);
        if(viewType == ITEM_VIEW_TYPE_UPCOMING_EVENTS_INFO){
            GalleryInfoCurrentEventsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.gallery_info_current_events, parent,false);
            return new CurrentEventsViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_PAST_EVENTS_INFO){
            GalleryInfoPastEventsBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.gallery_info_past_events, parent,false);
            return new PastEventsViewHolder(binding);
        }else if(viewType == ITEM_VIEW_TYPE_PRODUCTS_INFO){
            ItemProductBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product, parent,false);
            return new UserProductViewHolder(binding);
        }else{
            throw new ClassCastException("Unknown viewType ${viewType}");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == ITEM_VIEW_TYPE_UPCOMING_EVENTS_INFO){
            CurrentEventsViewHolder viewHolder = (CurrentEventsViewHolder)holder;
            UserEvent eventOne = (UserEvent)dataItems.get(0);
            UserEvent eventTwo = (UserEvent)dataItems.get(1);

            Glide.with(mContext)
                    .load(eventOne.url)
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.eventOneImage);

            Glide.with(mContext)
                    .load(eventTwo.url)
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.eventTwoImage);

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_PAST_EVENTS_INFO){

            PastEventsViewHolder viewHolder = (PastEventsViewHolder)holder;
            UserEvent eventOne = (UserEvent)dataItems.get(2);
            UserEvent eventTwo = (UserEvent)dataItems.get(3);

            Glide.with(mContext)
                    .load(eventOne.url)
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.eventOneImage);

            Glide.with(mContext)
                    .load(eventTwo.url)
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.eventTwoImage);

        }else if(holder.getItemViewType() == ITEM_VIEW_TYPE_PRODUCTS_INFO){

            UserProductViewHolder viewHolder = (UserProductViewHolder)holder;
            UserProduct product;
            if(position <= dataItems.size() - 2){
                product = (UserProduct)dataItems.get(position + 2);
            }else{
                product = (UserProduct)dataItems.get(dataItems.size() - 1);
            }

            viewHolder.binding.setProduct(product);
            Glide.with(mContext)
                    .load(product.url)
                    .thumbnail(0.25f)
                    .override(150, 150)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return ITEM_VIEW_TYPE_UPCOMING_EVENTS_INFO;
        }else if(position == 1){
            return ITEM_VIEW_TYPE_PAST_EVENTS_INFO;
        }else{
            return ITEM_VIEW_TYPE_PRODUCTS_INFO;
        }
    }

    class CurrentEventsViewHolder extends RecyclerView.ViewHolder{

        GalleryInfoCurrentEventsBinding binding;

        public CurrentEventsViewHolder(@NonNull GalleryInfoCurrentEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class PastEventsViewHolder extends RecyclerView.ViewHolder{

        GalleryInfoPastEventsBinding binding;

        public PastEventsViewHolder(@NonNull GalleryInfoPastEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class UserProductViewHolder extends RecyclerView.ViewHolder{

        ItemProductBinding binding;

        public UserProductViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
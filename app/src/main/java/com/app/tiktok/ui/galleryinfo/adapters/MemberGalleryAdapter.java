package com.app.tiktok.ui.galleryinfo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.ItemMemberGalleryBinding;
import com.app.tiktok.model.Gallery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MemberGalleryAdapter extends RecyclerView.Adapter<MemberGalleryAdapter.MemberGalleryViewHolder> {

    private List<Gallery> galleries;
    private Context mContext;

    public MemberGalleryAdapter(Context mContext, List<Gallery> galleries) {
        this.galleries = galleries;
        this.mContext = mContext;
    }

    public class MemberGalleryViewHolder extends RecyclerView.ViewHolder {

        ItemMemberGalleryBinding binding;

        public MemberGalleryViewHolder(ItemMemberGalleryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public MemberGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMemberGalleryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_member_gallery, parent, false);

        return new MemberGalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberGalleryViewHolder holder, int position) {

        Gallery gallery = galleries.get(position);

        Glide.with(mContext)
                .load(gallery.getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.binding.galleryImage);
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }

}
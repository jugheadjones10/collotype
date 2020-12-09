package com.app.tiktok.ui.galleryinfo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.BottomPostItemBinding;
import com.app.tiktok.databinding.EnlargedBottomPostItemBinding;
import com.app.tiktok.databinding.ItemMemberGalleryBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.ui.story.BottomPostsAdapter;
import com.app.tiktok.ui.story.StoryBunchFragment;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

class MemberGalleryAdapter extends RecyclerView.Adapter<MemberGalleryAdapter.MemberGalleryViewHolder> {

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
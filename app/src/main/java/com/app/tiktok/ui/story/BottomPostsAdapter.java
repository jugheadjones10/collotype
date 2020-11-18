package com.app.tiktok.ui.story;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.BottomPostItemBinding;
import com.app.tiktok.databinding.EnlargedBottomPostItemBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import static com.app.tiktok.utils.ExtensionsKt.logError;

public class BottomPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<StoriesDataModel> storiesDataModels;
    private StoryBunchFragment.OnBottomItemClickListener itemClickListener;
    private Context mContext;

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    private int selectedPos = RecyclerView.NO_POSITION;

    private SimpleExoPlayer simplePlayer;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private SimpleCache simpleCache = MyApp.Companion.getSimpleCache();
    private boolean modifyFirst;
    private int squareLength = -1;

    private int NORMAL_ITEM = 0;
    private int ENLARGED_ITEM = 1;

    public BottomPostsAdapter(List<StoriesDataModel> storiesDataModels, Context mContext, StoryBunchFragment.OnBottomItemClickListener itemClickListener) {
        this.storiesDataModels = storiesDataModels;
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;
        modifyFirst = false;
    }

    public BottomPostsAdapter(List<StoriesDataModel> storiesDataModels, Context mContext, boolean modifyFirst, int squareLength) {
        this.storiesDataModels = storiesDataModels;
        this.mContext = mContext;
        this.itemClickListener = null;
        this.modifyFirst = modifyFirst;

        this.squareLength = squareLength;
    }

    public class BottomPostViewHolder extends RecyclerView.ViewHolder {

        BottomPostItemBinding binding;

        public BottomPostViewHolder(BottomPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class EnlargedBottomPostViewHolder extends RecyclerView.ViewHolder {

        EnlargedBottomPostItemBinding binding;

        public EnlargedBottomPostViewHolder(EnlargedBottomPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && modifyFirst){
            return ENLARGED_ITEM;
        }else{
            return NORMAL_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ENLARGED_ITEM){
            EnlargedBottomPostItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.enlarged_bottom_post_item, parent, false);

            return new EnlargedBottomPostViewHolder(binding);
        }else{
            BottomPostItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bottom_post_item, parent, false);

            return new BottomPostViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);
        String storyUrl = storiesDataModels.get(position).getStoryUrl();
        String storyType = Utility.INSTANCE.extractS3URLfileType(storyUrl);

        if(holder.getItemViewType() == ENLARGED_ITEM) {
            EnlargedBottomPostViewHolder viewHolder = (EnlargedBottomPostViewHolder) holder;

            if(storyType.equals("jpg") || storyType.equals("gif") || storyType.equals("png") || storyType.equals("jpeg")){

                //holder.binding.bottomPlayerViewStory.setVisibility(View.GONE);
                viewHolder.binding.bottomPostImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(storyUrl)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(viewHolder.binding.bottomPostImage);

            }else if(storyType.equals("mp4")){

//            holder.binding.bottomPlayerViewStory.setVisibility(View.VISIBLE);
//            holder.binding.bottomPostImage.setVisibility(View.GONE);
//
//            holder.binding.bottomPlayerViewStory.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//
//            SimpleExoPlayer simplePlayer = getPlayer();
//            holder.binding.bottomPlayerViewStory.setPlayer(simplePlayer);
//
//            prepareMedia(storyUrl);
            }

        }else{
            BottomPostViewHolder viewHolder = (BottomPostViewHolder) holder;

            Log.d("Goalsscroller", "modify first ran");
            if(squareLength > 0){
                ViewGroup.LayoutParams itemContainerLayoutParams = viewHolder.binding.bottomPostItemContainer.getLayoutParams();
                itemContainerLayoutParams.height = squareLength;
                itemContainerLayoutParams.width = squareLength;
                viewHolder.binding.bottomPostItemContainer.setLayoutParams(itemContainerLayoutParams);

                ViewGroup.MarginLayoutParams viewPagerMarginParams = (ViewGroup.MarginLayoutParams)viewHolder.binding.bottomPostItemContainer.getLayoutParams();
                viewPagerMarginParams.topMargin = Utility.INSTANCE.dpToPx(130, mContext) - squareLength;
                viewHolder.binding.bottomPostItemContainer.setLayoutParams(viewPagerMarginParams);
            }

            if(storyType.equals("jpg") || storyType.equals("gif") || storyType.equals("png") || storyType.equals("jpeg")){

                //holder.binding.bottomPlayerViewStory.setVisibility(View.GONE);
                viewHolder.binding.bottomPostImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(storyUrl)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(viewHolder.binding.bottomPostImage);

            }else if(storyType.equals("mp4")){

//            holder.binding.bottomPlayerViewStory.setVisibility(View.VISIBLE);
//            holder.binding.bottomPostImage.setVisibility(View.GONE);
//
//            holder.binding.bottomPlayerViewStory.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//
//            SimpleExoPlayer simplePlayer = getPlayer();
//            holder.binding.bottomPlayerViewStory.setPlayer(simplePlayer);
//
//            prepareMedia(storyUrl);
            }

        }

        if(itemClickListener != null){
            holder.itemView.setOnClickListener(view -> {
                notifyItemChanged(selectedPos);
                selectedPos = holder.getLayoutPosition();
                notifyItemChanged(selectedPos);

                itemClickListener.onBottomItemClicked(position);
            });
        }

    }

    @Override
    public int getItemCount() {
        return storiesDataModels.size();
    }

    //ExoPlayer stuff
    private Player.EventListener playerCallback = new Player.EventListener(){
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            //logError("onPlayerStateChanged playbackState: $playbackState");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Player.EventListener.super.onPlayerError(error);
        }
    };

    private void prepareMedia(String linkUrl){
        Uri uri = Uri.parse(linkUrl);

        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);

        simplePlayer.prepare(mediaSource, true, true);
        simplePlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        simplePlayer.setPlayWhenReady(true);

        simplePlayer.setVolume(0f);
        simplePlayer.addListener(playerCallback);
    }

    private SimpleExoPlayer getPlayer(){
        if (simplePlayer == null) {
            prepareVideoPlayer();
        }
        return simplePlayer;
    }

    private void prepareVideoPlayer() {
        simplePlayer = ExoPlayerFactory.newSimpleInstance(mContext);
        cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache,
            new DefaultHttpDataSourceFactory(
                    Util.getUserAgent(mContext,
                            "exo"))
        );
    }

}

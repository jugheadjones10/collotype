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
import com.app.tiktok.model.Post;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.story.StoryBunchFragment;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import static com.app.tiktok.utils.ExtensionsKt.logError;

public class BottomPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private StoryBunchFragment.OnBottomItemClickListener itemClickListener = null;
    private Context mContext;

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    private int selectedPos = RecyclerView.NO_POSITION;

    private boolean modifyFirst = false;
    private int squareLength = -1;

    protected SimpleExoPlayer player;

    public static final int NORMAL_ITEM = 0;
    private static final int ENLARGED_ITEM = 1;

    public BottomPostsAdapter(List<Post> posts, Context mContext, StoryBunchFragment.OnBottomItemClickListener itemClickListener) {
        this.posts = posts;
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;
//        initVideoPlayer();
    }

    public BottomPostsAdapter(List<Post> posts, Context mContext, boolean modifyFirst) {
        this.posts = posts;
        this.mContext = mContext;
        this.modifyFirst = modifyFirst;
        this.squareLength = mContext.getResources().getDisplayMetrics().widthPixels/4;
//        initVideoPlayer();
    }

    private void initVideoPlayer(){
        //Initialize Video Player
        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(mContext);
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = new SimpleExoPlayer.Builder(mContext)
//                    .setTrackSelector(trackSelector)
                    .build();
            player.setVolume(0f);
            player.setPlayWhenReady(true);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        }
    }


    public class BottomPostViewHolder extends RecyclerView.ViewHolder {

        public BottomPostItemBinding binding;

        public BottomPostViewHolder(BottomPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.bottomPostItemContainer.setTag(this);
        }
    }

    public class EnlargedBottomPostViewHolder extends RecyclerView.ViewHolder {

        public EnlargedBottomPostItemBinding binding;

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

            ViewGroup.LayoutParams params = binding.bottomPostItemContainer.getLayoutParams();
            params.width = squareLength;
            params.height = squareLength;

            if(modifyFirst){
                ViewGroup.MarginLayoutParams viewPagerMarginParams = (ViewGroup.MarginLayoutParams)binding.bottomPostItemContainer.getLayoutParams();
                viewPagerMarginParams.topMargin = Utility.INSTANCE.dpToPx(130, mContext) - squareLength;
                binding.bottomPostItemContainer.setLayoutParams(viewPagerMarginParams);
            }

            return new BottomPostViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);

        String storyUrl = posts.get(position).getUrl();
        String storyType = Utility.INSTANCE.extractS3URLfileType(storyUrl);

        if(holder.getItemViewType() == ENLARGED_ITEM) {
            EnlargedBottomPostViewHolder viewHolder = (EnlargedBottomPostViewHolder) holder;

            //May need to add support for video for enlarged bottom posts.
            if(Utility.INSTANCE.isImage(storyType)){
                //holder.binding.bottomPlayerViewStory.setVisibility(View.GONE);
                viewHolder.binding.bottomPostImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(storyUrl)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(viewHolder.binding.bottomPostImage);
            }

        }else{
            BottomPostViewHolder viewHolder = (BottomPostViewHolder) holder;

            if(Utility.INSTANCE.isImage(storyType)){

                viewHolder.binding.bottomPlayerViewStory.setVisibility(View.GONE);
                viewHolder.binding.bottomPostImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(storyUrl)
                        .thumbnail(0.25f)
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(viewHolder.binding.bottomPostImage);
            }else{

                viewHolder.binding.bottomPlayerViewStory.setVisibility(View.VISIBLE);
                viewHolder.binding.bottomPostImage.setVisibility(View.GONE);

//                viewHolder.binding.bottomPlayerViewStory.setPlayer(player);
//
//                MediaItem mediaItem = MediaItem.fromUri(storyUrl);
//
//                player.setMediaItem(mediaItem);
//                player.prepare();
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

//    @Override
//    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView);
//        player.release();
//        player = null;
//    }

    //Clear player when StoryBunchFragment is paused
//    @Override
//    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
//        super.onViewRecycled(holder);
//        ((BottomPostViewHolder)holder).binding.bottomPlayerViewStory.
//    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}

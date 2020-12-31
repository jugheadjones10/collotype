package com.app.tiktok.ui.story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.databinding.BottomPostItemBinding;
import com.app.tiktok.databinding.BottomPostVideoItemBinding;
import com.app.tiktok.databinding.EnlargedBottomPostItemBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.List;

public class BottomPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Post> posts;
    private final Context mContext;

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    private int selectedPos = RecyclerView.NO_POSITION;

    private boolean modifyFirst = false;
    private final int squareLength;

//    protected SimpleExoPlayer player;
    private BottomItemClicked bottomItemClicked;

    public static final int NORMAL_ITEM = 0;
    public static final int ENLARGED_ITEM = 1;
    public static final int VIDEO_ITEM = 2;

    public BottomPostsAdapter(List<Post> posts, Context mContext, BottomItemClicked bottomItemClicked) {
        this.posts = posts;
        this.mContext = mContext;
        this.bottomItemClicked = bottomItemClicked;
        this.squareLength = mContext.getResources().getDisplayMetrics().widthPixels/4;
        //initVideoPlayer();
    }

    public BottomPostsAdapter(List<Post> posts, Context mContext, boolean modifyFirst) {
        this.posts = posts;
        this.mContext = mContext;
        this.modifyFirst = modifyFirst;
        this.squareLength = mContext.getResources().getDisplayMetrics().widthPixels/4;
        //initVideoPlayer();
    }


    public class BottomPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BottomPostItemBinding binding;
        private BottomItemClicked bottomItemClicked;

        public BottomPostViewHolder(BottomPostItemBinding binding, BottomItemClicked bottomItemClicked) {
            super(binding.getRoot());
            this.binding = binding;
            if(bottomItemClicked != null){
                this.bottomItemClicked = bottomItemClicked;
                binding.bottomPostItemContainer.setOnClickListener(this);
            }
            this.binding.bottomPostItemContainer.setTag(this);
        }

        @Override
        public void onClick(View v) {
            int oldPos = selectedPos;
            selectedPos = getLayoutPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);

            bottomItemClicked.onBottomItemClicked(getBindingAdapterPosition());
        }
    }

    public class BottomPostVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public BottomPostVideoItemBinding binding;
        private BottomItemClicked bottomItemClicked;
        public SimpleExoPlayer player;
        public Long playbackPosition;

        public BottomPostVideoHolder(BottomPostVideoItemBinding binding, BottomItemClicked bottomItemClicked) {
            super(binding.getRoot());
            this.binding = binding;
            if(bottomItemClicked != null){
                this.bottomItemClicked = bottomItemClicked;
                binding.bottomPostItemContainer.setOnClickListener(this);
            }
            this.binding.bottomPostItemContainer.setTag(this);
            initVideoPlayer();
        }

        @Override
        public void onClick(View v) {
            int oldPos = selectedPos;
            selectedPos = getLayoutPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);

            bottomItemClicked.onBottomItemClicked(getBindingAdapterPosition());
        }

        private void initVideoPlayer(){
            //Initialize Video Player
            if (player == null) {
                player = new SimpleExoPlayer.Builder(mContext)
                        .build();
                player.setVolume(0f);
                player.setPlayWhenReady(true);
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            }
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
        String storyUrl = posts.get(position).getUrl();
        String storyType = Utility.INSTANCE.extractS3URLfileType(storyUrl);

        if(position == 0 && modifyFirst){
            return ENLARGED_ITEM;
        }else{
            if(Utility.INSTANCE.isImage(storyType)){
                return NORMAL_ITEM;
            }else{
                return VIDEO_ITEM;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ENLARGED_ITEM){
            EnlargedBottomPostItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.enlarged_bottom_post_item, parent, false);

            return new EnlargedBottomPostViewHolder(binding);
        }else {
            if(viewType == NORMAL_ITEM){
                BottomPostItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bottom_post_item, parent, false);

                ViewGroup.LayoutParams params = binding.bottomPostItemContainer.getLayoutParams();
                params.width = squareLength;
                params.height = squareLength;

                if(modifyFirst){
                    ViewGroup.MarginLayoutParams viewPagerMarginParams = (ViewGroup.MarginLayoutParams)binding.bottomPostItemContainer.getLayoutParams();
                    viewPagerMarginParams.topMargin = Utility.INSTANCE.dpToPx(130, mContext) - squareLength;
                    binding.bottomPostItemContainer.setLayoutParams(viewPagerMarginParams);
                }

                return new BottomPostViewHolder(binding, bottomItemClicked);
            }else{
                BottomPostVideoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bottom_post_video_item, parent, false);

                ViewGroup.LayoutParams params = binding.bottomPostItemContainer.getLayoutParams();
                params.width = squareLength;
                params.height = squareLength;

                if(modifyFirst){
                    ViewGroup.MarginLayoutParams viewPagerMarginParams = (ViewGroup.MarginLayoutParams)binding.bottomPostItemContainer.getLayoutParams();
                    viewPagerMarginParams.topMargin = Utility.INSTANCE.dpToPx(130, mContext) - squareLength;
                    binding.bottomPostItemContainer.setLayoutParams(viewPagerMarginParams);
                }

                return new BottomPostVideoHolder(binding, bottomItemClicked);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.itemView.setSelected(selectedPos == holder.getLayoutPosition());

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

        }else if(holder.getItemViewType() == NORMAL_ITEM){

            BottomPostViewHolder viewHolder = (BottomPostViewHolder) holder;

            Glide.with(mContext)
                    .load(storyUrl)
                    .thumbnail(0.25f)
                    .override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.binding.bottomPostImage);
        }else{

            BottomPostVideoHolder viewHolder = (BottomPostVideoHolder) holder;

            if(viewHolder.binding.bottomPlayerViewStory.getPlayer() == null){
                viewHolder.binding.bottomPlayerViewStory.setPlayer(viewHolder.player);
            }

            MediaItem mediaItem = MediaItem.fromUri(storyUrl);
            viewHolder.player.setMediaItem(mediaItem);

            if(viewHolder.playbackPosition != null){
                viewHolder.player.seekTo(viewHolder.playbackPosition);
            }

            viewHolder.player.prepare();
        }

    }


    //Clear player when StoryBunchFragment is paused
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.getItemViewType() == VIDEO_ITEM){
            //TODO not sure if saving of video playback state works correctly
            //Also, creating a player per view holder is probably not sustainable in the long term.
            BottomPostVideoHolder viewHolder = (BottomPostVideoHolder) holder;
            viewHolder.playbackPosition = viewHolder.player.getCurrentPosition();
            viewHolder.player.stop();
        }
    }



    public interface BottomItemClicked{
        void onBottomItemClicked(int clickPosition);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}

package com.app.tiktok.ui.story;

import android.content.Context;
import android.net.Uri;
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
import com.app.tiktok.model.StoriesDataModel;
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


//  {
//          "parentId": 4,
//          "storyId" : 2,
//          "storyUrl" : "https://dl.dropboxusercontent.com/s/www6v7432raa6jv/1-2.mp4",
//          "storyThumbUrl" : "https://i.ibb.co/Xy6KvBM/01.jpg",
//          "storyDescription" : "Tried drawing Iron Man... it was fun, but also tiring",
//          "userId" : 1,
//          "userProfilePicUrl" : "https://res.cloudinary.com/mydatacloud/image/upload/v1592823557/profiles/user_3_dzxh4b.jpg",
//          "userName" : "Sherlyn",
//          "likesCount" : 500,
//          "commentsCount" : 1000,
//
//          "groupName" : "Anime Club",
//          "followersCount" : 1235,
//          "membersThumbUrls" : [],
//          "sameGroupPostIds" : [2, 3, 4]
//          },
//{
//        "parentId": 4,
//        "storyId" : 3,
//        "storyUrl" : "https://dl.dropboxusercontent.com/s/sj29gg0fnz7t7jg/1-3.mp4",
//        "storyThumbUrl" : "https://i.ibb.co/Xy6KvBM/01.jpg",
//        "storyDescription" : "Tried drawing Iron Man... it was fun, but also tiring",
//        "userId" : 1,
//        "userProfilePicUrl" : "https://res.cloudinary.com/mydatacloud/image/upload/v1592823557/profiles/user_3_dzxh4b.jpg",
//        "userName" : "Sherlyn",
//        "likesCount" : 500,
//        "commentsCount" : 1000,
//
//        "groupName" : "Anime Club",
//        "followersCount" : 1235,
//        "membersThumbUrls" : [],
//        "sameGroupPostIds" : [2, 3, 4]
//        },
//        {
//        "parentId": 4,
//        "storyId" : 4,
//        "storyUrl" : "https://dl.dropboxusercontent.com/s/20dxz114jrp4ubt/1-4.mp4",
//        "storyThumbUrl" : "https://i.ibb.co/Xy6KvBM/01.jpg",
//        "storyDescription" : "Tried drawing Iron Man... it was fun, but also tiring",
//        "userId" : 1,
//        "userProfilePicUrl" : "https://res.cloudinary.com/mydatacloud/image/upload/v1592823557/profiles/user_3_dzxh4b.jpg",
//        "userName" : "Sherlyn",
//        "likesCount" : 500,
//        "commentsCount" : 1000,
//
//        "groupName" : "Anime Club",
//        "followersCount" : 1235,
//        "membersThumbUrls" : [],
//        "sameGroupPostIds" : [2, 3, 4]
//        },


class BottomPostsAdapter extends RecyclerView.Adapter<BottomPostsAdapter.BottomPostViewHolder> {

    private List<StoriesDataModel> storiesDataModels;
    private StoryBunchFragment.OnBottomItemClickListener itemClickListener;
    private Context mContext;

    private int selectedPos = RecyclerView.NO_POSITION;

    private SimpleExoPlayer simplePlayer;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private SimpleCache simpleCache = MyApp.Companion.getSimpleCache();

    public BottomPostsAdapter(List<StoriesDataModel> storiesDataModels, Context mContext, StoryBunchFragment.OnBottomItemClickListener itemClickListener) {
        this.storiesDataModels = storiesDataModels;
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;
    }

    public class BottomPostViewHolder extends RecyclerView.ViewHolder {

        BottomPostItemBinding binding;

        public BottomPostViewHolder(BottomPostItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public BottomPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BottomPostItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bottom_post_item, parent, false);

        return new BottomPostViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull BottomPostViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPos == position);


        String storyUrl = storiesDataModels.get(position).getStoryUrl();
        String storyType = storyUrl.substring(storyUrl.length() - 3);
        if(storyType.equals("jpg") || storyType.equals("gif")){

            holder.binding.bottomPlayerViewStory.setVisibility(View.GONE);
            holder.binding.bottomPostImage.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(storyUrl)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.bottomPostImage);

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

        holder.itemView.setOnClickListener(view -> {
            notifyItemChanged(selectedPos);
            selectedPos = holder.getLayoutPosition();
            notifyItemChanged(selectedPos);

            itemClickListener.onBottomItemClicked(position);
        });
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

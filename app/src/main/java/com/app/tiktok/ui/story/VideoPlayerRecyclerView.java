package com.app.tiktok.ui.story;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tiktok.R;
import com.app.tiktok.model.Post;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerRecyclerView  extends RecyclerView {

    private static final String TAG = "VideoPlayerRecyclerView";

    // ui
    private ImageView thumbnail, volumeControl;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;

    // vars
    private List<Post> posts = new ArrayList<>();

    private ArrayList<Integer> videoPositions = new ArrayList<>();

    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;

    private Context context;
    private int playPosition = -1;

    private boolean isVideoViewAdded;

    private RequestManager requestManager;


    public VideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        this.context = context.getApplicationContext();

        //Initialize Video View
        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        //Initialize Video Player
        if (videoPlayer == null) {
            videoPlayer = new SimpleExoPlayer.Builder(this.context)
                    .build();
        }

        // Bind the Video Player to the Video View
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(videoPlayer);
        videoPlayer.setVolume(0f);
        videoPlayer.setPlayWhenReady(true);
        videoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }


        });


    }

    public void playVideo() {

        int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

        List<Integer> newVideoPositions = new ArrayList<>();
        for(int i = startPosition; i <= endPosition; i++){
            View child = getChildAt(i);
            if(child != null){
                BottomPostsAdapter.BottomPostViewHolder holder = (BottomPostsAdapter.BottomPostViewHolder) child.getTag();
                if(holder.binding.bottomPlayerViewStory.getVisibility() == VISIBLE){
                    newVideoPositions.add(i);
                }
            }
        }

        Log.d("finger", "New video positions : " + newVideoPositions + "");

        for(int i : videoPositions){
            if(newVideoPositions.contains(i)){
                BottomPostsAdapter.BottomPostViewHolder holder = (BottomPostsAdapter.BottomPostViewHolder) getChildAt(i).getTag();
                if(holder.binding.bottomPlayerViewStory.getPlayer() != null){
                    newVideoPositions.remove(i);
                }
            }
        }

        Log.d("finger", "New video positions after removal : " + newVideoPositions + "");


        for(int i : newVideoPositions){
            BottomPostsAdapter.BottomPostViewHolder holder = (BottomPostsAdapter.BottomPostViewHolder) getChildAt(i).getTag();

            holder.binding.bottomPlayerViewStory.setPlayer(videoPlayer);

            MediaItem mediaItem = MediaItem.fromUri(posts.get(i).getUrl());
            videoPlayer.setMediaItem(mediaItem);
            videoPlayer.prepare();
        }

        videoPositions.addAll(newVideoPositions);

    }


    public void releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }

        viewHolderParent = null;
    }

    public void setPosts(List<Post> posts){
        this.posts = posts;
    }
}

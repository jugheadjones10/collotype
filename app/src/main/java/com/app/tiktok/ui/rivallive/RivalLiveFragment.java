package com.app.tiktok.ui.rivallive;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentRivalLiveBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedLiveGallery;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.main.MainActivity;
import com.app.tiktok.ui.story.PostsViewModel;
import com.app.tiktok.ui.story.StoryBunchFragment;
import com.app.tiktok.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.concurrent.Executor;

public class RivalLiveFragment extends Fragment {

    private Gallery gallery;
    private String position;
    private FragmentRivalLiveBinding binding;
    private PostsViewModel postsViewModel;

    protected SimpleExoPlayer playerTop;
    protected SimpleExoPlayer playerBottom;
    private long playbackPositionTop;
    private long playbackPositionBottom;

    private VolumeState volumeState = VolumeState.OFF;
    private enum  VolumeState {
        ON, OFF
    }
    public RivalLiveFragment() {

    }

    public static RivalLiveFragment newInstance(Gallery gallery, String position) {
        RivalLiveFragment rivalLiveFragment = new RivalLiveFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_STORY_DATA, gallery);
        args.putString(Constants.KEY_GALLERY_POSITION, position);
        rivalLiveFragment.setArguments(args);

        return rivalLiveFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            gallery = getArguments().getParcelable(Constants.KEY_STORY_DATA);
            position = getArguments().getString(Constants.KEY_GALLERY_POSITION);
        }
        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rival_live, container, false);
        binding.setContext(requireContext());
        binding.setToggleVolumeClickListener(new ToggleVolumeClickListener());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playerTop == null || playerBottom == null){
            initializePlayers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER); // Make to run your application only in portrait mode

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MainActivity.Companion.getBottomNavBar().setVisibility(View.GONE);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            MainActivity.Companion.getBottomNavBar().setVisibility(View.VISIBLE);
        }

        if(playerTop != null && playerBottom != null){
            toggleVolume();
            playerTop.setPlayWhenReady(true);
            playerBottom.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Make to run your application only in portrait mode

        if(playerTop != null && playerBottom != null){
            toggleVolume();
            playerTop.setPlayWhenReady(false);
            playerBottom.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayers(){
        if (playerTop == null || playerBottom == null) {
            playerTop = new SimpleExoPlayer.Builder(requireContext()).build();
            playerBottom = new SimpleExoPlayer.Builder(requireContext()).build();
            binding.liveStreamTop.setPlayer(playerTop);
            binding.liveStreamBottom.setPlayer(playerBottom);

            MediaItem mediaItemTop = MediaItem.fromUri(gallery.getRivalLiveUrls().get(0));
            playerTop.setMediaItem(mediaItemTop);

            MediaItem mediaItemBottom = MediaItem.fromUri(gallery.getRivalLiveUrls().get(1));
            playerBottom.setMediaItem(mediaItemBottom);

            playerTop.setPlayWhenReady(false);
            playerTop.setRepeatMode(Player.REPEAT_MODE_ONE);
            playerTop.seekTo(playbackPositionTop);
            playerTop.prepare();

            playerBottom.setPlayWhenReady(false);
            playerBottom.setRepeatMode(Player.REPEAT_MODE_ONE);
            playerBottom.seekTo(playbackPositionBottom);
            playerBottom.prepare();

            playerTop.setVolume(0f);
            playerBottom.setVolume(0f);
        }
    }

    private void releasePlayer() {
        if (playerTop != null ) {
            playbackPositionTop = playerTop.getCurrentPosition();
            playerTop.release();
            playerTop = null;
        }
        if(playerBottom != null){
            playbackPositionBottom = playerBottom.getCurrentPosition();
            playerBottom.release();
            playerBottom = null;
        }
    }

    private void getData(){
        Executor executor = MyApp.Companion.getExecutorService();
        postsViewModel.getHyrdratedLiveGallery(gallery, executor).observe(getViewLifecycleOwner(), new Observer<HydratedLiveGallery>() {
            @Override
            public void onChanged(HydratedLiveGallery gallery) {
                if(gallery != null){
                    binding.setHydratedLiveGallery(gallery);
                }
            }
        });
        postsViewModel.getRandomUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if(users != null){
                    binding.setUsersList(users);
                }
            }
        });
    }

    private class ToggleVolumeClickListener implements OnClickListener{
        @Override
        public void onClick(View v) {
           toggleVolume();
        }
    }

    private void toggleVolume(){
        if (playerTop != null) {
            if (volumeState == VolumeState.OFF) {
                setVolumeControl(VolumeState.ON);
            } else if (volumeState == VolumeState.ON) {
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state){
        volumeState = state;
        if (state == VolumeState.OFF) {
            playerTop.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            playerTop.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl(){
        binding.volumeControl.bringToFront();

        if(volumeState == VolumeState.OFF){
            Glide.with(this).load(R.drawable.ic_volume_off).into(binding.volumeControl);
        }else if(volumeState == VolumeState.ON){
            Glide.with(this).load(R.drawable.ic_volume_on).into(binding.volumeControl);
        }

        binding.volumeControl.animate().cancel();
        binding.volumeControl.setAlpha(1f);
        binding.volumeControl.animate()
                .alpha(0f)
                .setDuration(600).setStartDelay(1000);
    }

}
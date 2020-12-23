package com.app.tiktok.ui.ad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentAdBinding;
import com.app.tiktok.databinding.FragmentRivalLiveBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedLiveGallery;
import com.app.tiktok.ui.rivallive.RivalLiveFragment;
import com.app.tiktok.ui.story.PostsViewModel;
import com.app.tiktok.utils.Constants;
import com.google.android.exoplayer2.SimpleExoPlayer;

public class AdFragment extends Fragment {

    private Gallery gallery;
    private String position;
    private FragmentAdBinding binding;

    public AdFragment() {

    }

    public static AdFragment newInstance(Gallery gallery, String position) {
        AdFragment rivalLiveFragment = new AdFragment();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ad, container, false);
        binding.setContext(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setContext(requireContext());
        binding.setGallery(gallery);
    }

}
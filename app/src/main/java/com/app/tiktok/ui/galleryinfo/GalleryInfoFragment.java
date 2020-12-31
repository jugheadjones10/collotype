package com.app.tiktok.ui.galleryinfo;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentGalleryInfoBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.ui.story.PostsViewModel;

import java.util.List;

public class GalleryInfoFragment extends Fragment {

    private static final String POSITION_KEY = "POSITION_KEY";
    private static final String GALLERY_KEY = "GALLERY_KEY";
    private static final String TOP_BAR_HEIGHT_KEY = "TOP_BAR_HEIGHT_KEY";

    private String position;
    private Gallery gallery;
    private int topBarHeight;

    private PostsViewModel postsViewModel;

    private FragmentGalleryInfoBinding binding;

    public GalleryInfoFragment() {
        // Required empty public constructor
    }

    public static GalleryInfoFragment newInstance(String position, Gallery gallery, int topBarHeight) {
        GalleryInfoFragment fragment = new GalleryInfoFragment();
        Bundle args = new Bundle();
        args.putString(POSITION_KEY, position);
        args.putParcelable(GALLERY_KEY, gallery);
        args.putInt(TOP_BAR_HEIGHT_KEY, topBarHeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getString(POSITION_KEY);
            gallery = getArguments().getParcelable(GALLERY_KEY);
            topBarHeight = getArguments().getInt(TOP_BAR_HEIGHT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery_info, container, false);
        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
        getGalleryInfo();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTopMargin();
        interceptBackButton();
    }

    private void getGalleryInfo(){
        postsViewModel.getFakeGalleryInfoData(gallery).observe(getViewLifecycleOwner(), new Observer<List<GalleryInfoRecyclerDataItem>>() {
            @Override
            public void onChanged(List<GalleryInfoRecyclerDataItem> recyclerDataItems) {
                if(recyclerDataItems != null){
                    initializeRecyclerView(recyclerDataItems);
                }
            }
        });
    }

    private void setTopMargin(){
        ViewGroup.LayoutParams blackBgLayoutParams = binding.blackBg.getLayoutParams();
        blackBgLayoutParams.height = topBarHeight + 40;
        binding.blackBg.setLayoutParams(blackBgLayoutParams);
        binding.detailedGalleryInfoRecyclerView.invalidate();
    }

    private void interceptBackButton() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStackImmediate();
                postsViewModel.setEnableInteractions(true);
            }
        });
    }

    private void initializeRecyclerView(List<GalleryInfoRecyclerDataItem> finalList){
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        GalleryInfoAdapter galleryInfoAdapter = new GalleryInfoAdapter(getContext(), finalList, navController);
        binding.detailedGalleryInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.detailedGalleryInfoRecyclerView.setAdapter(galleryInfoAdapter);
    }
}
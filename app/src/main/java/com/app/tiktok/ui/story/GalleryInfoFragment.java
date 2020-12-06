package com.app.tiktok.ui.story;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentGalleryInfoBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.Post;
import com.app.tiktok.ui.user.DataItem;
import com.app.tiktok.ui.user.UserEvent;
import com.app.tiktok.ui.user.UserProduct;
import com.app.tiktok.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.addToBackStack(null);
//                transaction.commit();

                getActivity().getSupportFragmentManager().popBackStack();

//                getChildFragmentManager().popBackStack("GalleryInfoFragment");
//                        .remove((Fragment)GalleryInfoFragment.this).commit();

            }
        });
    }

    private void getGalleryInfo(){
        postsViewModel.getPosts(gallery.getId()).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts != null){
                    List<DataItem> finalList = new ArrayList<>();
                    for(int i = 0; i < posts.size(); i++){
                        if(i < 4){
                            UserEvent userEvent = new UserEvent("Water painting tutorial",
                                    "@5pm 09:24",
                                    postsViewModel.getMembers().getValue().subList(0,1),
                                    posts.get(i).getUrl());
                            finalList.add(userEvent);
                        }else{
                            UserProduct userProduct = new UserProduct(
                                    posts.get(i).getId(),
                                    "Ironman water painting",
                                    35L,
                                    posts.get(i).getUrl(),
                                    4288743308L
                            );
                            finalList.add(userProduct);
                        }
                    }
                    initializeRecyclerView(finalList);
                }
            }
        });
    }

    private void setTopMargin(){
//        ViewGroup.MarginLayoutParams tabMarginParams = (ViewGroup.MarginLayoutParams) binding.fragmentGalleryInfoContainer.getLayoutParams();
//        tabMarginParams.paddingTop = topBarHeight;

//        binding.fragmentGalleryInfoContainer.setPadding(0, topBarHeight + 20, 0, 0);

        ViewGroup.LayoutParams blackBgLayoutParams = binding.blackBg.getLayoutParams();
        blackBgLayoutParams.height = topBarHeight + 20;
        binding.blackBg.setLayoutParams(blackBgLayoutParams);
        
        binding.detailedGalleryInfoRecyclerView.invalidate();
    }


    private void initializeRecyclerView(List<DataItem> finalList){

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        GalleryInfoAdapter galleryInfoAdapter = new GalleryInfoAdapter(getContext(), finalList, navController);
        binding.detailedGalleryInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.detailedGalleryInfoRecyclerView.setAdapter(galleryInfoAdapter);
    }
}
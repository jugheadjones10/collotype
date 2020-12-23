package com.app.tiktok.ui.recommended;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.tiktok.R;
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentRecommendedBinding;
import com.app.tiktok.model.Gallery;
import com.app.tiktok.model.HydratedEvent;
import com.app.tiktok.model.Product;
import com.app.tiktok.ui.story.PostsViewModel;
import com.app.tiktok.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class RecommendedFragment extends Fragment implements RecommendedController.AdapterCallbacks {

    private static final String POSITION_KEY = "POSITION_KEY";
    private static final String GALLERY_KEY = "GALLERY_KEY";
    private static final String TOP_BAR_HEIGHT_KEY = "TOP_BAR_HEIGHT_KEY";

    private String position;
    private Gallery gallery;
    private int topBarHeight;

    private PostsViewModel postsViewModel;
    private FragmentRecommendedBinding binding;

    private RecommendedController controller;

    public RecommendedFragment() {

    }

    @BindingAdapter({"url", "context", "widthDp", "heightDp"})
    public static void loadImage(ShapeableImageView view, String url, Context context, int widthDp, int heightDp) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.25f)
                .override(Utility.INSTANCE.dpToPx(widthDp, context), Utility.INSTANCE.dpToPx(heightDp, context))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view);
    }

    @BindingAdapter({"url", "context", "widthDp", "heightDp"})
    public static void loadImage(ImageView view, String url, Context context, int widthDp, int heightDp) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.25f)
                .override(Utility.INSTANCE.dpToPx(widthDp, context), Utility.INSTANCE.dpToPx(heightDp, context))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view);
    }

    @BindingAdapter({"url", "context", "widthDp"})
    public static void loadImage(ShapeableImageView view, String url, Context context, int widthDp) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.25f)
                .override(Utility.INSTANCE.dpToPx(widthDp, context))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view);
    }

    @BindingAdapter({"url", "context", "widthDp"})
    public static void loadImage(ImageView view, String url, Context context, int widthDp) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.25f)
                .override(Utility.INSTANCE.dpToPx(widthDp, context))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view);
    }


    public static RecommendedFragment newInstance(String position, Gallery gallery, int topBarHeight) {
        RecommendedFragment fragment = new RecommendedFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommended, container, false);
        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRecyclerView();
        getRecommendedData();
        setTopMargin();
        interceptBackButton();
    }

    private void getRecommendedData(){
        Executor executor = MyApp.Companion.getExecutorService();
        postsViewModel.getRecommendedData(gallery, executor).observe(getViewLifecycleOwner(), new Observer<HashMap<String, List<?>>>() {
            @Override
            public void onChanged(HashMap<String, List<?>> recommendedData) {
                if(recommendedData != null){

                    Log.d("gotd", recommendedData.toString());
                    List<HydratedEvent> pastEvents = new ArrayList<>();
                    List<HydratedEvent> upcomingEvents = new ArrayList<>();
                    List<GalleryPost> beforeProductPosts = new ArrayList<>();
                    List<GalleryPost> afterProductPosts = new ArrayList<>();
                    List<Product> products = new ArrayList<>();

                    for (Map.Entry mapElement : recommendedData.entrySet()) {
                        if(mapElement.getKey().equals("Past Events")){
                            pastEvents = (List<HydratedEvent>)mapElement.getValue();
                        }else if(mapElement.getKey().equals("Upcoming Events")){
                            upcomingEvents = (List<HydratedEvent>)mapElement.getValue();
                        }else if(mapElement.getKey().equals("Before Product Posts")){
                            beforeProductPosts = (List<GalleryPost>)mapElement.getValue();
                        }else if(mapElement.getKey().equals("After Product Posts")){
                            afterProductPosts = (List<GalleryPost>)mapElement.getValue();
                        }else{
                            products = (List<Product>)mapElement.getValue();
                        }
                    }
                    updateController(pastEvents, upcomingEvents, beforeProductPosts, afterProductPosts, products);
                }
            }
        });
    }

    private void initializeRecyclerView(){
        controller = new RecommendedController(requireContext(), this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setController(controller);
    }

    private void updateController(List<HydratedEvent> pastEvents,
                                  List<HydratedEvent> upcomingEvents,
                                  List<GalleryPost> beforeProductPosts,
                                  List<GalleryPost> afterProductPosts,
                                  List<Product> products) {
        controller.setControllerData(pastEvents, upcomingEvents, beforeProductPosts, afterProductPosts, products);
    }

    public void onPastOrUpcomingClicked(String pastOrUpcoming){
        controller.setPastOrUpcoming(pastOrUpcoming);
    }

    private void setTopMargin(){
        ViewGroup.LayoutParams blackBgLayoutParams = binding.blackBg.getLayoutParams();
        blackBgLayoutParams.height = topBarHeight + 40;
        binding.blackBg.setLayoutParams(blackBgLayoutParams);

        //TEST IF YOU REALLY NEED THE BELOW LINE
        binding.recyclerView.invalidate();
    }

    private void interceptBackButton() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("GG", "gig");
                getParentFragmentManager().popBackStackImmediate();
                postsViewModel.setEnableInteractions(true);
            }
        });
    }

}
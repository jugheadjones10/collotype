package com.app.tiktok.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserPostsBinding;
import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.util.List;

public class UserPostsFragment extends Fragment {

    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserPostsBinding binding;
    private UserViewModel userViewModel;
    private NavController navController;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    public static UserPostsFragment newInstance(long userId){
        UserPostsFragment fragment = new UserPostsFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID_PARAM, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_posts, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(Long.toString(userId), UserViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeRecyclerView();
    }

    private void initializeRecyclerView(){

        userViewModel.getUserPosts(userId).observe(getViewLifecycleOwner(), new Observer<List<UserPost>>() {
            @Override
            public void onChanged(List<UserPost> userPosts) {
                if(userPosts != null){

                    GestureListener gestureListener = new GestureListener(binding.userPostsRecyclerView);
                    GestureDetectorCompat myGestureListener = new GestureDetectorCompat(getContext(), gestureListener);
                    binding.userPostsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            myGestureListener.onTouchEvent(event);
                            return false;
                        }
                    });

                    CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
                    layoutManager.setMaxVisibleItems(2);
                    layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
                    binding.userPostsRecyclerView.addOnScrollListener(new CenterScrollListener());

                    binding.userPostsRecyclerView.setLayoutManager(layoutManager);
                    UserPostsAdapterAlt userPostsAdapter = new UserPostsAdapterAlt(getContext(), userPosts);
                    binding.userPostsRecyclerView.scrollToPosition(userPosts.size()/2);
                    binding.userPostsRecyclerView.setAdapter(userPostsAdapter);
                }
            }
        });
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener{
        RecyclerView targetRecyclerView;

        GestureListener(RecyclerView targetRecyclerView){
            this.targetRecyclerView = targetRecyclerView;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("mama", "onDown");
            targetRecyclerView.requestDisallowInterceptTouchEvent(true);
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("mama", "onFling");
            if(Math.abs(velocityY) > Math.abs(velocityX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("mama", "onScroll");
            if(Math.abs(distanceY) > Math.abs(distanceX)){
                targetRecyclerView.requestDisallowInterceptTouchEvent(false);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
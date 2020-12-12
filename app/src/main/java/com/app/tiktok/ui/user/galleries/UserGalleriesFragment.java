package com.app.tiktok.ui.user.galleries;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserGalleriesBinding;
import com.app.tiktok.ui.user.UserViewModel;

import java.util.List;


public class UserGalleriesFragment extends Fragment {
    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserGalleriesBinding binding;
    private UserViewModel userViewModel;
    private NavController navController;

    public UserGalleriesFragment() {
        // Required empty public constructor
    }

    public static UserGalleriesFragment newInstance(long userId){
        UserGalleriesFragment fragment = new UserGalleriesFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_galleries, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(Long.toString(userId), UserViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        getUserGalleries();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        listenForWidth();
    }

    private void getUserGalleries(){
        userViewModel.getUserGalleries(userId).observe(getViewLifecycleOwner(), new Observer<List<UserGallery>>() {
            @Override
            public void onChanged(List<UserGallery> userGalleries) {
                if(userGalleries != null){
                    listenForWidth(userGalleries);
                }
            }
        });
    }

    private void listenForWidth(List<UserGallery> userGalleries){
        final ViewTreeObserver observer = binding.userGalleriesRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // I don't understand onGlobalLayout. What exactly does it listen for?
                int squareLength = binding.userGalleriesRecyclerView.getWidth()/4;

                initializeRecyclerView(squareLength, userGalleries);

                ViewTreeObserver innerObserver = binding.userGalleriesRecyclerView.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initializeRecyclerView(int squareLength, List<UserGallery> userGalleries){
        UserGalleriesAdapter userGalleriesAdapter = new UserGalleriesAdapter(getContext(), squareLength, userGalleries, navController);
        binding.userGalleriesRecyclerView.setAdapter(userGalleriesAdapter);
    }
}
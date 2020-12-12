package com.app.tiktok.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserEventsBinding;
import com.app.tiktok.ui.story.UtilViewModel;

import java.util.List;

public class UserEventsFragment extends Fragment {
    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserEventsBinding binding;
    private UserViewModel userViewModel;
    private UtilViewModel utilViewModel;
    private NavController navController;

    public UserEventsFragment() {

    }

    public static UserEventsFragment newInstance(long userId){
        UserEventsFragment fragment = new UserEventsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_events, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(Long.toString(userId), UserViewModel.class);
        utilViewModel = new ViewModelProvider(requireActivity()).get(UtilViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        getUserEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        initializeRecyclerView();
    }

    private void getUserEvents(){
        userViewModel.getUserGalleries(userId).observe(getViewLifecycleOwner(), new Observer<List<UserGallery>>() {
            @Override
            public void onChanged(List<UserGallery> userGalleries) {
                if(userGalleries != null){
                    initializeRecyclerView(userGalleries);
                }
            }
        });
    }

    private void initializeRecyclerView(List<UserGallery> userGalleries){

        UserEventsAdapter userEventsAdapter = new UserEventsAdapter(getContext(), userGalleries, utilViewModel, navController, userId);
        binding.userGalleriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.userGalleriesRecyclerView.setAdapter(userEventsAdapter);
    }
}
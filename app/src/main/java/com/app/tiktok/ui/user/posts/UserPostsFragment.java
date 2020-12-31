package com.app.tiktok.ui.user.posts;

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
import com.app.tiktok.app.MyApp;
import com.app.tiktok.databinding.FragmentUserPostsBinding;
import com.app.tiktok.ui.recommended.GalleryPost;
import com.app.tiktok.ui.user.UserViewModel;

import java.util.List;
import java.util.concurrent.Executor;

public class UserPostsFragment extends Fragment {

    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserPostsBinding binding;
    private UserViewModel userViewModel;
    private NavController navController;

    private PostsController controller;

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
        getUserPosts();
    }

    private void initializeRecyclerView(){
        controller = new PostsController(requireContext());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setController(controller);
    }

    private void getUserPosts(){
        Executor executor = MyApp.Companion.getExecutorService();
        userViewModel.getUserPosts(userId, executor).observe(getViewLifecycleOwner(), new Observer<List<GalleryPost>>() {
            @Override
            public void onChanged(List<GalleryPost> userPosts) {
                if(userPosts != null){

                    updateController(userPosts);
                }
            }
        });
    }

    private void updateController(List<GalleryPost> posts) {
        controller.setControllerData(posts);
    }
}
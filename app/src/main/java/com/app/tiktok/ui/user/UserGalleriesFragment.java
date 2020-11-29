package com.app.tiktok.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserGalleriesBinding;
import com.app.tiktok.databinding.FragmentUserGalleriesBindingImpl;
import com.app.tiktok.databinding.FragmentUserPostsBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.story.StoryBunchViewModel;

import java.util.ArrayList;
import java.util.List;


public class UserGalleriesFragment extends Fragment {
    private static final String USER_ID_PARAM = "USER_ID_PARAM";

    private long userId;
    private FragmentUserGalleriesBinding binding;
    private StoryBunchViewModel storyBunchViewModel;
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

        storyBunchViewModel = new ViewModelProvider(getActivity()).get(StoryBunchViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_galleries, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listenForWidth();
    }

    private void listenForWidth(){
        final ViewTreeObserver observer = binding.userGalleriesRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // I don't understand onGlobalLayout. What exactly does it listen for?
                int squareLength = binding.userGalleriesRecyclerView.getWidth()/4;

                initializeRecyclerView(squareLength);

                ViewTreeObserver innerObserver = binding.userGalleriesRecyclerView.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initializeRecyclerView(int squareLength){

        List<DataItem> dataItems = new ArrayList<>();

        UserDataModel userDataModel = storyBunchViewModel.getUser(userId);

        for(long galleryId : userDataModel.getGalleries()){
            //Adding user galleries to data items
            StoriesDataModel parentPost = userViewModel.getParentPostWithGhosts(galleryId);
            List<StoriesDataModel> children = userViewModel.getChildrenPosts(galleryId);
            dataItems.add(new UserGallery(parentPost, children));
        }

        UserGalleriesAdapter userGalleriesAdapter = new UserGalleriesAdapter(getContext(), squareLength, dataItems, storyBunchViewModel, navController);
        binding.userGalleriesRecyclerView.setAdapter(userGalleriesAdapter);
    }
}
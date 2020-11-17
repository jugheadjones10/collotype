package com.app.tiktok.ui.user;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.ui.story.StoryBunchFragment;
import com.app.tiktok.ui.story.StoryBunchViewModel;
import com.app.tiktok.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private StoryBunchViewModel storyBunchViewModel;
    private UserAdapter userAdapter;
    private FragmentUserBinding binding;
    private UserDataModel userData;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);

        //View Model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        storyBunchViewModel = new ViewModelProvider(getActivity()).get(StoryBunchViewModel.class);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        //Get Argument
        if(getArguments() != null){
            userData = UserFragmentArgs.fromBundle(getArguments()).getUserDataModel();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listenForWidth();
    }

    private void listenForWidth(){
        final ViewTreeObserver observer = binding.userRecyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // I don't understand onGlobalLayout. What exactly does it listen for?
                int squareLength = binding.userRecyclerView.getWidth()/4;

                initializeRecyclerView(squareLength);

                ViewTreeObserver innerObserver = binding.userRecyclerView.getViewTreeObserver();
                innerObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initializeRecyclerView(int squareLength){

        List<DataItem> dataItems = new ArrayList<>();

        //Adding user header to data items;
        dataItems.add(0, new UserHeader(userData));

        List<List<StoriesDataModel>> userGalleriesList = new ArrayList<>();
        for(long galleryId : userData.getGalleries()){

            //Adding user galleries to data items
            StoriesDataModel parentPost = userViewModel.getParentPostWithGhosts(galleryId);
            List<StoriesDataModel> children = userViewModel.getChildrenPosts(galleryId);
            dataItems.add(new UserGallery(parentPost, children));

            userGalleriesList.add(children);
        }

        //Taking the first child post of each gallery to add all posts to data items
        List<StoriesDataModel> allUserPostsMix = new ArrayList<>();
        for(List<StoriesDataModel> gallery : userGalleriesList){
            allUserPostsMix.addAll(gallery.subList(0, 4));
        }
        dataItems.add(1, new AllUserPosts(allUserPostsMix));

        Log.d("type", dataItems.toString());

        userAdapter = new UserAdapter(getContext(), squareLength, dataItems, storyBunchViewModel, navController);
        binding.userRecyclerView.setAdapter(userAdapter);
    }
}
package com.app.tiktok.ui.user;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentUserBinding;
import com.app.tiktok.model.Post;
import com.app.tiktok.model.User;
import com.app.tiktok.ui.story.PostsViewModel;
import com.app.tiktok.ui.story.UtilViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserFragment extends Fragment {

    private UtilViewModel utilViewModel;
    private PostsViewModel postsViewModel;
    private FragmentUserBinding binding;
    private User userData;
    private NavController navController;
    private String position;

    private static final String USER_KEY = "USER_KEY";
    private static final String POSITION_KEY = "POSITION_KEY";

    public static UserFragment newInstance(String position, User userData) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_KEY, userData);
        args.putString(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Argument
        if(getArguments() != null){
            userData = getArguments().getParcelable(USER_KEY);
            position = getArguments().getString(POSITION_KEY);
        }

        utilViewModel = new ViewModelProvider(requireActivity()).get(UtilViewModel.class);
        postsViewModel = new ViewModelProvider(requireActivity()).get(position, PostsViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpToolbar();

        binding.setUserData(userData);
        Glide.with(this)
                .load(userData.getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.userProfile);

        Random rand = new Random();
        Post coverStory = utilViewModel.getRandomUserPost(userData.getGalleries().get(rand.nextInt(userData.getGalleries().size())));

        Glide.with(this)
                .load(coverStory.getUrl())
                .thumbnail(0.25f)
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.profileBackground);

        initializeViewPager();

        TabLayout tabLayout = binding.tabLayout;

        tabLayout.setSelectedTabIndicator(R.drawable.tab_indicator);
        new TabLayoutMediator(tabLayout, binding.bottomSheetPager,
            (tab, position) -> {
                switch (position){
                    case 0:
                        tab.setText("Posts");
                        break;
                    case 1:
                        tab.setText("Galleries");
                        break;
                    case 2:
                        tab.setText("Events");
                        break;
                    default:
                }
            }
        ).attach();
    }

    private void setUpToolbar(){
        binding.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStackImmediate();
                postsViewModel.setEnableInteractions(true);
            }
        });
    }

    private void initializeViewPager(){
        //Pass in everything first. Later we may need to filter.
        UserPagerAdapter pagerAdapter = new UserPagerAdapter(this, userData.getId());
        binding.bottomSheetPager.setOffscreenPageLimit(1);
        binding.bottomSheetPager.setAdapter(pagerAdapter);
    }
}
package com.app.tiktok.ui.story;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentBottomSheetBinding;
import com.app.tiktok.databinding.LayoutTabBinding;
import com.app.tiktok.model.StoriesDataModel;
import com.app.tiktok.utils.Constants;
import com.app.tiktok.utils.Utility;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BottomSheetFragment extends Fragment {

    public static final int TAB_ITEM_WIDTH = 35;
    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";

    private StoriesDataModel parentPost;
    public FragmentBottomSheetBinding binding;
    private static BottomSheetFragment instance;

    public BottomSheetFragment() {
    }

    public static BottomSheetFragment newInstance(StoriesDataModel parentPost) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PARENT_POST, parentPost);
        fragment.setArguments(args);
        return fragment;
    }

    public static BottomSheetFragment getInstance(){
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("morning", "BOttom SHeet wok up");
        instance = this;
        if (getArguments() != null) {
            parentPost = getArguments().getParcelable(KEY_PARENT_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewPager();
        TabLayout tabLayout = binding.tabLayout;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LinearLayout tabLayout = (LinearLayout) tab.getCustomView();
                tabLayout.findViewById(R.id.icon_container).setBackground(getResources().getDrawable(R.drawable.tab_indicator));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LinearLayout tabLayout = (LinearLayout) tab.getCustomView();
                tabLayout.findViewById(R.id.icon_container).setBackground(getResources().getDrawable(R.drawable.tab_indicator_white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setSelectedTabIndicator(R.drawable.tab_indicator);
        new TabLayoutMediator(tabLayout, binding.bottomSheetPager,
            (tab, position) -> {
                LayoutTabBinding tabBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_tab, binding.tabLayout, false);
                switch (position){
                    case 0:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.clock_30__3_);
                        break;
                    case 1:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.short_vid_30);
                        break;
                    case 2:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.long_vid_30);
                        break;
                    case 3:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.pictures_30);
                        break;
                    case 4:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.favorites_30);
                        break;
                    default:
                }
                tab.setCustomView(tabBinding.getRoot());
            }
        ).attach();
    }

    private void initializeViewPager(){
        //Pass in everything first. Later we may need to filter.
        BottomSheetPagerAdapter pagerAdapter = new BottomSheetPagerAdapter(this, parentPost);

        binding.bottomSheetPager.setOffscreenPageLimit(1);
        binding.bottomSheetPager.setAdapter(pagerAdapter);
    }
}
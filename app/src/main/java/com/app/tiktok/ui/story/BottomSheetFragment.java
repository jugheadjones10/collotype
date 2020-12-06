package com.app.tiktok.ui.story;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.tiktok.R;
import com.app.tiktok.databinding.FragmentBottomSheetBinding;
import com.app.tiktok.databinding.LayoutTabBinding;
import com.app.tiktok.model.Gallery;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class BottomSheetFragment extends Fragment {

    public static final int TAB_ITEM_WIDTH = 35;
    private static final String KEY_PARENT_POST = "KEY_PARENT_POST";
    private static final String KEY_GALLERY = "KEY_GALLERY";

    private String position;
    private Gallery gallery;
    public FragmentBottomSheetBinding binding;
    private static BottomSheetFragment instance;

    public BottomSheetFragment() {
    }

    public static BottomSheetFragment newInstance(String position, Gallery gallery) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PARENT_POST, position);
        args.putParcelable(KEY_GALLERY, gallery);
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
            position = getArguments().getString(KEY_PARENT_POST);
            gallery = getArguments().getParcelable(KEY_GALLERY);
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
                LinearLayout tabLayout = (LinearLayout) tab.getCustomView();
                tabLayout.findViewById(R.id.icon_container).setBackground(getResources().getDrawable(R.drawable.tab_indicator_white));

                tabLayout.findViewById(R.id.icon_bg).getBackground().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LinearLayout tabLayout = (LinearLayout) tab.getCustomView();
                tabLayout.findViewById(R.id.icon_container).setBackground(getResources().getDrawable(R.drawable.tab_indicator_grey));

                tabLayout.findViewById(R.id.icon_bg).getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
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
                        tabBinding.iconBg.setBackgroundResource(R.drawable.clock_30);
                        break;
                    case 1:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.ic_goals);
                        break;
                    case 2:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.short_vid_30);
                        break;
                    case 3:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.ic_long_vid);
                        break;
                    case 4:
                        tabBinding.iconBg.setBackgroundResource(R.drawable.pictures_30);
                        break;
                    default:
                }
                tabBinding.iconBg.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
                tab.setCustomView(tabBinding.getRoot());
            }
        ).attach();
    }

    private void initializeViewPager(){
        //Pass in everything first. Later we may need to filter.
        BottomSheetPagerAdapter pagerAdapter = new BottomSheetPagerAdapter(this, position, gallery);

        binding.bottomSheetPager.setOffscreenPageLimit(1);
        binding.bottomSheetPager.setAdapter(pagerAdapter);
    }
}
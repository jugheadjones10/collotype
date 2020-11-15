package com.app.tiktok.ui.home.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class VerticalCubeTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(position < 0f ? page.getHeight() : 0f);
        page.setRotationX(-45f * position);
    }
}

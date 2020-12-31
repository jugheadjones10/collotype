package com.app.tiktok.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.app.tiktok.utils.Utility;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.BaseRequestOptions;

@GlideExtension
public final class MyGlideExtension {

    private MyGlideExtension() {
        // Utility class.
    }

    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> smallCircle(BaseRequestOptions<?> options, Context context) {
        return options
            .fitCenter()
            .override(Utility.INSTANCE.dpToPx(25, context));
    }
//
//    @GlideOption
//    public static void smallCircle(RequestOptions options) {
//        options
//            .thumbnail(0.25f)
//            .circleCrop()
//            .override(25, 25);
//    }
//
//    @GlideOption
//    public static void largeCircle(RequestOptions options) {
//        options
//                .placeholder(R.drawable.noise)
//                .circleCrop()
//                .override(300, 300);
//    }
//
//    @GlideOption
//    public static void smallSquare(RequestOptions options) {
//        options
//                .placeholder(R.drawable.noise)
//                .circleCrop()
//                .override(300, 300);
//    }
//
//    @GlideOption
//    public static void mainPost(RequestOptions options) {
//        options
//                .placeholder(R.drawable.noise)
//                .circleCrop()
//                .override(300, 300);
//    }

}

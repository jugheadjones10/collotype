package com.app.tiktok.utils

import android.graphics.Bitmap
import android.view.View
import com.app.tiktok.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.google.android.material.imageview.ShapeableImageView


fun ShapeableImageView.loadImageFromUrl(imageUrl: String?){
//    Glide.with(this)
//        .load(imageUrl)
//        .into(this)

    Glide.with(this)
        .load(imageUrl)
        .thumbnail(0.25f)
        .override(this.width, this.height)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)

}

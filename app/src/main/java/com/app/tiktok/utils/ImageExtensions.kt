package com.app.tiktok.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.android.synthetic.main.fragment_story_bunch.*

fun ShapeableImageView.loadImageFromUrl(imageUrl: String?){
//    Glide.with(this)
//        .load(imageUrl)
//        .into(this)

    Glide.with(this)
        .load(imageUrl)
        .thumbnail(0.25f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ShapeableImageView.loadCenterCropImageFromUrl(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .centerCrop()
        .into(this)
}

fun ShapeableImageView.loadCenterCropFromLocal(imageId: Int?) {
    Glide.with(this)
        .load(imageId)
        .centerCrop()
        .into(this)
}

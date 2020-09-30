package com.app.tiktok.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

fun ImageView.loadImageFromUrl(imageUrl: String?){
    Glide.with(this)
        .load(imageUrl)
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

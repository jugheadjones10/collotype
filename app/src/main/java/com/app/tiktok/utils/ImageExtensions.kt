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
//        .placeholder(R.drawable.ironman_sexy)
        .thumbnail(0.25f)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)

//    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
//        .asBitmap()
//        .load(imageUrl)
//        .submit(200, 600)
//
//
//    //This thing crashes if recycler view hasn't loaded yet/5555
//    postDelayed(Runnable { //DOES THIS CHECK ACTUALLY HAVE ANY EFFECT
//        val bitmap = futureTarget.get()
//        setImageBitmap(bitmap)
//        Glide.with(context).clear(futureTarget)
//    }, 50)

}

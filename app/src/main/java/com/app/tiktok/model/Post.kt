package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(

    val id: Long,
    val url: String,
    val gallery: Long,

    val caption: String,
    val likesCount: Long,
    val commentsCount: Long,

    val products: ArrayList<Long>,

    var processPosts: ArrayList<Long>,
    val processTitle: String

): Parcelable
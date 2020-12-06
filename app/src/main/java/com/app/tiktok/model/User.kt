package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(

    val id: Long,
    val url: String,
    val username: String,

    val galleries: ArrayList<Long>,

    val followersCount: Long,
    val viewsCount: Long,
    val postsCount: Long

): Parcelable
package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gallery(

    val id: Long,
    val url: String,
    val name: String,

    val posts: ArrayList<Long>,
    val members: ArrayList<Long>,
    val events: ArrayList<Long>,

    val recposts: ArrayList<Long>,
    val recproducts: ArrayList<Long>,

    val followersCount: Long,

    val collab: Boolean,
    val collaborators: ArrayList<Long>,

    val official: Boolean

): Parcelable
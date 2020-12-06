package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(

    val id: Long,
    val name: String,
    val url: String,

    val price: Long,
    val seller: Long

): Parcelable
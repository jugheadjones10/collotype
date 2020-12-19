package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(

    val id: Long,
    val url: String,
    val title: String,
    val gallery: Long,

    val hosts: ArrayList<Long>,
    val datetime: String

): Parcelable
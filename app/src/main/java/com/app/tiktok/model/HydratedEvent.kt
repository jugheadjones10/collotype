package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HydratedEvent(

    val id: Long,
    val url: String,
    val title: String,

    val hosts: ArrayList<User>,
    val dateTime: String

): Parcelable
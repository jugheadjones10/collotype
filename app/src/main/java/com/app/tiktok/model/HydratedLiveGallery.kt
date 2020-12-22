package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HydratedLiveGallery(

    val gallery: Gallery,
    val rivalGalleries: ArrayList<Gallery>,
    val rivalMembers: ArrayList<User>

): Parcelable
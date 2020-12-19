package com.app.tiktok.ui.recommended

import android.os.Parcelable
import com.app.tiktok.model.Gallery
import com.app.tiktok.model.Post
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryPost(

    val post: Post,
    val gallery: Gallery

): Parcelable
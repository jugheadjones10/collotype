package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoriesDataModel(
    val storyId: Long,
    val storyUrl: String,
    val storyThumbUrl: String? = null,
    val storyDescription: String? = null,
    val userId: String,
    val userProfilePicUrl: String? = null,
    val userName: String,
    val likesCount: Long,
    val commentsCount: Long,

    val parentId: Long,
    val groupName: String,
    val followersCount : Long,
    val membersThumbUrls : ArrayList<String>,
    val memberIds : ArrayList<Integer>?,
    val sameGroupPostIds : ArrayList<String>,

    val productPrice : Long?,
    val productName : String?,
    val productThumb : String?,

    val processPostUrls : ArrayList<String>?,
    val processPostCaptions : ArrayList<String>?

): Parcelable
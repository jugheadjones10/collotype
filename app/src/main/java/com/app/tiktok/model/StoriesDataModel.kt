package com.app.tiktok.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoriesDataModel(

    val parentId: Long,
    val storyId: Long,

    val storyThumbUrl: String? = null,
    val groupName: String? = null,
    val memberIds : ArrayList<Long>?,
    val followersCount : Long? = null,

    val storyUrl: String,
    val processPostIds: ArrayList<Long>?,
    val storyDescription: String? = null,
    val likesCount: Long,
    val commentsCount: Long,


//    val userId: String,
//    val userProfilePicUrl: String? = null,
//    val userName: String,
//
//    val membersThumbUrls : ArrayList<String>,
//    val sameGroupPostIds : ArrayList<String>?,

    val productPrice : Long?,
    val productName : String?,
    val productThumb : String?

//    val processPostUrls : ArrayList<String>?,
//    val processPostCaptions : ArrayList<String>?

): Parcelable
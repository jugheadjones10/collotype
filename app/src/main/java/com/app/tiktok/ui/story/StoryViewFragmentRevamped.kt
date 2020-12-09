package com.app.tiktok.ui.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.tiktok.R
import com.app.tiktok.app.MyApp
import com.app.tiktok.databinding.IncludePriceTagBinding
import com.app.tiktok.databinding.ItemProcessPostBinding
import com.app.tiktok.model.*
import com.app.tiktok.ui.main.MainViewModel
import com.app.tiktok.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.android.synthetic.main.fragment_story_bunch.*
import kotlinx.android.synthetic.main.include_price_tag.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_process_posts_scroll.*
import kotlinx.android.synthetic.main.layout_story_view.*

class StoryViewFragmentRevamped : Fragment(R.layout.fragment_story_view) {


    private lateinit var post: Post
    private lateinit var gallery: Gallery
    private lateinit var storyUrl: String
    private lateinit var postViewModel: PostViewModel
    var parent: StoryBunchFragment? = null

    companion object {
        fun newInstance(post: Post, gallery: Gallery) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_POST_DATA, post)
                    putParcelable(Constants.KEY_GALLERY_DATA, gallery)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            post = it.getParcelable(Constants.KEY_POST_DATA)!!
            gallery = it.getParcelable(Constants.KEY_GALLERY_DATA)!!
        }

        //Initialize View Model
        postViewModel = ViewModelProvider(requireActivity()).get(
            post.id.toString(),
            PostViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(parentFragment is StoryBunchFragment){
            parent = parentFragment as StoryBunchFragment
        }

        setData()
    }

    private fun setData() {

        storyUrl = post.url
        val storyUrlType = Utility.extractS3URLfileType(storyUrl)

        if (Utility.isImage(storyUrlType)) {
            post_image.visibility = View.VISIBLE
            player_view_story.visibility = View.GONE

            //Loading image content from url
            post_image?.loadImageFromUrl(storyUrl)

        } else {

            post_image.visibility = View.GONE
            player_view_story.visibility = View.VISIBLE

            //Loading video content from url
//            val simplePlayer = getPlayer()
//
//            player_view_story.player = simplePlayer

            //post.url.let { prepareMedia(it) }
//            prepareMedia(storyUrl)
        }

        //image_view_group_pic?.loadCenterCropImageFromUrl(storiesDataModel?.storyThumbUrl)


//        text_view_video_description.setTextOrHide(value = post.caption)
//
//        var user: User
//        if(gallery.members.size > 0){
//            user = utilViewModel.getUser(gallery.members.get(0))
//        }else{
//            user = User(
//                125,
//                "https://collotype.s3.ap-northeast-2.amazonaws.com/squatteamx/members/07+James+Law/07.jpg",
//                "Jack Mueller",
//                arrayListOf(-1L),
//                -1L,
//                -1L,
//                -1L
//            )
//        }
//        username.text = user.username
//
//        image_view_option_like_title?.text =
//            post.likesCount.formatNumberAsReadableFormat()
//        image_view_option_comment_title?.text =
//            post.commentsCount.formatNumberAsReadableFormat()
//
//        (caption_profile as ShapeableImageView).loadImageFromUrl(user.url)
    }

}

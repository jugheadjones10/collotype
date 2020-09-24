package com.app.tiktok.ui.story

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tiktok.R
import com.app.tiktok.app.MyApp
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.main.activity.MainActivity
import com.app.tiktok.ui.main.viewmodel.MainViewModel
import com.app.tiktok.utils.*
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.layout_story_view.*


class StoryViewFragment : Fragment(R.layout.fragment_story_view) {
    private var storyUrl: String? = null
    private var storiesDataModel: StoriesDataModel? = null

    private var simplePlayer: SimpleExoPlayer? = null
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private val simpleCache = MyApp.simpleCache
    private var toPlayVideoPosition: Int = -1
    private var position: Int = 0

    //Bottom posts recycler
//    var recyclerView: RecyclerView? = null
    var bottomPhotos: ArrayList<Int>? = null
//    var RecyclerViewLayoutManager: RecyclerView.LayoutManager? = null
//    var adapter: BottomPostsAdapter? = null
//    var HorizontalLayout: LinearLayoutManager? = null

    companion object {
        fun newInstance(storiesDataModel: StoriesDataModel, postion: Int) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, storiesDataModel)
                    putInt(Constants.POSITION_INT, postion)
                }
            }
    }

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.Companion.bottomNavBar?.visibility = View.GONE

        bottomPhotos = arrayListOf(
            R.drawable.ironman_yellow,
            R.drawable.ironman_fight,
            R.drawable.ironman_faceoff,
            R.drawable.ironman_shooting,
            R.drawable.ironman_lego,
            R.drawable.ironman_mini,
            R.drawable.ironman_sexy,
            R.drawable.ironman_king
        )
        bottom_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)
        bottom_recycler_view.adapter = BottomPostsAdapter(bottomPhotos)

        storiesDataModel = arguments?.getParcelable(Constants.KEY_STORY_DATA)
        position = arguments?.getInt(Constants.POSITION_INT)!!

        setData()
    }

    private fun setData() {

        //Group members
        group_member_one?.setImageResource(R.drawable.profile_scarlett)
        group_member_two?.setImageResource(R.drawable.profile_victoria)
        group_member_three?.setImageResource(R.drawable.profile_chris)

        if(position == 0){

            text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)

            image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
            image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()

            image_view_profile_pic?.loadCenterCropFromLocal(R.drawable.ironman_group_profile)

            post_image?.setImageResource(R.drawable.ironman_painting)

        }else if(position == 1){

            text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)

            image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
            image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()

            image_view_profile_pic?.loadCenterCropFromLocal(R.drawable.ironman_suit)

            post_image?.setImageResource(R.drawable.ironman_king)

        }else if(position == 2){

            text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)

            image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
            image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()

            image_view_profile_pic?.loadCenterCropFromLocal(R.drawable.ironman_painting)

            post_image?.setImageResource(R.drawable.ironman_lego)

        }else{

            text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)

            image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
            image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()

            image_view_profile_pic?.loadCenterCropFromLocal(R.drawable.ironman_faceoff)

            post_image?.setImageResource(R.drawable.ironman_suit)

        }
//        text_view_account_handle.setTextOrHide(value = storiesDataModel?.userName)
//        text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)
//        text_view_music_title.setTextOrHide(value = storiesDataModel?.musicCoverTitle)
//
//        image_view_option_comment_title?.text = storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()
//        image_view_option_like_title?.text = storiesDataModel?.likesCount?.formatNumberAsReadableFormat()
//
//        image_view_profile_pic?.loadCenterCropImageFromUrl(storiesDataModel?.userProfilePicUrl)
//
//        text_view_music_title.isSelected = true
//
//        val simplePlayer = getPlayer()
//        player_view_story.player = simplePlayer
//
//        storyUrl = storiesDataModel?.storyUrl
//        storyUrl?.let { prepareMedia(it) }
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private val playerCallback: Player.EventListener? = object: Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("onPlayerStateChanged playbackState: $playbackState")
        }

        override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException?) {
            super.onPlayerError(error)
        }
    }

    private fun prepareVideoPlayer() {
        simplePlayer = ExoPlayerFactory.newSimpleInstance(context)
        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(context,
                "exo"))
        )
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        logError("prepareMedia linkUrl: $linkUrl")

        val uri = Uri.parse(linkUrl)

        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri)

        simplePlayer?.prepare(mediaSource, true, true)
        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
        simplePlayer?.playWhenReady = true
        simplePlayer?.addListener(playerCallback)

        toPlayVideoPosition = -1
    }

    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        if (simplePlayer == null) {
            storyUrl?.let { prepareMedia(it) }
        } else {
            simplePlayer?.seekToDefaultPosition()
            simplePlayer?.playWhenReady = true
        }
    }

    private fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }
}
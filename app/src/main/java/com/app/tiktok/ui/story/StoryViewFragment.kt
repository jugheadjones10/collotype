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
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.tiktok.R
import com.app.tiktok.app.MyApp
import com.app.tiktok.databinding.IncludePriceTagBinding
import com.app.tiktok.databinding.IncludeProcessPostBinding
import com.app.tiktok.model.StoriesDataModel
import com.app.tiktok.ui.main.viewmodel.MainViewModel
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
import kotlinx.android.synthetic.main.layout_process_posts_scroll.*
import kotlinx.android.synthetic.main.layout_story_view.*


//https://thumbsnap.com/i/tn1tP9To.mp4
//https://media.giphy.com/media/7JHtlG3rEkyLLZ1JCs/giphy.gif
//Sample mp4 of my face
private const val DEBUG_TAG = "Gestures"


class StoryViewFragment : Fragment(R.layout.fragment_story_view) {
    private var storyUrl: String? = null
    private var storiesDataModel: StoriesDataModel? = null
    private var parentPost: StoriesDataModel? = null
    private var gestureListener: MyGestureListener? = null

    private var simplePlayer: SimpleExoPlayer? = null
    private val simpleCache = MyApp.simpleCache
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var toPlayVideoPosition: Int = -1

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var playbackStateListener: PlaybackStateListener? = null

    var parent: StoryBunchFragment? = null


    private var volumeState: VolumeState? = null
    private enum class VolumeState {
        ON, OFF
    }

    class PlaybackStateListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String
            stateString = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(
                "StoryViewFragment", "changed state to $stateString"
            )
        }
    }

    companion object {
        fun newInstance(storiesDataModel: StoriesDataModel, parentPost: StoriesDataModel) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_STORY_DATA, storiesDataModel)
                    putParcelable(Constants.KEY_PARENT_STORY, parentPost)
                }
            }
    }

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = parentFragment as StoryBunchFragment

        playbackStateListener = PlaybackStateListener()

        options_container.setOnClickListener {
            Log.d("volume", "options container on click listener")
            toggleVolume()
        }

        storiesDataModel = arguments?.getParcelable(Constants.KEY_STORY_DATA)
        parentPost = arguments?.getParcelable(Constants.KEY_PARENT_STORY)

        gestureListener = MyGestureListener(parent)
        val myGestureListener = GestureDetectorCompat(context, gestureListener)
        options_container.setOnTouchListener(OnTouchListener { view, motionEvent ->
            myGestureListener.onTouchEvent(motionEvent)
            false
        })

        //This thing not working
        if(parent?.top_bar_container?.translationY != 0f){
            gestureListener!!.moveBottomsOff()
        }

        //Adding drag functionality
        options_container.setOnDragListener(dragListen)

        setData()
    }

    inner class MyGestureListener constructor(val parentFragment: StoryBunchFragment?) : GestureDetector.SimpleOnGestureListener() {

        fun create0fAnimator(view: View?): ObjectAnimator? {
            return ObjectAnimator.ofFloat(view, "translationY", 0f)
        }

        fun moveBottomsOff(){
            val socialIconsAnim = ObjectAnimator.ofFloat(recycler_view_options, "translationY", 400f)
            val profilePicAnim = ObjectAnimator.ofFloat(caption_profile, "translationY", 300f)
            val usernameAnim = ObjectAnimator.ofFloat(username, "translationY", 300f)
            val dateAnim = ObjectAnimator.ofFloat(date, "translationY", 300f)
            val captionAnim = ObjectAnimator.ofFloat(text_view_video_description, "translationY", 300f)

            AnimatorSet().apply {
                duration = 600
                playTogether(socialIconsAnim, profilePicAnim, usernameAnim, dateAnim, captionAnim)
                start()
            }
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {

            if(parentFragment?.top_bar_container?.translationY == 0f){

                val topBarAnim = ObjectAnimator.ofFloat(parentFragment?.top_bar_container, "translationY", -300f)
                val bottomPostsAnim = ObjectAnimator.ofFloat(parentFragment?.layout_bot_sheet, "translationY", 300f)

                moveBottomsOff()
                AnimatorSet().apply {
                    duration = 600
                    playTogether(topBarAnim, bottomPostsAnim)
                    start()
                }

                ValueAnimator.ofFloat(parentFragment!!.bigSquareLength?.toFloat(), 0f).apply {
                    duration = 600
                    start()
                    addUpdateListener { updatedAnimation ->
                        // You can use the animated value in a property that uses the
                        // same type as the animation. In this case, you can use the
                        // float value in the translationX property.

                        val viewPagerMarginParams = parentFragment?.posts_view_pager.getLayoutParams() as ViewGroup.MarginLayoutParams
                        viewPagerMarginParams.bottomMargin = (updatedAnimation.animatedValue as Float).toInt()
                        parentFragment?.posts_view_pager.setLayoutParams(viewPagerMarginParams)
                    }
                }

            }else{
                AnimatorSet().apply {
                    duration = 600
                    playTogether(
                        create0fAnimator(parentFragment?.top_bar_container),
                        create0fAnimator(parentFragment?.layout_bot_sheet),
                        create0fAnimator(recycler_view_options),
                        create0fAnimator(caption_profile),
                        create0fAnimator(username),
                        create0fAnimator(date),
                        create0fAnimator(text_view_video_description)
                    )
                    start()
                }

                ValueAnimator.ofFloat(0f, parentFragment!!.bigSquareLength?.toFloat()).apply {
                    duration = 600
                    start()
                    addUpdateListener { updatedAnimation ->
                        // You can use the animated value in a property that uses the
                        // same type as the animation. In this case, you can use the
                        // float value in the translationX property.

                        val viewPagerMarginParams = parentFragment?.posts_view_pager.getLayoutParams() as ViewGroup.MarginLayoutParams
                        viewPagerMarginParams.bottomMargin = (updatedAnimation.animatedValue as Float).toInt()
                        parentFragment?.posts_view_pager.setLayoutParams(viewPagerMarginParams)
                    }
                }
            }

            return true
        }

    }

    // Creates a new drag event listener
    //https://www.tutlane.com/tutorial/android/android-drag-and-drop-with-examples
    private val dragListen = View.OnDragListener { v, event ->

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    val vw = event.localState as View
                    vw.visibility = GONE
                    true
                } else {
                    false
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                true
            }
            DragEvent.ACTION_DRAG_LOCATION ->
                // Ignore the event
                true
            DragEvent.ACTION_DRAG_EXITED -> {
                true
            }
            DragEvent.ACTION_DROP -> {

                val vw = event.localState as View
                vw.x = event.x - vw.width/2
                vw.y = event.y - vw.height/2
                vw.visibility = View.VISIBLE //finally set Visibility to VISIBLE

                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                true
            }
            else -> {
                // An unknown action type was received.
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                false
            }
        }
    }

    inner class OnProcessPostClicked {
        fun onProcessPostClicked(processCaption: String, processPostUrl: String){
            context?.let {
                Glide.with(it)
                    .load(processPostUrl)
                    .into(post_image)
            }

            text_view_video_description.setTextOrHide(value = processCaption)
        }
    }

    private fun setData() {

        //Own self is added so it is displayed at the bottom
        Log.d("lag", "Setting data in story view fragment")

        if(storiesDataModel?.productName != null){
            val binding: IncludePriceTagBinding = IncludePriceTagBinding.inflate(
                getLayoutInflater(),
                options_container,
                false
            )
            options_container.addView(binding.root)

            binding.apply {
                productName = storiesDataModel?.productName
                productPrice = storiesDataModel?.productPrice
            }

            product_thumb.loadImageFromUrl(storiesDataModel?.productThumb)

            val PRICETAG_TAG = "price tag"
            val priceTag = binding.root.apply {
                tag = PRICETAG_TAG
                setOnLongClickListener { v: View ->
                    val item = ClipData.Item(v.tag as? CharSequence)
                    val dragData = ClipData(
                        v.tag as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item)

                    val myShadow = View.DragShadowBuilder(this)

                    // Starts the drag
                    v.startDragAndDrop(
                        dragData,   // the data to be dragged
                        myShadow,   // the drag shadow builder
                        v,
                        0           // flags (not currently used, set to 0)
                    )
                }
            }

        }

        if(storiesDataModel?.processPostUrls != null){

            val processPostsLayout = layoutInflater.inflate(R.layout.layout_process_posts_scroll, options_container, false)

            options_container.addView(processPostsLayout)

            storiesDataModel?.processPostUrls?.add(0, storiesDataModel?.storyUrl!!)
            storiesDataModel?.processPostCaptions?.add(0, storiesDataModel?.storyDescription!!)
            for((i, processPostUrlString) in storiesDataModel?.processPostUrls?.withIndex()!!){
                val binding: IncludeProcessPostBinding = IncludeProcessPostBinding.inflate(
                    getLayoutInflater(),
                    scroll_linear_layout,
                    false
                )

                binding.apply {
                    processCaption = storiesDataModel?.processPostCaptions?.get(i)
                    processPostUrl = processPostUrlString
                    onProcessPostClicked = OnProcessPostClicked()
                }

                //binding.processPostImage.loadImageFromUrl(processPostUrlString)
                Glide.with(this)
                    .load(processPostUrlString)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.processPostImage)

                scroll_linear_layout.addView(binding.root)
            }
        }

        //Group members
//        group_member_one?.setImageResource(R.drawable.profile_scarlett)
//        group_member_two?.setImageResource(R.drawable.profile_victoria)
//        group_member_three?.setImageResource(R.drawable.profile_chris)

//        "storyId" : 1,
//        "storyUrl" : "https://res.cloudinary.com/mydatacloud/video/upload/v1592819413/stories/video_azmmil.mp4",
//        "storyThumbUrl" : "https://res.cloudinary.com/mydatacloud/video/upload/v1592819413/stories/video_azmmil.jpg",
//        "storyDescription" : "Tried drawing Iron Man... it was fun, but also tiring",
//        "userId" : 1,
//        "userProfilePicUrl" : "https://res.cloudinary.com/mydatacloud/image/upload/v1592823557/profiles/user_3_dzxh4b.jpg",
//        "userName" : "Sherlyn",
//        "likesCount" : 500,
//        "commentsCount" : 1000,
//
//        "groupName" : "Iron Man X",
//        "followersCount" : 1235,
//        "membersThumbUrls" : [],
//        "sameGroupPostIds" : [2, 3, 4]

        //Check file type of url. If file type is jpg, then use loadImageFromUrl into ImageView. If mp4, use exo player.
        storyUrl = storiesDataModel?.storyUrl
        val storyUrlType = storyUrl?.substring(storyUrl!!.length - 3)

        //Eventually include gif when you figure out how to make ImageView support it
        if (storyUrlType.equals("jpg") || storyUrlType.equals("gif") || storyUrlType.equals("jpeg")) {
            post_image.visibility = View.VISIBLE
            player_view_story.visibility = View.GONE

            //Loading image content from url
            post_image?.loadImageFromUrl(storyUrl)
            Log.d("lag", "Loaded image in story view fragment")


        } else if (storyUrlType.equals("mp4")) {
//            post_image.visibility = View.GONE
//            player_view_story.visibility = View.VISIBLE
//
//            //Loading video content from url
//
//            val simplePlayer = getPlayer()
//
//            setVolumeControl(VolumeState.OFF)
//            player_view_story.player = simplePlayer
//
//            storyUrl = storiesDataModel?.storyUrl
//            storyUrl?.let { prepareMedia(it) }
        }

        //image_view_group_pic?.loadCenterCropImageFromUrl(storiesDataModel?.storyThumbUrl)
        text_view_video_description.setTextOrHide(value = storiesDataModel?.storyDescription)
        username.text = storiesDataModel?.userName

        image_view_option_like_title?.text =
            storiesDataModel?.likesCount?.formatNumberAsReadableFormat()
        image_view_option_comment_title?.text =
            storiesDataModel?.commentsCount?.formatNumberAsReadableFormat()

        (caption_profile as ShapeableImageView).loadImageFromUrl(parentPost?.membersThumbUrls?.get(0))


        //group_name?.text =storiesDataModel?.groupName
        //followers_count?.text = storiesDataModel?.followersCount?.formatNumberAsReadableFormat()


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
        //pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        //restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        //releasePlayer()
        super.onDestroy()
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareVideoPlayer() {
        simplePlayer = ExoPlayerFactory.newSimpleInstance(context)
        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(context,
                    "exo"))
        )
    }

    private val playerCallback: Player.EventListener? = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("onPlayerStateChanged playbackState: $playbackState")
        }
    }

//    private fun initializePlayer() {
//
//        simplePlayer = context?.let { SimpleExoPlayer.Builder(it).build() }
//        simplePlayer!!.playWhenReady = playWhenReady
//        simplePlayer!!.seekTo(currentWindow, playbackPosition)
//        playbackStateListener?.let { simplePlayer!!.addListener(it) }
//        simplePlayer!!.prepare()
//
//        player_view_story.player = simplePlayer

//        cacheDataSourceFactory = CacheDataSource.Factory(simpleCache,
//            DefaultHttpDataSourceFactory(
//                Util.getUserAgent(context,
//                "exo"))
//        )
//    }

//    private val cacheDataSink: CacheDataSink = this!!.simpleCache?.let { CacheDataSink(it, 1000) }!!
//    private val fileDataSource: FileDataSource = FileDataSource()
//    private val defaultDataSourceFactory: DefaultDataSourceFactory
//
//    init {
//        val bandwidthMeter = DefaultBandwidthMeter()
////        defaultDataSourceFactory = DefaultDataSourceFactory(
////            requireContext(),
////            bandwidthMeter,
////            DefaultHttpDataSourceFactory(Util.getUserAgent(this.requireContext(), "exo"))
//
//        defaultDataSourceFactory = DefaultDataSourceFactory(
//            requireContext(),
//            bandwidthMeter,
//            DefaultHttpDataSourceFactory(
//                Util.getUserAgent(this.requireContext(), "exo"),
//                bandwidthMeter
//            )
//        )
//    }

//    private fun initializeMedia(storyUrl: String) {
//
//        val cacheDataSourceFactory = CacheDataSource.Factory()
//            .setCache(simpleCache!!)
//            .setUpstreamDataSourceFactory(defaultDataSourceFactory)
//            .setCacheWriteDataSinkFactory(null)
//            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

//        (
//            simpleCache!!,
//            defaultDataSourceFactory.createDataSource(),
//            fileDataSource,
//            cacheDataSink,
//            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
//            null
//        )

        // Create a data source factory.
//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory()

        // Create a progressive media source pointing to a stream uri.
//        val mediaSource: ProgressiveMediaSource =
//            ProgressiveMediaSource.Factory(cacheDataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(storyUrl))
//
//        // Set the media source to be played.
//        simplePlayer?.setMediaSource(mediaSource)
//    }

    private fun  prepareMedia(linkUrl: String) {
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

    private fun toggleVolume() {
        if (simplePlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(
                    "volume",
                    "togglePlaybackState: enabling volume."
                )
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                Log.d(
                    "volume",
                    "togglePlaybackState: disabling volume."
                )
                setVolumeControl(VolumeState.OFF)
            }
        }
    }

    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            simplePlayer?.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            simplePlayer?.volume = 1f
            animateVolumeControl()
        }
    }

    private fun animateVolumeControl() {
        if (volume_control != null) {
            volume_control.bringToFront()
            if (volumeState == VolumeState.OFF) {
                Glide.with(this).load(R.drawable.ic_volume_off).into(volume_control)
            } else if (volumeState == VolumeState.ON) {
                Glide.with(this).load(R.drawable.ic_volume_on).into(volume_control)
            }
            volume_control.animate().cancel()
            volume_control.setAlpha(1f)
            volume_control.animate()
                .alpha(0f)
                .setDuration(600).setStartDelay(1000)
        }
    }
}

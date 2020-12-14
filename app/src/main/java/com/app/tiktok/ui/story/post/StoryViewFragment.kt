package com.app.tiktok.ui.story.post

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.tiktok.R
import com.app.tiktok.app.MyApp
import com.app.tiktok.app.MyApp.Companion.executorService
import com.app.tiktok.databinding.IncludePriceTagBinding
import com.app.tiktok.databinding.ItemProcessPostBinding
import com.app.tiktok.model.Gallery
import com.app.tiktok.model.Post
import com.app.tiktok.model.Product
import com.app.tiktok.model.User
import com.app.tiktok.ui.story.StoryBunchFragment
import com.app.tiktok.ui.story.UtilViewModel
import com.app.tiktok.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.android.synthetic.main.fragment_story_bunch.*
import kotlinx.android.synthetic.main.include_price_tag.*
import kotlinx.android.synthetic.main.layout_process_posts_scroll.*
import kotlinx.android.synthetic.main.layout_story_view.*
import java.util.concurrent.Executor

class StoryViewFragment : Fragment(R.layout.fragment_story_view) {
    private var gestureListener: MyGestureListener? = null

    private lateinit var post: Post
    private lateinit var gallery: Gallery
    private lateinit var storyUrl: String

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
        fun newInstance(post: Post, gallery: Gallery) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.KEY_POST_DATA, post)
                    putParcelable(Constants.KEY_GALLERY_DATA, gallery)
                }
            }
    }


    private val utilViewModel by activityViewModels<UtilViewModel>()
    private lateinit var postViewModel: PostViewModel

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

        Log.d("fuhh", "StoryViewFragment onViewCreated");

        if(parentFragment is StoryBunchFragment){
            parent = parentFragment as StoryBunchFragment
        }

        playbackStateListener =
            PlaybackStateListener()

        post_image.cameraDistance = 1000000000000000000000000000f
        story_view_parent_constraint.cameraDistance = 1000000000000000000000000000f

        options_container.setOnClickListener {
            Log.d("volume", "options container on click listener")
            toggleVolume()
        }

        gestureListener = MyGestureListener(parent)
        val myGestureListener = GestureDetectorCompat(context, gestureListener)

        options_container.setOnTouchListener(OnTouchListener { view, motionEvent ->
            myGestureListener.onTouchEvent(motionEvent)
            false
        })

        //This thing not working
        if(parent?.inflatedTopBar?.translationY != 0f){
            gestureListener!!.moveBottomsOff()
        }

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

            if(parentFragment?.inflatedTopBar?.translationY == 0f){

                val topBarAnim = ObjectAnimator.ofFloat(parentFragment.inflatedTopBar, "translationY", -300f)
                val bottomPostsAnim = ObjectAnimator.ofFloat(parentFragment.layout_bot_sheet, "translationY", 300f)

                moveBottomsOff()
                AnimatorSet().apply {
                    duration = 600
                    playTogether(topBarAnim, bottomPostsAnim)
                    start()
                }

                ValueAnimator.ofFloat(parentFragment!!.squareLength?.toFloat(), 0f).apply {
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
                        create0fAnimator(parentFragment?.inflatedTopBar),
                        create0fAnimator(parentFragment?.layout_bot_sheet),
                        create0fAnimator(recycler_view_options),
                        create0fAnimator(caption_profile),
                        create0fAnimator(username),
                        create0fAnimator(date),
                        create0fAnimator(text_view_video_description)
                    )
                    start()
                }

                ValueAnimator.ofFloat(0f, parentFragment!!.squareLength?.toFloat()).apply {
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
        fun onProcessPostClicked(processCaption: String, processPostUrl: String, view: View){
            context?.let {
                Glide.with(it)
                    .load(processPostUrl)
                    .thumbnail(0.25f)
                    .override(250, 450)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(post_image)
            }

            Log.d("holyshit", " " + view.isSelected);

            (view.getParent() as LinearLayout).dispatchSetSelected(false)
            view.isSelected = true

            Log.d("holyshit", "after " + view.isSelected);

            text_view_video_description.setTextOrHide(value = processCaption)
        }
    }

    override fun onResume() {
        if(simplePlayer != null){
            val storyUrlType = Utility.extractS3URLfileType(storyUrl)
            if (!Utility.isImage(storyUrlType)) {
                setVolumeControl(VolumeState.ON)
            }

            restartVideo()
        }

        super.onResume()
    }


    private fun setData() {

        //Own self is added so it is displayed at the bottom
        Log.d("lag", "Setting data in story view fragment")

        if(post.products.size > 0){
            //Adding drag functionality
            options_container.setOnDragListener(dragListen)

            val binding: IncludePriceTagBinding = IncludePriceTagBinding.inflate(
                getLayoutInflater(),
                options_container,
                false
            )
            options_container.addView(binding.root)

            postViewModel.getProducts(post).observe(viewLifecycleOwner, Observer<List<Product>>{ products ->
                binding.apply {
                    productName = products.get(0).name
                    productPrice = products.get(0).price
                }
                product_thumb.loadImageFromUrl(products.get(0).url)
            })

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

        if(post.processPosts.size > 0){

            proces_title.text = post.processTitle

            val executor: Executor? = executorService
            postViewModel.getProcessPostsAsync(post, executor).observe(viewLifecycleOwner, Observer<List<Post>> { processPosts ->
                if(processPosts != null){

                    if(scroll_linear_layout ==  null){
                        val processPostsLayout = layoutInflater.inflate(R.layout.layout_process_posts_scroll, story_view_parent_constraint, false)
                        story_view_parent_constraint.addView(processPostsLayout)

                        for(processPost in processPosts){

                            val binding: ItemProcessPostBinding = ItemProcessPostBinding.inflate(
                                getLayoutInflater(),
                                scroll_linear_layout,
                                false
                            )

                            binding.apply {
                                processCaption = processPost.caption
                                processPostUrl = processPost.url
                                onProcessPostClicked = OnProcessPostClicked()
                            }

                            //binding.processPostImage.loadImageFromUrl(processPostUrlString)
                            Glide.with(this)
                                .load(processPost.url)
                                .thumbnail(0.25f)
                                .override(50, 50)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.processPostImage)

                            scroll_linear_layout.addView(binding.root)
                        }

                        scroll_linear_layout.getChildAt(0).isSelected = true
                    }
                }
            })

        }

        storyUrl = post.url
        val storyUrlType = Utility.extractS3URLfileType(storyUrl)

        if (Utility.isImage(storyUrlType)) {
            post_image.visibility = View.VISIBLE
            player_view_story.visibility = View.GONE

            //Loading image content from url
            if(post.gallery == 5L){
                post_image.scaleType = ImageView.ScaleType.FIT_CENTER
            }

            post_image?.loadImageFromUrl(storyUrl)

        } else {
            post_image.visibility = View.GONE
            player_view_story.visibility = View.VISIBLE

            //Loading video content from url
            val simplePlayer = getPlayer()

            setVolumeControl(VolumeState.OFF)
            player_view_story.player = simplePlayer

            post.url.let { prepareMedia(it) }
//            prepareMedia(storyUrl)
        }

        //image_view_group_pic?.loadCenterCropImageFromUrl(storiesDataModel?.storyThumbUrl)

        text_view_video_description.setTextOrHide(value = post.caption)

        var user: User
        if(gallery.members.size > 0){
            user = utilViewModel.getUser(gallery.members.get(0))
        }else{
            user = User(
                125,
                "https://collotype.s3.ap-northeast-2.amazonaws.com/squatteamx/members/07+James+Law/07.jpg",
                "Jack Mueller",
                arrayListOf(-1L),
                -1L,
                -1L,
                -1L
            )
        }
        username.text = user.username

        image_view_option_like_title?.text =
            post.likesCount.formatNumberAsReadableFormat()
        image_view_option_comment_title?.text =
            post.commentsCount.formatNumberAsReadableFormat()

        Glide.with(this)
            .load(user.url)
            .thumbnail(0.25f)
            .override(50, 50)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(caption_profile as ShapeableImageView)
    }

    override fun onPause() {
        if(simplePlayer != null){
            pauseVideo()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if(simplePlayer != null){
            releasePlayer()
        }
        super.onDestroy()
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareVideoPlayer() {
        simplePlayer = SimpleExoPlayer.Builder(requireContext())
            .build()
//        cacheDataSourceFactory = CacheDataSourceFactory(simpleCache,
//            DefaultHttpDataSourceFactory(
//                Util.getUserAgent(context,
//                    "exo"))
//        )
    }

    private val playerCallback: Player.EventListener? = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("onPlayerStateChanged playbackState: $playbackState")
        }
    }

    private fun  prepareMedia(linkUrl: String) {
        logError("prepareMedia linkUrl: $linkUrl")

//        val uri = Uri.parse(linkUrl)

        val mediaItem =
            MediaItem.fromUri(linkUrl)

        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
        simplePlayer?.playWhenReady = true

        simplePlayer?.setMediaItem(mediaItem)
        simplePlayer?.prepare()

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
            storyUrl.let { prepareMedia(it) }
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

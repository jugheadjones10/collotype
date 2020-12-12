package com.app.tiktok.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.app.tiktok.R
import com.app.tiktok.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.prefill.PreFillType
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity: BaseActivity(), NavController.OnDestinationChangedListener {
    private val homeViewModel by viewModels<MainViewModel>()

    companion object{
        var bottomNavBar: BottomNavigationView? = null
    }
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavBar = nav_view

        navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        container.cameraDistance = 1000000000000000000000000000f

        navController.addOnDestinationChangedListener(this)

        if (savedInstanceState == null) {
            // Weight values determined experimentally by measuring the number of incurred GCs while
            // scrolling through the various photo grids/lists.
//            val squareLength = getResources().getDisplayMetrics().widthPixels/4
//            val postHeight = getResources().getDisplayMetrics().heightPixels - squareLength
//            val postWidth = getResources().getDisplayMetrics().widthPixels
//            Glide.get(this)
//                .preFillBitmapPool(
//                    PreFillType.Builder(squareLength).setWeight(3),
//                    PreFillType.Builder(postWidth, postHeight).setWeight(2)
//                )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ?: false
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_home -> {
                nav_view.visibility = View.VISIBLE
//                changeStatusBarColor(R.color.colorBlack)
//                val colorDark = ContextCompat.getColorStateList(
//                    this,
//                    R.color.bottom_tab_selector_item_dark
//                )
//
//                val colorBlack = ContextCompat.getColorStateList(
//                    this,
//                    R.color.colorBlack
//                )
//
//                nav_view.backgroundTintList = colorBlack
//                nav_view.itemTextColor = colorDark
//                nav_view.itemIconTintList = colorDark
            }
            else -> {
                nav_view.visibility = View.VISIBLE
//                changeStatusBarColor(R.color.colorWhite)
//                val colorDark = ContextCompat.getColorStateList(
//                    this,
//                    R.color.bottom_tab_selector_item_light
//                )
//
//                val colorWhite = ContextCompat.getColorStateList(
//                    this,
//                    R.color.colorWhite
//                )
//
//                nav_view.backgroundTintList = colorWhite
//                nav_view.itemTextColor = colorDark
//                nav_view.itemIconTintList = colorDark
            }
        }
    }
}
package com.app.tiktok.utils

import android.content.Context
import android.util.DisplayMetrics
import java.util.*

object Utility {
    private val suffixes: NavigableMap<Long, String> = TreeMap()
    private fun setSuffixes() {
        suffixes[1_000L] = "k"
        suffixes[1_000_000L] = "M"
        suffixes[1_000_000_000L] = "B"
        suffixes[1_000_000_000_000L] = "T"
        suffixes[1_000_000_000_000_000L] = "P"
        suffixes[1_000_000_000_000_000_000L] = "E"
    }


    fun formatNumberAsNumberFormat(value: Long): String {
        setSuffixes()
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNumberAsNumberFormat(Long.MIN_VALUE + 1)
        if (value < 0) return "-" + formatNumberAsNumberFormat(-value)
        if (value < 1000) return java.lang.Long.toString(value) //deal with easy case
        val e = suffixes.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value
        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal =
            truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }

    fun pxToDp(px: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density: Float = context
            .resources
            .displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    fun extractS3URLfileType(url: String): String{
        return url.split("/").last().split(".").last().toLowerCase()
    }

    fun isImage(storyUrlType: String): Boolean{
        if (storyUrlType.equals("jpg") || storyUrlType.equals("gif") || storyUrlType.equals("jpeg") || storyUrlType.equals("png") || storyUrlType.equals("webp")) {
            return true
        }else if(storyUrlType.equals("mp4")){
            return false
        }else{
            throw Exception("Unrecognized file type")
        }
    }

    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    fun interpolateColor(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff
        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff
        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }
}
package com.app.tiktok.ui.story.trash;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;

import com.app.tiktok.R;
import com.app.tiktok.ui.story.StoryBunchFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LinearLayoutBottomSheet extends LinearLayout {

    public LinearLayoutBottomSheet(Context context) {
        super(context);
    }

    public LinearLayoutBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private String DEBUG_TAG = "mother";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                if(StoryBunchFragment.getInstance().bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }
                Log.d(DEBUG_TAG, "Dispatched Action was DOWN");
                break;
            case (MotionEvent.ACTION_MOVE):
                if(StoryBunchFragment.getInstance().bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    return false;
                }
                Log.d(DEBUG_TAG, "Dispatched Action was MOVE");
                break;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "Dispatched Action was UP");
                break;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Dispatched Action was CANCEL");
                break;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                break;
            default:
        }
//        return false;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "onTouchEvent Action was DOWN");
                break;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "onTouchEvent Action was MOVE");
                break;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "onTouchEvent Action was UP");
                break;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "onTouchEvent Action was CANCEL");
                break;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "onInterceptTouchEvent Action was DOWN");
                break;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "onInterceptTouchEvent Action was MOVE");
                break;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "onInterceptTouchEvent Action was UP");
                break;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "onInterceptTouchEvent Action was CANCEL");
                break;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                break;
            default:
        }
        return false;
        //return super.onInterceptTouchEvent(ev);
    }
}

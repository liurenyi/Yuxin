package com.lry.songmachine.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureDetector implements GestureDetector.OnGestureListener {

    public static final String TAG = "MyGestureDetector";
    public static boolean isFling = true; //滑动的标志符，为解决gridview的左右滑动事件与点击事件的冲突
    public boolean DEBUG = false;

    private Context mContext;
    private Intent intent;

    private SlideListener slideListener;

    public void setSlideListener(SlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public interface SlideListener {
        void LeftSlideListener();

        void RightSlideListener();
    }

    public MyGestureDetector(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if (DEBUG) {
            Log.e(TAG, "--onDown--");
        }
        isFling = false;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        if (DEBUG) {
            Log.e(TAG, "--onShowPress--");
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (DEBUG) {
            Log.e(TAG, "--onSingleTapUp--");
        }
        isFling = false;
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.e(TAG, "--onLongPress--");
    }

    /**
     * @param motionEvent  第1个ACTION_DOWN MotionEven
     * @param motionEvent1 最后一个ACTION_MOVE MotionEvent
     * @param v            X轴上的移动速度，像素/秒
     * @param v1           Y轴上的移动速度，像素/秒
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if (DEBUG) {
            Log.e(TAG, "--onFling--");
        }
        isFling = true;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float x1 = motionEvent1.getX();
        float y1 = motionEvent1.getY();
        if (x - x1 > 0) {
            if (slideListener != null) {
                slideListener.LeftSlideListener();
            }
            return true;
        } else if (x1 - x > 0) {
            if (slideListener != null) {
                slideListener.RightSlideListener();
            }
            return true;
        }
        return true;
    }
}

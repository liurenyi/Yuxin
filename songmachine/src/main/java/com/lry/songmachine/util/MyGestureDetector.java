package com.lry.songmachine.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureDetector implements GestureDetector.OnGestureListener {

    public static final String TAG = "MyGestureDetector";

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
        Log.e(TAG, "--onDown--");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.e(TAG, "--onShowPress--");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.e(TAG, "--onSingleTapUp--");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.e(TAG, "--onScroll--");
        /*float x = motionEvent.getX();
        float y = motionEvent.getY();
        float x1 = motionEvent1.getX();
        float y1 = motionEvent1.getY();
        Log.e(TAG, "X轴移速" + v);
        if (x - x1 > 0) {
            Log.e(TAG, "左滑");
            *//*intent = new Intent(Constant.KEY_LEFT_SLIDE_ORDER);
            mContext.sendBroadcast(intent);*//*
            if (slideListener != null){
                slideListener.LeftSlideListener();
            }
        } else if (x1 - x > 0) {
            Log.e(TAG, "右滑");
            *//*intent = new Intent(Constant.KEY_RIGHT_SLIDE_ORDER);
            mContext.sendBroadcast(intent);*//*
            if (slideListener!=null){
                slideListener.RightSlideListener();
            }
        }*/
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
        Log.e(TAG, "--onFling--");
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float x1 = motionEvent1.getX();
        float y1 = motionEvent1.getY();
        if (x - x1 > 0) {
            if (slideListener != null) {
                slideListener.LeftSlideListener();
            }
        } else if (x1 - x > 0) {
            if (slideListener != null) {
                slideListener.RightSlideListener();
            }
        }
        return false;
    }
}

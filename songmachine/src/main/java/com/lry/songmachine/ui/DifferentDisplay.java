package com.lry.songmachine.ui;

import android.app.Presentation;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lry.songmachine.R;

import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DifferentDisplay extends Presentation implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    public static final String TAG = "DifferentDisplay";
    public SurfaceView mSurfaceView;
    public SurfaceHolder holder;
    public MediaPlayer mMediaPlayer;

    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.different_display);
        mMediaPlayer = new MediaPlayer();
        mSurfaceView = this.findViewById(R.id.surface_different_display);
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
    }

    //开始播放video，path-->video的路径
    public void startPlayVideo(String path) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
                mMediaPlayer.setVolume(0f, 0f);//静音播放
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "MediaPlayer is null");
        }
    }


    //副屏 - 重唱
    public void Replay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(0); //设置为开始位
        }
    }

    //副屏 - 暂停播放
    public void pauseAndPlay(int position) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
            mMediaPlayer.seekTo(position);
        }
    }

    public void pause(int position) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(position-50);
            mMediaPlayer.pause();
        }
    }

    public void play(int position) {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mMediaPlayer.seekTo(position);
        }
    }

    public void play() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}

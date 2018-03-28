package com.lry.songmachine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lry.songmachine.bean.VideoInfo;
import com.lry.songmachine.rxjava.RxUtils;
import com.lry.songmachine.ui.HomeActivity;
import com.lry.songmachine.util.Method;
import com.lry.songmachine.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WelcomeActivity extends AppCompatActivity {

    public static final String TAG = "liu";
    public Context mContext = WelcomeActivity.this;
    public Intent intent;
    //扫描之后得到的文件集合。
    public static List<VideoInfo> videoInfos = new ArrayList<>();
    //扫描之后得到的文件集合，并把它当做已点歌曲，默认选中。
    public static List<VideoInfo> selectedVideos = new ArrayList<>();
    //需要申请的权限集合。
    private List<String> permissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= 23) {
            checkAppPermission();
        } else {
            initRxJava();
        }
    }

    @SuppressLint("InlinedApi")
    private void checkAppPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            initRxJava();
        }
        if (!permissions.isEmpty()) {
            String[] permission = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions((Activity) mContext, permission, Utils.KEY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.KEY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Method.toast(mContext, "必须同意权限才能正常运行");
                            finish();
                        }
                    }
                    initRxJava();
                }
                break;
        }
    }

    private void initRxJava() {
        Log.e(TAG, "realtime 1 : " + SystemClock.elapsedRealtime());
        if (videoInfos != null || selectedVideos != null) {
            videoInfos.clear();
            selectedVideos.clear();
        }
        final File path = new File(Utils.KEY_SCANNING_PATH);
        Observable.just(path).flatMap(new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File file) {
                return RxUtils.listFiles(file);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<File>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "realtime 3 : " + SystemClock.elapsedRealtime());
                Log.e(TAG, "-->onCompleted()");
                intent = new Intent();
                intent.setClass(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(File file) {
                Log.e(TAG, "realtime 2 : " + SystemClock.elapsedRealtime());
                String filename = Method.formatFileName(file.getName());
                String filePath = file.getPath();
                Log.e(TAG, "filePath: " + filePath);
                VideoInfo info = new VideoInfo(filename, filePath);
                videoInfos.add(info);
                selectedVideos.add(info);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

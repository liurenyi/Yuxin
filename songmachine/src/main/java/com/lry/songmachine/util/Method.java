package com.lry.songmachine.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class Method {

    private static final int A_HOUR = 3600 * 1000;

    private static final int A_MINUTE = 60 * 1000;

    private static final int A_SECOND = 1000;

    private static WindowManager windowManager;

    private static AudioManager audioManager;

    /**
     * toast分装类,减少每次写的麻烦
     *
     * @param context context
     * @param content 吐司的内容
     */
    public static void toast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    // 获取SharedPreferences的值
    public static String getPrefValues(Context context, String tag, String values) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(tag, values);
    }

    // 设置SharedPreferences的值
    public static void setPrefValues(Context context, String tag, String values) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(tag, values);
        editor.apply();
    }

    // 获取SharedPreferences的值
    public static int getPrefValues(Context context, String tag, int values) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(tag, values);
    }

    // 设置SharedPreferences的值
    public static void setPrefValues(Context context, String tag, int values) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(tag, values);
        editor.apply();
    }

    /**
     * 获取视频文件缩略图 API>=8(2.2)
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     * MINI_KIND、MICRO_KIND、FULL_SCREEN_KIND
     */
    public static Bitmap getVideoThumb(String path) {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    /**
     * 获取内部sdcard的根目录
     *
     * @return 返回根目录
     */
    public static String getInnerSDcardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String formatFileName(String fileName) {
        if (fileName.contains(".")) {
            String result = fileName.substring(0, fileName.lastIndexOf("."));
            return result;
        }
        return fileName;
    }

    /**
     * 格式化时间
     *
     * @param time 时间
     * @return 返回值
     */
    public static String formatTime(long time) {
        int hour;
        int minute;
        int second;
        if (time >= A_HOUR) {
            hour = (int) (time / (A_HOUR));
            minute = (int) ((time - (hour * A_HOUR)) / A_MINUTE);
            second = (int) ((time - hour * A_HOUR - minute * A_MINUTE) / A_SECOND);
            return hour + ":" + minute + ":" + second;
        } else if (time < A_HOUR && time >= A_MINUTE) {
            minute = (int) (time / A_MINUTE);
            second = (int) ((time - minute * A_MINUTE) / A_SECOND);
            return (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
        } else if (time >= 0 && time < 10 * 1000) {
            second = (int) (time / A_SECOND);
            return "00:0" + second;
        } else {
            second = (int) (time / A_SECOND);
            return "00:" + second;
        }
    }

    public static int getHeight(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        return height;
    }

    public static int getWidth(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        return width;
    }

    // 获取最大的音量值
    public static int getVolumeMax(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    // 获取当前音量值
    public static int getCurrentVolume(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 调节音量 (增加，减少)
    public static void adjustVolume(Context context, int values) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, values, AudioManager.FLAG_PLAY_SOUND);
    }

    // 获取当前路径文件的名字
    public static String getFileName(String path) {
        File file = new File(path);
        return formatSongName(file.getName());
    }

    // 格式化歌曲的名字，去掉后面的后缀
    private static String formatSongName(String name) {
        return name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
    }

    // 获取当前时间点
    public static long getCurrentTime() {
        return SystemClock.elapsedRealtime();
    }

}

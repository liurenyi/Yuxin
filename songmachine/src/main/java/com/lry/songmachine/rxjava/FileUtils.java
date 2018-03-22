package com.lry.songmachine.rxjava;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class FileUtils {

    public static final String[] VIDEO_EXTENSIONS = { "3gp", "wmv", "ts", "3gp2", "rmvb","mp4","mov", "m4v", "avi", "3gpp", "3gpp2", "mkv", "flv", "divx", "f4v", "rm", "avb", "asf", "ram", "avs", "mpg", "v8", "swf", "m2v", "asx", "ra", "ndivx", "box", "xvid"};

    private static final HashSet<String> mHashVideo;

    private static final double KB = 1024.0;
    private static final double MB = KB * KB;
    private static final double GB = KB * KB * KB;

    static {
        mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));
    }

    /** 是否是音频或者视频 */
    public static boolean isVideoOrAudio(File f) {
        final String ext = getFileExtension(f);
        return mHashVideo.contains(ext);
    }

    public static boolean isVideoOrAudio(String f) {
        final String ext = getUrlExtension(f);
        return mHashVideo.contains(ext);
    }

    public static boolean isVideo(File f) {
        final String ext = getFileExtension(f);
        return mHashVideo.contains(ext);
    }

    /** 获取文件后缀 */
    public static String getFileExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    public static String getUrlExtension(String url) {

        return "";
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static String showFileSize(long size) {
        String fileSize;
        if (size < KB)
            fileSize = size + "B";
        else if (size < MB)
            fileSize = String.format("%.1f", size / KB) + "KB";
        else if (size < GB)
            fileSize = String.format("%.1f", size / MB) + "MB";
        else
            fileSize = String.format("%.1f", size / GB) + "GB";

        return fileSize;
    }
}

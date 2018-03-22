package com.lry.songmachine.bean;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class VideoInfo {

    private String videoName; //视频名字

    private String videoPath; //视频路径

    private String videoAuthor; //视频作者

    private String videoLanguage; //视频语言

    public VideoInfo(String videoName, String videoPath) {
        this.videoName = videoName;
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoAuthor() {
        return videoAuthor;
    }

    public void setVideoAuthor(String videoAuthor) {
        this.videoAuthor = videoAuthor;
    }

    public String getVideoLanguage() {
        return videoLanguage;
    }

    public void setVideoLanguage(String videoLanguage) {
        this.videoLanguage = videoLanguage;
    }

}

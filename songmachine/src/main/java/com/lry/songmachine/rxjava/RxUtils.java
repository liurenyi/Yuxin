package com.lry.songmachine.rxjava;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class RxUtils {
    /**
     * rxjava递归查询内存中的视频文件
     *
     * @param file
     * @return
     */
    public static Observable<File> listFiles(final File file) {
        if (file.isDirectory()) {
            return Observable.from(file.listFiles()).flatMap(new Func1<File, Observable<File>>() {
                @Override
                public Observable<File> call(File file) {
                    return listFiles(file);
                }
            });
        } else {
            return Observable.just(file).filter(new Func1<File, Boolean>() {
                @Override
                public Boolean call(File file) {
                    return file.canRead() && file.exists() && FileUtils.isVideo(file);
                }
            });
        }
    }
}

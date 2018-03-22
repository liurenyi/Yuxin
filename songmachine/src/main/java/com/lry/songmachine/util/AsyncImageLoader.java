package com.lry.songmachine.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class AsyncImageLoader {

    private final Handler handler = new Handler();
    private HashMap<String, SoftReference<Drawable>> imageCache;

    public AsyncImageLoader() {
        imageCache = new HashMap<>();
    }

    public interface ImageCallback {
        void onImageLoad(Integer position, Drawable drawable);

        void onError(Integer position);
    }

    // 传递需要加载的图片的位置以及路径，这里传进来的路径是直接去数据库中的缩略图路径
    public void loadDrawable(final Integer position, final String imageUri, final ImageCallback imageCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadImage(position, imageUri, imageCallback);
            }
        }).start();

    }

    private void loadImage(final Integer position, String imageUri, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUri)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUri);
            final Drawable drawable = softReference.get();
            if (drawable != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageCallback.onImageLoad(position, drawable);
                    }
                });
                return;
            }
        }
        try {
            final Drawable drawable = loadImageFromUri(imageUri);
            if (drawable != null) {
                imageCache.put(imageUri, new SoftReference<>(drawable));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageCallback.onImageLoad(position, drawable);
                    }
                });
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageCallback.onError(position);
                }
            });
            e.printStackTrace();
        }
    }

    // 从Uri中去加载图片的
    public static Drawable loadImageFromUri(String imageUri) throws Exception {
        Bitmap bitmap = Method.getVideoThumb(imageUri); // 获取缩略图（bitmap）
        return new BitmapDrawable(bitmap);
    }

}

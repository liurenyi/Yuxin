package com.lry.songmachine.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lry.songmachine.R;
import com.lry.songmachine.bean.VideoInfo;
import com.lry.songmachine.util.AsyncImageLoader;
import com.lry.songmachine.util.Method;
import com.lry.songmachine.util.Utils;

import java.util.List;

public class ViewSwitchBaseAdapter extends BaseAdapter {

    public static final String TAG = "ViewSwitchBaseAdapter";

    private List<VideoInfo> videoInfos;
    private Context mContext;
    private AsyncImageLoader imageLoader;

    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener {
        void addToList(String name, String path);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public ViewSwitchBaseAdapter(List<VideoInfo> videoInfos, Context mContext) {
        this.videoInfos = videoInfos;
        this.mContext = mContext;
        imageLoader = new AsyncImageLoader();
    }

    @Override
    public int getCount() {
        int currentValue = Method.getPrefValues(mContext, Utils.KEY_CURRENT_SCREEN, -1);
        int toggleValues = Method.getPrefValues(mContext, Utils.KEY_TOGGLE_SCREEN, 0);
        if (currentValue == toggleValues - 1 && videoInfos.size() % Utils.NUMBER_PER_SCREEN != 0) {
            return videoInfos.size() % Utils.NUMBER_PER_SCREEN;
        }
        return Utils.NUMBER_PER_SCREEN; // 每一页显示9 item
    }

    @Override
    public Object getItem(int i) {
        int currentValue = Method.getPrefValues(mContext, Utils.KEY_CURRENT_SCREEN, -1);
        return videoInfos.get(Utils.NUMBER_PER_SCREEN * currentValue + i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final int i1 = Method.getPrefValues(mContext, Utils.KEY_CURRENT_SCREEN, -1);
        ViewHolder holder = null;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item, null);
            holder = new ViewHolder();
            holder.iconImg = view.findViewById(R.id.img_video_info);
            holder.nameTv = view.findViewById(R.id.textView);
            holder.imgSelectedSong = view.findViewById(R.id.img_choose_song);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.iconImg.setBackgroundResource(R.mipmap.ic_launcher); // 设置默认背景图片
        final ViewHolder finalHolder = holder;
        imageLoader.loadDrawable(i, videoInfos.get(i + i1 * Utils.NUMBER_PER_SCREEN).getVideoPath(), new AsyncImageLoader.ImageCallback() {
            @Override
            public void onImageLoad(Integer position, Drawable drawable) {
                finalHolder.iconImg.setBackgroundDrawable(drawable);
            }

            @Override
            public void onError(Integer position) {
                Log.e(TAG, "位置为" + (position + i1 * Utils.NUMBER_PER_SCREEN) + "加载失败!");
            }
        });
        holder.nameTv.setText(videoInfos.get(i + i1 * Utils.NUMBER_PER_SCREEN).getVideoName());
        //设置为增加“已点歌曲”的功能，点击之后可把此歌曲添加到已点列表
        holder.imgSelectedSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = videoInfos.get(i + i1 * Utils.NUMBER_PER_SCREEN).getVideoName();
                String path = videoInfos.get(i + i1 * Utils.NUMBER_PER_SCREEN).getVideoPath();
                Log.e("liu","selected name: " + name + " path: " + path);
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.addToList(name, path);
                }
                Log.e(TAG, videoInfos.get(i + i1 * Utils.NUMBER_PER_SCREEN).getVideoName());
            }
        });
        return view;
    }

    private class ViewHolder {
        ImageView iconImg;
        ImageView imgSelectedSong;
        TextView nameTv;
    }
}

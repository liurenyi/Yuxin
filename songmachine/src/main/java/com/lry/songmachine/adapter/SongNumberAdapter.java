package com.lry.songmachine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lry.songmachine.R;
import com.lry.songmachine.bean.VideoInfo;

import java.util.List;
import java.util.Map;

public class SongNumberAdapter extends BaseAdapter {

    private List<VideoInfo> selectedVideos;
    private LayoutInflater inflater;

    public SongNumberAdapter(Context context, List<VideoInfo> selectedVideos) {
        inflater = LayoutInflater.from(context);
        this.selectedVideos = selectedVideos;
    }

    @Override
    public int getCount() {
        return selectedVideos.size();
    }

    @Override
    public Object getItem(int i) {
        return selectedVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_listview_song_number, null);
            viewHolder.tvSongName = view.findViewById(R.id.tv_song_name);
            viewHolder.tvSongAuthor = view.findViewById(R.id.tv_song_author);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvSongName.setText(selectedVideos.get(i).getVideoName());
        viewHolder.tvSongAuthor.setText(selectedVideos.get(i).getVideoPath());
        return view;
    }

    private class ViewHolder {
        TextView tvSongName;
        TextView tvSongAuthor;
    }

}

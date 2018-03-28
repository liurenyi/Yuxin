package com.lry.songmachine.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lry.songmachine.R;
import com.lry.songmachine.WelcomeActivity;
import com.lry.songmachine.adapter.SongNumberAdapter;
import com.lry.songmachine.adapter.ViewSwitchBaseAdapter;
import com.lry.songmachine.bean.VideoInfo;
import com.lry.songmachine.util.Method;
import com.lry.songmachine.util.MyGestureDetector;
import com.lry.songmachine.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.
        OnCompletionListener {

    public static final String TAG = "HomeActivity";
    public static final boolean DEBUG = true;
    public Context mContext = HomeActivity.this;
    public ViewSwitcher mViewSwitcher;
    public int currentPage = -1;
    public int togglePage;
    public GestureDetector mGestureDetector;
    public MyGestureDetector myGestureDetector;
    public RelativeLayout mUIRelativeLayout;
    public LinearLayout relativeSurface;
    public LinearLayout linearLayout;
    public RadioButton mRadioButton1;
    public RadioButton mRadioButton2;
    public RadioButton mRadioButton3;
    public RadioButton mRadioButton4;
    public RadioButton mRadioButton5;
    public RadioButton mRadioButton6;
    public RadioButton mRadioButton7;
    public RadioButton mRadioButton8;
    public RadioButton mRadioButton9;
    public RadioButton mRadioButton10;
    public RadioButton mRadioButtonSetting, mRadioButtonTuning;
    public RadioGroup radioGroupLeft, radioGroupRight;
    public ImageView imgVolumeDown, imgVolumeUp;
    private TextView selectedNumber, selectedInfo;
    public SeekBar seekBarVolume;
    public SurfaceView surfaceViewMain;
    public SurfaceHolder holder;
    public Intent intent;
    public Dialog dialog;
    public ListView lvSongNumber;
    public SongNumberAdapter snAdapter;
    private int relativeSurfaceWidth;
    private int relativeSurfaceHeight;

    //扫描得到的所有视频文件几个，只用来作为数据展示
    private List<VideoInfo> videoInfos;
    //已点歌曲集合
    private List<VideoInfo> selectedVideos;
    private ViewSwitchBaseAdapter adapter;
    private MediaPlayer mMediaPlayer;
    private int tempVolume = 0; // 音量为0
    private String prevVideoPath; //准备预览的视频路径
    private static final String KEY_PLAY_MODE = "play_mode"; //播放的模式，小窗口or全屏
    private static final String KEY_PLAY_MODE_SMALL_VALUES = "small"; //小窗口模式设置值
    private static final String KEY_PLAY_MODE_FULL_VALUES = "full"; //全屏设置值
    private String[] stringArray;
    private boolean isYuanChang = true;

    public Button btnOneFunction, btnTwoFunction, btnThreeFunction, btnFourFunction, btnFixFunction,
            btnSixFunction, btnSevenFunction;
    public SeekBar seekBarBassBoost;
    public TextView tvPresetReverb;

    public Button btnPrev, btnNext;
    public TextView tvPageNumber;

    // 关于双屏异显
    public DisplayManager mDisplayManager;
    public Display[] displays;
    public DifferentDisplay mDifferentDisplay;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    String format = (String) msg.obj;
                    selectedInfo.setText(format);
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        videoInfos = WelcomeActivity.videoInfos;
        selectedVideos = WelcomeActivity.selectedVideos;//选中歌曲的数据集合

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);

        myGestureDetector = new MyGestureDetector(mContext);
        mGestureDetector = new GestureDetector(mContext, myGestureDetector);
        myGestureDetector.setSlideListener(new MyGestureDetector.SlideListener() {
            @Override
            public void LeftSlideListener() {
                next();
            }

            @Override
            public void RightSlideListener() {
                prev();
            }
        });

        Method.setPrefValues(mContext, KEY_PLAY_MODE, KEY_PLAY_MODE_SMALL_VALUES); // 设置初始值，窗口的模式

        /**--------------------双屏异显 start-------------------------**/
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displays = mDisplayManager.getDisplays();
        Log.e(TAG, "displays: " + displays.length);
        if (mDifferentDisplay == null && displays.length > 1) {
            mDifferentDisplay = new DifferentDisplay(mContext, displays[displays.length - 1]);
            mDifferentDisplay.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mDifferentDisplay.show();
        }
        /**--------------------双屏异显 end-------------------------**/
        initOneView();
        initTwoView();
        getTogglePage();
        PlayDefaultVideo();
    }

    @SuppressLint("SetTextI18n")
    private void initOneView() {
        mRadioButton1 = this.findViewById(R.id.radio_left_1);
        mRadioButton2 = this.findViewById(R.id.radio_left_2);
        mRadioButton3 = this.findViewById(R.id.radio_left_3);
        mRadioButton4 = this.findViewById(R.id.radio_left_4);
        mRadioButton5 = this.findViewById(R.id.radio_left_5);
        mRadioButton6 = this.findViewById(R.id.radio_right_1);
        mRadioButton7 = this.findViewById(R.id.radio_right_2);
        mRadioButton8 = this.findViewById(R.id.radio_right_3);
        mRadioButton9 = this.findViewById(R.id.radio_right_4);
        mRadioButton10 = this.findViewById(R.id.radio_right_5);
        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mRadioButton3.setOnClickListener(this);
        mRadioButton4.setOnClickListener(this);
        mRadioButton5.setOnClickListener(this);
        mRadioButton6.setOnClickListener(this);
        mRadioButton7.setOnClickListener(this);
        mRadioButton8.setOnClickListener(this);
        mRadioButton9.setOnClickListener(this);
        mRadioButton10.setOnClickListener(this);
        mRadioButtonSetting = this.findViewById(R.id.radio_btn_settings);
        mRadioButtonTuning = this.findViewById(R.id.radio_btn_tuning);
        mRadioButtonSetting.setOnClickListener(this);
        mRadioButtonTuning.setOnClickListener(this);

        btnPrev = this.findViewById(R.id.btn_prev);
        btnNext = this.findViewById(R.id.btn_next);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvPageNumber = this.findViewById(R.id.tv_page_number);

        int currentVolume = Method.getCurrentVolume(mContext);
        updateIconState(currentVolume > 0); //更新音量键的图标显示,如果音量不为0，显示正常图标

        selectedNumber = this.findViewById(R.id.tv_selected_number);
        selectedNumber.setText(String.valueOf(selectedVideos.size()));
        selectedInfo = this.findViewById(R.id.tv_selected_song_info); // 跑马灯效果的textview
        selectedInfo.setSelected(true); // 开启跑马灯效

        stringArray = getResources().getStringArray(R.array.ReverbName);

        relativeSurface = this.findViewById(R.id.relative_surface);
        relativeSurface.setOnClickListener(this);
        radioGroupLeft = this.findViewById(R.id.radio_group_left);
        radioGroupRight = this.findViewById(R.id.radio_group_right);

        linearLayout = this.findViewById(R.id.linearLayout);

        surfaceViewMain = this.findViewById(R.id.surface_main);
        surfaceViewMain.setKeepScreenOn(true);
        holder = surfaceViewMain.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(surfaceHolder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    // 中间数据展示部分的控件初始化
    private void initTwoView() {

        mUIRelativeLayout = this.findViewById(R.id.relative_layout_ui);
        /*mUIRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });*/

        mViewSwitcher = this.findViewById(R.id.view_switcher);
        mViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public View makeView() {
                GridView view = (GridView) getLayoutInflater().inflate(R.layout.slide_gridview, null);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        mGestureDetector.onTouchEvent(motionEvent);
                        return false;
                    }
                });
                view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!MyGestureDetector.isFling) { //isFling为false，表示执行了滑动事件，此时不响应点击事件
                            Log.e(MyGestureDetector.TAG, "--onItemClick--");
                            prevVideoPath = videoInfos.get(currentPage * Utils.NUMBER_PER_SCREEN + i).getVideoPath();
                            String name = videoInfos.get(currentPage * Utils.NUMBER_PER_SCREEN + i).getVideoName();
                            Method.toast(mContext, name);
                            checkAppPermission();
                        }
                    }
                });
                return view;
            }
        });
    }

    //检查开启悬浮框所需要的权限
    @SuppressLint("NewApi")
    private void checkAppPermission() {
        if (!Settings.canDrawOverlays(mContext)) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, Utils.KEY_DIALOG_PERMISSION_REQUEST_CODE);
        } else {
            startFService();
        }
    }

    //开启悬浮窗口
    private void startFService() {
        intent = new Intent(mContext, FloatWindowService.class);
        intent.putExtra("video_path", prevVideoPath);
        startService(intent);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.KEY_DIALOG_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(mContext)) {
                Method.toast(mContext, "授权失败");
            } else {
                Method.toast(mContext, "授权成功");
                startFService();
            }
        }
    }

    //获取当前一共有多少数据，从而得到分成多少页
    private void getTogglePage() {
        togglePage = videoInfos.size() % Utils.NUMBER_PER_SCREEN == 0 ?
                videoInfos.size() / Utils.NUMBER_PER_SCREEN :
                videoInfos.size() / Utils.NUMBER_PER_SCREEN + 1;
        Method.setPrefValues(mContext, Utils.KEY_TOGGLE_SCREEN, togglePage);
        adapter = new ViewSwitchBaseAdapter(videoInfos, mContext);
        adapter.setOnItemSelectedListener(new ViewSwitchBaseAdapter.OnItemSelectedListener() {
            @Override
            public void addToList(String name, String path) {
                VideoInfo info = new VideoInfo(name, path);
                selectedVideos.add(info);
                selectedNumber.setText(String.valueOf(selectedVideos.size())); //只能传入string型，传入int型会报错
                Method.toast(mContext, getString(R.string.app_add_song_to_list));
            }
        });
        next();
    }

    // 开始播放默认的歌曲
    private void PlayDefaultVideo() {
        String defaultUri = "/storage/emulated/0/Movies/烟花易冷.mp4";
        File file = new File(defaultUri);
        if (!file.exists()) {
            if (videoInfos.size() > 0) {
                defaultUri = videoInfos.get(0).getVideoPath();
            } else {
                Log.e(TAG, "没有视频可播放");
            }
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(defaultUri);
            curPlaySong = defaultUri;
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint({"StringFormatMatches", "NewApi"})
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //副屏播放在前面可减少主副屏之间的误差
                    mDifferentDisplay.startPlayVideo(curPlaySong); //开启副屏播放
                    mMediaPlayer.start();
                    setupPresetReverb(mMediaPlayer);
                    setupBassBoost(mMediaPlayer);
                    getVideoInfo();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Equalizer mEqualizer; // 定义系统的均衡器
    private PresetReverb mPresetReverb; // 定义系统的预设音场控制器
    private List<Short> reverbNames = new ArrayList<>();
    private BassBoost mBassBoost;

    // 初始化音场
    private void setupPresetReverb(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
            mEqualizer.setEnabled(true); // 开启均衡器
            mPresetReverb = new PresetReverb(0, mediaPlayer.getAudioSessionId());
            mPresetReverb.setEnabled(true); //开启预设音场控制器
            for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
                // <-- Normal,Classical,Dance,Flat,Folk,Heavy Metal,Hip Hop,Jazz,Pop,Rock -->
                //String presetName = mEqualizer.getPresetName(i);
                reverbNames.add(i);
            }
            String currentReverb = Method.getPrefValues(mContext, "currentReverb", null);
            if (currentReverb != null) {
                mPresetReverb.setPreset(Short.parseShort(currentReverb));
            } else {
                mPresetReverb.setPreset((short) 0);
            }
        }
    }

    // 初始化重低音控制器
    private void setupBassBoost(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mBassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
            mBassBoost.setEnabled(true);
        }
    }

    // 获取当前播放的歌曲，以及下一曲的歌曲,在导航栏进行展示
    private void getVideoInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String curFileName = Method.getFileName(curPlaySong);
                String nextFileName;
                String format;
                if (selectedVideos.size() > 0) {
                    nextFileName = Method.getFileName(selectedVideos.get(0).getVideoName());
                    format = getResources().getString(R.string.
                            ui_main_selected_song_info_text, curFileName, nextFileName);
                } else {
                    format = getResources().getString(R.string.
                            ui_main_selected_song_info_text_1, curFileName);
                }
                Message message = new Message();
                message.what = 10;
                message.obj = format;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void next() {
        if (currentPage < togglePage - 1) {
            currentPage++;
            if (DEBUG) {
                Log.e(TAG, "当前第" + (currentPage + 1) + "页");
            }
            tvPageNumber.setText(getString(R.string.app_textview_page_number, currentPage + 1, togglePage));
            Method.setPrefValues(mContext, Utils.KEY_CURRENT_SCREEN, currentPage);
            ((GridView) mViewSwitcher.getNextView()).setAdapter(adapter);
            mViewSwitcher.setInAnimation(mContext, R.anim.slide_in_right);
            mViewSwitcher.setOutAnimation(mContext, R.anim.slide_out_left);

            mViewSwitcher.showNext();
        }
    }

    private void prev() {
        if (currentPage > 0) {
            currentPage--;
            if (DEBUG) {
                Log.e(TAG, "当前第" + (currentPage + 1) + "页");
            }
            tvPageNumber.setText(getString(R.string.app_textview_page_number, currentPage + 1, togglePage));
            Method.setPrefValues(mContext, Utils.KEY_CURRENT_SCREEN, currentPage);
            ((GridView) mViewSwitcher.getNextView()).setAdapter(adapter);
            mViewSwitcher.setInAnimation(mContext, R.anim.slide_in_left);
            mViewSwitcher.setOutAnimation(mContext, R.anim.slide_out_right);
            mViewSwitcher.showPrevious();
        }
    }

    private void release() {
        // 释放所有资源
        if (mBassBoost != null) {
            mBassBoost.release();
        }
        if (mPresetReverb != null) {
            mPresetReverb.release();
        }
        if (mEqualizer != null) {
            mEqualizer.release();
        }
        for (int i = 0; i < staticBands; i++) {
            Method.setPrefValues(mContext, "bar" + i, 0);
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radio_left_1:
                break;
            case R.id.radio_left_2:
                Replay();
                break;
            case R.id.radio_left_3:
                int volume = Method.getCurrentVolume(mContext);
                if (volume > 0) {
                    tempVolume = volume;
                    Method.adjustVolume(mContext, 0);
                    updateIconState(false);
                } else if (volume == 0 && tempVolume != 0) {
                    Method.adjustVolume(mContext, tempVolume);
                    updateIconState(true);
                } else if (volume == 0 && tempVolume == 0) {
                    Method.toast(mContext, "已经静音了");
                }
                break;
            case R.id.radio_left_4:
                pauseAndPlay();
                break;
            case R.id.radio_left_5:
                adjustVolumeLayout();
                break;
            case R.id.radio_right_1: // 切歌功能按键
                cutSongs();
                break;
            case R.id.radio_right_2: // 伴唱功能按键
                VA();
                break;
            case R.id.radio_right_3: // 已点功能按键
                showSongList();
                break;
            case R.id.radio_right_4: // 气氛功能按键
                break;
            case R.id.radio_right_5: // 返回功能按键
                break;
            case R.id.radio_btn_settings:
                createEqualizerLayout(); // 创建一个调节均衡器的界面
                break;
            case R.id.radio_btn_tuning:
                createCustomDialog(mContext, R.layout.layout_tuning); // 启动一个自定义的dialog
                break;
            case R.id.img_volume_down:
                int currentVolume = Method.getCurrentVolume(mContext);
                if (currentVolume > 0) {
                    Method.adjustVolume(mContext, currentVolume - 1);
                    seekBarVolume.setProgress(currentVolume - 1);
                } else if (currentVolume == 0) {
                    Method.toast(mContext, getString(R.string.ui_main_volume_text));
                    updateIconState(false);
                } else {
                    Method.toast(mContext, getString(R.string.ui_main_volume_text_1));
                }
                break;
            case R.id.img_volume_up:
                int currentVolume1 = Method.getCurrentVolume(mContext);
                int volumeMax = Method.getVolumeMax(mContext);
                if (currentVolume1 < volumeMax) {
                    Method.adjustVolume(mContext, currentVolume1 + 1);
                    seekBarVolume.setProgress(currentVolume1 + 1);
                    updateIconState(true);
                } else {
                    Method.toast(mContext, getString(R.string.ui_main_volume_text_2));
                }
                break;
            case R.id.btn_function_one:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(0));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[0]);
                }
                break;
            case R.id.btn_function_two:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(1));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[1]);
                }
                break;
            case R.id.btn_function_three:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(2));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[2]);
                }
                break;
            case R.id.btn_function_four:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(3));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[3]);
                }
                break;
            case R.id.btn_function_five:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(4));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[4]);
                }
                break;
            case R.id.btn_function_six:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(5));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[5]);
                }
                break;
            case R.id.btn_function_seven:
                if (reverbNames.size() > 0) {
                    mPresetReverb.setPreset(reverbNames.get(6));
                    short preset = mPresetReverb.getPreset();
                    Method.setPrefValues(mContext, "currentReverb", preset + "");
                    tvPresetReverb.setText(stringArray[6]);
                }
                break;
            case R.id.relative_surface: // 点击surfaceview触发全屏功能
                resetSize();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_prev:
                prev();
                break;
        }
    }

    //更新图标的显示状态
    private void updateIconState(boolean b) {
        mRadioButton3.setCompoundDrawablesWithIntrinsicBounds(null, b ? getResources().
                getDrawable(R.drawable.ic_volume_up_black) : getResources().
                getDrawable(R.drawable.ic_volume_off_black), null, null);
    }

    //重唱
    @SuppressLint("NewApi")
    private void Replay() {
        mDifferentDisplay.Replay(); // 副屏也同时执行重唱功能
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(0);
        }
    }

    //暂停,播放视频
    @SuppressLint("NewApi")
    private void pauseAndPlay() {
        if (mMediaPlayer.isPlaying()) {
            int position = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
            mDifferentDisplay.pause(position);
        } else {
            mMediaPlayer.start();
            mDifferentDisplay.play(mMediaPlayer.getCurrentPosition());
        }
        mRadioButton4.setCompoundDrawablesWithIntrinsicBounds(null,
                mMediaPlayer.isPlaying() ? getResources().getDrawable(R.drawable.ic_pause_circle_filled_black) :
                        getResources().getDrawable(R.drawable.ic_play_arrow_black), null, null);
        mRadioButton4.setText(mMediaPlayer.isPlaying() ? getResources().getString(R.string.radio_button_left_4_text_1) :
                getResources().getString(R.string.radio_button_left_4_text));
    }

    private String curPlaySong = null;

    // 切歌功能
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void cutSongs() {
        release();
        if (selectedVideos.size() > 0) { //表示有已点的歌曲
            String path = selectedVideos.get(0).getVideoPath();
            mMediaPlayer.reset(); // 重置mMediaPlayer
            try {
                mMediaPlayer.setDataSource(path);
                curPlaySong = path;
                mMediaPlayer.prepare();
                //mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PTOS();
            // 解决上一首歌曲处于暂停状态，点击切歌，图标不改变。
            mRadioButton4.setCompoundDrawablesWithIntrinsicBounds(null, getResources().
                    getDrawable(R.drawable.ic_pause_circle_filled_black), null, null);
            mRadioButton4.setText(getResources().getString(R.string.radio_button_left_4_text));
            selectedVideos.remove(0);
            selectedNumber.setText(String.valueOf(selectedVideos.size()));
        } else {
            Method.toast(mContext, getString(R.string.tv_songs_alter_text));
        }
    }

    /**
     * VA --> vocal accompaniment 伴唱功能的方法
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void VA() {
        if (mMediaPlayer != null) {
            MediaPlayer.TrackInfo[] trackInfo = mMediaPlayer.getTrackInfo();
            if (trackInfo != null && trackInfo.length > 0) {
                if (DEBUG) {
                    Log.e(TAG, "TrackInfo length: " + trackInfo.length);
                }
                if (trackInfo.length >= 3) { // 如果大于等于3，则视频文件支持伴唱原唱功能
                    if (isYuanChang) {
                        mMediaPlayer.selectTrack(2); // 伴唱
                        isYuanChang = false;
                        mRadioButton7.setText("原唱");
                    } else {
                        mMediaPlayer.selectTrack(1); // 原唱
                        isYuanChang = true;
                        mRadioButton7.setText("伴唱");
                    }
                }
            }
        }
    }

    /**
     * PTOS --> Perform the original song
     * 强制执行原唱功能
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void PTOS() {
        MediaPlayer.TrackInfo[] trackInfo = mMediaPlayer.getTrackInfo();
        if (trackInfo != null && trackInfo.length > 0) {
            mMediaPlayer.selectTrack(1); // 切换到原唱功能
            isYuanChang = true;
            mRadioButton7.setText("伴唱");
        }
    }

    // 自定义的音量调节布局
    private void adjustVolumeLayout() {
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(this).inflate(R.layout.volume_adjust, null);
        imgVolumeDown = inflate.findViewById(R.id.img_volume_down);
        imgVolumeUp = inflate.findViewById(R.id.img_volume_up);
        imgVolumeDown.setOnClickListener(this);
        imgVolumeUp.setOnClickListener(this);
        seekBarVolume = inflate.findViewById(R.id.seek_bar_volume);
        // seek_bar监听事件
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0 && i < 75) { // STREAM_MUSIC的最大值为75，此处直接写75，减去频繁调用方法
                    Method.adjustVolume(mContext, i);
                    updateIconState(true);
                }
                if (i == 0) { //如果音量调到0,则图标显示为静音图标
                    updateIconState(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarVolume.setMax(Method.getVolumeMax(mContext));
        seekBarVolume.setProgress(Method.getCurrentVolume(mContext));
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void showSongList() { // 显示已点歌曲的详细情况
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(mContext).inflate(R.layout.selected_song_number, null);
        lvSongNumber = inflate.findViewById(R.id.lv_song_number);
        snAdapter = new SongNumberAdapter(mContext, selectedVideos);
        lvSongNumber.setAdapter(snAdapter);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        WindowManager manager = dialog.getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        //window.setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = display.getWidth() / 2;   // 弹出框宽度
        layoutParams.height = display.getHeight() / 2;
        window.setAttributes(layoutParams);
        dialog.show();
    }

    // 重新绘制SurfaceView的布局，达到全屏效果。
    private void resetSize() {
        String values = Method.getPrefValues(mContext, KEY_PLAY_MODE, null);
        if (values != null && values.equals(KEY_PLAY_MODE_SMALL_VALUES)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.
                    MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeSurface.setLayoutParams(params);
            // 开启动画
            relativeSurface.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.layout_enlarge));
            radioGroupRight.setVisibility(View.GONE);
            radioGroupLeft.setVisibility(View.GONE);
            selectedNumber.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            Method.setPrefValues(mContext, KEY_PLAY_MODE, KEY_PLAY_MODE_FULL_VALUES);
        } else if (values != null && values.equals(KEY_PLAY_MODE_FULL_VALUES)) {
            // 443,200,测量小窗口模式，控件的宽高。
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(relativeSurfaceWidth,
                    relativeSurfaceHeight);
            //relativeSurface.setAnimation(AnimationUtils.loadAnimation(context, R.anim.layout_narrow));
            relativeSurface.setLayoutParams(params);

            radioGroupLeft.setVisibility(View.VISIBLE);
            radioGroupRight.setVisibility(View.VISIBLE);
            selectedNumber.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            Method.setPrefValues(mContext, KEY_PLAY_MODE, KEY_PLAY_MODE_SMALL_VALUES);
        }
    }

    // 创建自定义的dialog弹出框,调音的dialog
    public void createCustomDialog(Context mContext, int resId) {
        String currentReverb = Method.getPrefValues(mContext, "currentReverb", null);
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(mContext).inflate(resId, null);
        if (resId == R.layout.layout_tuning) {
            btnOneFunction = inflate.findViewById(R.id.btn_function_one);
            btnTwoFunction = inflate.findViewById(R.id.btn_function_two);
            btnThreeFunction = inflate.findViewById(R.id.btn_function_three);
            btnFourFunction = inflate.findViewById(R.id.btn_function_four);
            btnFixFunction = inflate.findViewById(R.id.btn_function_five);
            btnSixFunction = inflate.findViewById(R.id.btn_function_six);
            btnSevenFunction = inflate.findViewById(R.id.btn_function_seven);

            tvPresetReverb = inflate.findViewById(R.id.tv_preset_reverb);

            btnOneFunction.setOnClickListener(this);
            btnTwoFunction.setOnClickListener(this);
            btnThreeFunction.setOnClickListener(this);
            btnFourFunction.setOnClickListener(this);
            btnFixFunction.setOnClickListener(this);
            btnSixFunction.setOnClickListener(this);

            btnSevenFunction.setOnClickListener(this);

            seekBarBassBoost = inflate.findViewById(R.id.seek_bar_bass_boost);
            seekBarBassBoost.setMax(1000);
            seekBarBassBoost.setProgress(0);
            seekBarBassBoost.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mBassBoost.setStrength((short) i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            dialog.setContentView(inflate);
        }
        dialog.show();
        updateReverbState(currentReverb);
    }

    public static int staticBands = 5;//默认5条seekbar

    // 创建均衡器可调节界面
    @SuppressLint("SetTextI18n")
    private void createEqualizerLayout() {
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);

        LinearLayout mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setBackgroundResource(R.drawable.ic_layout_tuning_bakcgorund);
        mLinearLayout.setPadding(20, 20, 20, 20);

        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        TextView bbTitle = new TextView(mContext);
        bbTitle.setText("均衡器:");
        bbTitle.setTextColor(Color.WHITE);
        mLinearLayout.addView(bbTitle);

        final TextView showTitle = new TextView(mContext);
        showTitle.setTextColor(Color.WHITE);
        mLinearLayout.addView(showTitle);

        // 获取均衡控制器支持最小值和最大值
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围
        // 获取均衡控制器支持的所有频率
        short bands = mEqualizer.getNumberOfBands();
        staticBands = bands;
        if (DEBUG) {
            Log.e("liu", "minEQLevel: " + minEQLevel + " maxEQLevel: " + maxEQLevel + " " +
                    "Equalizer bands is " + bands);
        }
        for (short i = 0; i < bands; i++) {

            TextView eqTextView = new TextView(mContext);
            // 创建一个TextView，用于显示频率
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            // 设置该均衡控制器的频率
            eqTextView.setText((mEqualizer.getCenterFreq(i) / 1000) + "Hz");
            eqTextView.setTextColor(Color.WHITE);
            mLinearLayout.addView(eqTextView);

            // 创建一个水平排列组件的LinearLayout
            LinearLayout tmpLayout = new LinearLayout(mContext);
            tmpLayout.setOrientation(LinearLayout.HORIZONTAL);

            // 创建显示均衡控制器最小值的TextView
            TextView minDbTextView = new TextView(mContext);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最小值
            minDbTextView.setText((minEQLevel / 100) + "dB");
            minDbTextView.setTextColor(Color.WHITE);
            // 创建显示均衡控制器最大值的TextView

            TextView maxDbTextView = new TextView(mContext);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最大值
            maxDbTextView.setText((maxEQLevel / 100) + "dB");
            maxDbTextView.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams layoutParams = new
                    LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

            // 定义SeekBar做为调整工具
            final SeekBar bar = new SeekBar(mContext);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            //bar.setProgress(mEqualizer.getBandLevel(i));
            int prefValues = Method.getPrefValues(mContext, "bar" + i, 0);
            //默认设置为0
            //bar.setProgress((maxEQLevel - minEQLevel) / 2);
            bar.setProgress(prefValues - minEQLevel);
            final short brand = i;
            // 为SeekBar的拖动事件设置事件监听器
            bar.setOnSeekBarChangeListener(new SeekBar
                    .OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 设置该频率的均衡值
                    mEqualizer.setBandLevel(brand, (short) (progress + minEQLevel));
                    String barTag = "bar" + brand;
                    int barValue = (progress + minEQLevel);
                    Method.setPrefValues(mContext, barTag, barValue);
                    showTitle.setVisibility(View.VISIBLE);
                    showTitle.setText((progress + minEQLevel) / 100 + "dB");

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            // 使用水平排列组件的LinearLayout“盛装”3个组件
            tmpLayout.addView(minDbTextView);
            tmpLayout.addView(bar);
            tmpLayout.addView(maxDbTextView);
            // 将水平排列组件的LinearLayout添加到myLayout容器中
            mLinearLayout.addView(tmpLayout);
        }
        dialog.setContentView(mLinearLayout);
        Window window = dialog.getWindow();
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = display.getWidth() * 3 / 4;
        params.height = display.getHeight() * 3 / 4;
        window.setAttributes(params);
        dialog.show();
    }

    private void updateReverbState(String currentReverb) {
        Log.e("liu", "currentReverb: " + currentReverb);
        if (currentReverb == null || currentReverb.equals("0")) {
            tvPresetReverb.setText(stringArray[0]);
        } else if (currentReverb.equals("1")) {
            tvPresetReverb.setText(stringArray[1]);
        } else if (currentReverb.equals("2")) {
            tvPresetReverb.setText(stringArray[2]);
        } else if (currentReverb.equals("3")) {
            tvPresetReverb.setText(stringArray[3]);
        } else if (currentReverb.equals("4")) {
            tvPresetReverb.setText(stringArray[4]);
        } else if (currentReverb.equals("5")) {
            tvPresetReverb.setText(stringArray[5]);
        } else if (currentReverb.equals("6")) {
            tvPresetReverb.setText(stringArray[6]);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        relativeSurfaceWidth = relativeSurface.getWidth();
        relativeSurfaceHeight = relativeSurface.getHeight();
    }

    @SuppressLint("NewApi")
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e(TAG, "歌曲播放完成！");
        release();
        if (selectedVideos.size() > 0) {
            cutSongs();
        } else {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(curPlaySong);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        release();
    }
}

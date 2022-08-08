package com.kjsc.ijkplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class VideoViewForTv extends IjkVideoView {
    TextView tvNowTime, tvAllTime;
    SeekBar seekBar;
    boolean isSeek = false;
    ImageView ivPlayStatus;
    LinearLayout control;

    int controlTimeout = 5000;

    public static final int UPDATE_WHAT = 0;
    public static final int HIDE_CONTROL_WHAT = 2;

    public VideoViewForTv(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoViewForTv(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoViewForTv(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_view_for_tv, this);
        tvNowTime = findViewById(R.id.nowTime);
        tvAllTime = findViewById(R.id.allTime);
        seekBar = findViewById(R.id.seek_bar);
        ivPlayStatus = findViewById(R.id.play_status);
        control = findViewById(R.id.control);
        setUserControl(true);
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    public String millis2String(long mills) {
        return simpleDateFormat.format(new Date(mills));
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_WHAT:
                    if (isPlaying()) {
                        int duration = getDuration();
                        if (duration != seekBar.getMax()) {
                            String allTime = millis2String(duration);
                            seekBar.setMax(duration);
                            tvAllTime.setText(allTime);
                        }

                        int currentPosition = getCurrentPosition();
                        String curTime = millis2String(currentPosition);
                        tvNowTime.setText(curTime);
                        if (!isSeek) {
                            seekBar.setProgress(currentPosition);
                        }
                    }
                    handler.sendEmptyMessageDelayed(UPDATE_WHAT, 500);
                    break;
                case HIDE_CONTROL_WHAT:
                    hideControl();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onPrepared(IMediaPlayer mp) {
        super.onPrepared(mp);
        start();
        handler.sendEmptyMessage(UPDATE_WHAT);
        showControl();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("dispatchKeyEvent isFocused=" + seekBar.isFocused());
        if (controlFocusable) {
            showControl();
            int action = event.getAction();
            int keycode = event.getKeyCode();
            if (action == KeyEvent.ACTION_DOWN) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        isSeek = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (isPlaying()) {
                            pause();
                        } else {
                            start();
                        }
                        return true;
                }
            } else if (action == KeyEvent.ACTION_UP) {
                switch (keycode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        isSeek = false;
                        seekTo(seekBar.getProgress());
                        start();
                        break;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void pause() {
        super.pause();
        ivPlayStatus.setVisibility(VISIBLE);
    }

    @Override
    public void start() {
        super.start();
        ivPlayStatus.setVisibility(GONE);
    }

    public void setControlTimeout(int timeout) {
        controlTimeout = timeout;
    }

    public void showControl() {
        if (useControl) {
            control.setVisibility(VISIBLE);
            seekBar.requestFocus();
            if (controlTimeout > 0) {
                handler.removeMessages(HIDE_CONTROL_WHAT);
                handler.sendEmptyMessageDelayed(HIDE_CONTROL_WHAT, controlTimeout);
            }
        }
    }


    public void hideControl() {
        control.setVisibility(INVISIBLE);
    }

    boolean controlFocusable = true;

    public void setControlFocus(boolean needControl) {
        controlFocusable = needControl;
        if (controlFocusable) {
            setFocusable(true);
            seekBar.setFocusable(true);
        } else {
            setFocusable(false);
            seekBar.setFocusable(false);
        }
    }

    boolean useControl = true;

    public void setUserControl(boolean useControl) {
        this.useControl = useControl;
        if (!useControl) {
            setControlFocus(false);
        }
    }
}

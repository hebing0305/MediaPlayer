package com.kjsc.ijkplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.TimeBar;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerView extends IjkVideoView {
    TextView tvNowTime, tvAllTime;
    CustomProgressBar seekBar;
    ImageView ivPlayStatus;
    LinearLayout control;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    int controlTimeout = 5000;

    public static final int UPDATE_WHAT = 0;
    public static final int HIDE_CONTROL_WHAT = 2;

    public PlayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        LayoutInflater.from(getContext()).inflate(R.layout.video_view_for_tv, this);
        tvNowTime = findViewById(R.id.nowTime);
        tvAllTime = findViewById(R.id.allTime);
        seekBar = findViewById(R.id.seek_bar);
        ivPlayStatus = findViewById(R.id.play_status);
        control = findViewById(R.id.control);
        seekBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                System.out.println(">>>>>>onScrubStart");
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                System.out.println(">>>>>>onScrubMove");
                String curTime = millis2String(position);
                tvNowTime.setText(curTime);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                System.out.println(">>>>>>onScrubStop position=" + position);
                seekTo((int) position);
                start();

            }
        });
        setUserControl(true);
        setControlFocus(true);
    }


    public String millis2String(long mills) {

        return simpleDateFormat.format(new Date(mills));
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_WHAT:
                    if (isPlaying()) {
                        if (!seekBar.scrubbing) {
                            seekBar.setPosition(getCurrentPosition());
                            String curTime = millis2String(seekBar.position);
                            tvNowTime.setText(curTime);
                        }
                        seekBar.setBufferedPosition((long) ((getBufferPercentage() / 100f) * seekBar.duration));
                        System.out.println(">>>>>>getBufferPercentage()=" + getBufferPercentage());
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
        int duration = getDuration();
        String allTime = millis2String(duration);
        seekBar.setDuration(duration);
        tvAllTime.setText(allTime);
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
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (isPlaying()) {
                            pause();
                        } else {
                            start();
                        }
                        return true;
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

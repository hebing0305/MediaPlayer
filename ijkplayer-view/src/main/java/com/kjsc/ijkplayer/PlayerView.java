package com.kjsc.ijkplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.TimeBar;
import com.kjsc.ijkplayer.databinding.PlayerViewBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerView extends IjkVideoView {
    PlayerViewBinding binding;
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
        binding = PlayerViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        LayoutInflater.from(getContext()).inflate(R.layout.player_view, this);
        setUserControl(true);
        setControlFocus(true);
        addListener(listener);
        binding.seekBar.addListener(onScrubListener);
    }

    TimeBar.OnScrubListener onScrubListener = new TimeBar.OnScrubListener() {
        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
            System.out.println(">>>>>>onScrubStart");
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            System.out.println(">>>>>>onScrubMove");
            String curTime = millis2String(position);
            binding.nowTime.setText(curTime);
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            System.out.println(">>>>>>onScrubStop position=" + position);
            seekTo((int) position);
            start();
        }
    };

    PlayerListener listener = new PlayerListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    binding.progressbar.setVisibility(VISIBLE);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    binding.progressbar.setVisibility(GONE);
                    break;
            }
            return super.onInfo(iMediaPlayer, i, i1);
        }

        @Override
        public void onPrepared(IMediaPlayer mp) {
            super.onPrepared(mp);
            start();
            int duration = getDuration();
            String allTime = millis2String(duration);
            binding.seekBar.setDuration(duration);
            binding.allTime.setText(allTime);
            handler.sendEmptyMessage(UPDATE_WHAT);
            showControl();
            binding.progressbar.setVisibility(GONE);
        }
    };

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        super.setVideoURI(uri, headers);
        binding.progressbar.setVisibility(VISIBLE);
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
                        if (!binding.seekBar.scrubbing) {
                            binding.seekBar.setPosition(getCurrentPosition());
                            String curTime = millis2String(binding.seekBar.position);
                            binding.nowTime.setText(curTime);
                        }
                        binding.seekBar.setBufferedPosition((long) ((getBufferPercentage() / 100f) * binding.seekBar.duration));
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
    public boolean dispatchKeyEvent(KeyEvent event) {
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
        binding.playStatus.setVisibility(VISIBLE);
    }

    @Override
    public void start() {
        super.start();
        binding.playStatus.setVisibility(GONE);
    }

    public void setControlTimeout(int timeout) {
        controlTimeout = timeout;
    }

    public void showControl() {
        if (useControl) {
            binding.control.setVisibility(VISIBLE);
            binding.seekBar.requestFocus();
            if (controlTimeout > 0) {
                handler.removeMessages(HIDE_CONTROL_WHAT);
                handler.sendEmptyMessageDelayed(HIDE_CONTROL_WHAT, controlTimeout);
            }
        }
    }


    public void hideControl() {
        binding.control.setVisibility(INVISIBLE);
    }

    boolean controlFocusable = true;

    public void setControlFocus(boolean needControl) {
        controlFocusable = needControl;
        if (controlFocusable) {
            setFocusable(true);
            binding.seekBar.setFocusable(true);
        } else {
            setFocusable(false);
            binding.seekBar.setFocusable(false);
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

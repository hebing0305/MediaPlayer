package com.kjsc.ijkplayertest;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.FragmentActivity;

import com.kjsc.ijkplayer.PlayerView;
import com.kjsc.ijkplayer.model.MediaItem;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    String url1 = "http://219.152.49.2:8088/kjsc/2021041616341064975767.mp4";//4k视频
    String url2 = "https://media.w3.org/2010/05/sintel/trailer.mp4";//4k视频
    String url3 = "http://219.152.49.2:8088/kjsc/2021040916235592619742.mp4";
    String url4 = "http://vjs.zencdn.net/v/oceans.mp4";
    String url5 = "http://219.152.49.2:8088/kjsc/2021040916235592619742.mp4";
    String url6 = "http://219.152.49.2:8088/kjsc/2021041616565171623286.mp4";//高帧率视频
    String url7 = "http://192.168.180.243:8080/static/manuvideo_root/066c88d71f50fd7927859e13475fa187ccc6bb4d_manu_4635.0_4861.0.mp4";
    String url8 = "http://192.168.180.243:8080/static/video/hnlv/08e1b18bd265b211181fde1938f5baacdfcd7bb4.mp4";
    PlayerView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.ijkplayer);
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
//        mediaItems.add(new MediaItem(url1));
//        mediaItems.add(new MediaItem(url2));
        mediaItems.add(new MediaItem(url3));
        mediaItems.add(new MediaItem(url4));
        mediaItems.add(new MediaItem(url5));
        mediaItems.add(new MediaItem(url6));
        mediaItems.add(new MediaItem(url7));
        mediaItems.add(new MediaItem(url8));
        mVideoView.setVideoList(mediaItems, 0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.stopAndRelease();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mVideoView.preVideo();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mVideoView.nextVideo();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
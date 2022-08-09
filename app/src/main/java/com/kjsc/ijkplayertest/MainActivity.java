package com.kjsc.ijkplayertest;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.kjsc.ijkplayer.PlayerView;


public class MainActivity extends FragmentActivity {
    //    String url = "http://219.152.49.2:8088/kjsc/2021041616341064975767.mp4";//4k视频
    //        String url = "https://media.w3.org/2010/05/sintel/trailer.mp4";
    //http://219.152.49.2:8088/kjsc/2021040916235592619742.mp4
    //http://vjs.zencdn.net/v/oceans.mp4
//    String url = "http://219.152.49.2:8088/kjsc/2021040916235592619742.mp4";
//    String url = "http://219.152.49.2:8088/kjsc/2021041616565171623286.mp4";//高帧率视频
//    String url = "http://192.168.180.243:8080/static/manuvideo_root/066c88d71f50fd7927859e13475fa187ccc6bb4d_manu_4635.0_4861.0.mp4";
    String url = "http://192.168.180.243:8080/static/video/hnlv/08e1b18bd265b211181fde1938f5baacdfcd7bb4.mp4";
    PlayerView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.ijkplayer);
        mVideoView.setVideoPath(url);
//        mVideoView.setControlTimeout(-1);
//        mVideoView.getConfig().setPlayerCore(MediaPlayerConfig.PLAYER_CORE_ANDROID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.stopAndRelease();
    }
}
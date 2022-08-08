package com.kjsc.ijkplayer;

public class MediaPlayerConfig {
    public static final int PLAYER_CORE_ANDROID = 0;
    public static final int PLAYER_CORE_IJK_PLAYER = 1;
    boolean isMediaCodec = true;
    int playerCore = PLAYER_CORE_ANDROID;
    boolean isLooping = true;

    public boolean isMediaCodec() {
        return isMediaCodec;
    }

    public void setMediaCodec(boolean mediaCodec) {
        isMediaCodec = mediaCodec;
    }

    public int getPlayerCore() {
        return playerCore;
    }

    public void setPlayerCore(int playerCore) {
        this.playerCore = playerCore;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}

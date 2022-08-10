package com.kjsc.ijkplayer.model;

public class MediaItem implements IMediaItem {
    String url;

    public MediaItem(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}

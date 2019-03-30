package com.oolive.live.event;

import com.oolive.live.model.LiveSongModel;


public class ELiveSongDownload {
    public LiveSongModel songModel;


    public ELiveSongDownload() {
        songModel = null;
    }

    public ELiveSongDownload(LiveSongModel songModel) {
        this.songModel = songModel;

    }
}

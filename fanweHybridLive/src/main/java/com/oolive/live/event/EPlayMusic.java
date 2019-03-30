package com.oolive.live.event;

import com.oolive.live.model.LiveSongModel;

public class EPlayMusic {
    public LiveSongModel songModel;

    public EPlayMusic(LiveSongModel model) {
        songModel = model;
    }

    public EPlayMusic() {
        songModel = null;
    }
}

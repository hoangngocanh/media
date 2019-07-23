package com.example.ngocanhpro.media.interf;

public interface IMusicRemote {

    void updateSeekbar(int position);
    void updateMaxSeekbar(int duration);
    void updateTitleSong(String title, String artist);
    void updateTextTime(String timePlay, String timeMax);
    void updateUIbtnPause();
    void updateUIbtnPlay();
    void updateUIimageSong();



}

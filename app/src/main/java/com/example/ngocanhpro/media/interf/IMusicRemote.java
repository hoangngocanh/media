package com.example.ngocanhpro.media.interf;

public interface IMusicRemote {

    void updateSeekbar(int position);
    void updateMaxSeekbar(int duration);
    void updateTextNameSong(String s);
    void updateTextTimePlay(String s);
    void updateUIbtnPause();
    void updateUIbtnPlay();



}

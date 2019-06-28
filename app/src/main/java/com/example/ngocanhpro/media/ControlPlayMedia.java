package com.example.ngocanhpro.media;

import android.widget.SeekBar;
import android.widget.TextView;

public interface ControlPlayMedia {
    void pauseMedia();
    boolean isPlaying();
    int getDur();
    int getCurrentPosition();
    void playMedia();
    void nextSong();
    void prevSong();
    void setSeekbar1(SeekBar seekbar);
    void setSeekbar2(SeekBar seekbar);
    void setNameSong1(TextView tv);
    void setNameSong2(TextView tv);
    void setTextTime(TextView tv1, TextView tv2);

    void openFragmentPlayMusic();
    void playSong(int pos);



}
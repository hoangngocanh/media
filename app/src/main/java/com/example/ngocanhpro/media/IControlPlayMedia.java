package com.example.ngocanhpro.media;

import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;

public interface IControlPlayMedia {
    void pauseMedia();
    boolean isPlaying();
    int getDur();
    void seek(int posn);
    int getCurrentPosition();
    void playMedia();
    void nextSong();
    void prevSong();
    String getNameSong();
    void openFragmentPlayMusic();
    void playSong(int pos);
    void setListSong(ArrayList<Song> array);


}
package com.example.ngocanhpro.media.interf;

import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;

public interface IControlPlayMedia {
    void pauseMedia();
    boolean isMediaInit();
    boolean isPlaying();
    int getDur();
    Song getSong();
    void seek(int posn);
    int getCurrentPosition();
    void playMedia();
    void nextSong();
    void prevSong();
    String getNameSong();
    void playSong(int pos);
    void setListSong(ArrayList<Song> array);
    void openFragmentSongs();
    void setIdAlbum(long id);
    void playSongById(long id);
    void openFragmentSongsPlaylist();
    void setIdPlaylist(long id);

}
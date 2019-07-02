package com.example.ngocanhpro.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;

public class FragmentListSong extends Fragment  {
    private SongAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Song> mListSong;
    IControlPlayMedia iControlPlayMedia;
    TextView tvNameOfSong;
    SeekBar seekBar;
    Button playMusic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false) ;
        playMusic = (Button) v.findViewById(R.id.play_icon);
        seekBar = (SeekBar) v.findViewById(R.id.seek_song);
        tvNameOfSong = (TextView) v.findViewById(R.id.nameSong);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.song_list);
        mAdapter = new SongAdapter(mListSong);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        tvNameOfSong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iControlPlayMedia.openFragmentPlayMusic();
            }

        });
        setSeekBarTouch(seekBar);

        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (iControlPlayMedia.isPlaying()) {
                    iControlPlayMedia.pauseMedia();
                    playMusic.setBackgroundResource(R.drawable.ic_play);
                } else {
                    playMusic.setBackgroundResource(R.drawable.ic_pause);
                    iControlPlayMedia.playMedia();
                }

            }
        });


        mRecyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                iControlPlayMedia.openFragmentPlayMusic();
                iControlPlayMedia.playSong(mListSong.indexOf(item));
            }
        }));




        return v;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ArrayList<Song> setListSong(ArrayList<Song> songs) {
        return mListSong = songs;
    }

    public void setOnHeadlineSelectedListener(IControlPlayMedia callback) {
        this.iControlPlayMedia = callback;
    }

    public void updateSeekbar(int position) {
        if(seekBar != null) {
            seekBar.setProgress(position);
        }
    }

    public void updateMaxSeekbar(int duration) {
        if (seekBar != null) {
            seekBar.setMax(duration);
        }
    }

    public void setSeekBarTouch(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    iControlPlayMedia.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void setTextNameSong(String s){
        if(tvNameOfSong != null) {
            tvNameOfSong.setText(s);
        }
    }
}


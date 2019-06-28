package com.example.ngocanhpro.media;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private RecyclerView recyclerView;
    private ArrayList<Song> mListSong;
    ControlPlayMedia controlEventMusic;
    private int mPosnSong;
    TextView nameOfSong;
    SeekBar mSeekBar;
    Button playMusic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false) ;
        playMusic = (Button) v.findViewById(R.id.play_icon);
        mSeekBar = (SeekBar) v.findViewById(R.id.seek_song) ;
        nameOfSong = (TextView) v.findViewById(R.id.nameSong);
        recyclerView = (RecyclerView) v.findViewById(R.id.song_list);
        mAdapter = new SongAdapter(mListSong);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        nameOfSong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                controlEventMusic.openFragmentPlayMusic();
            }

        });

        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (controlEventMusic.isPlaying()) {
                    controlEventMusic.pauseMedia();
                    playMusic.setBackgroundResource(R.drawable.ic_play);
                } else {
                    playMusic.setBackgroundResource(R.drawable.ic_pause);
                    controlEventMusic.playMedia();
                }

            }
        });


        recyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                controlEventMusic.playSong(mListSong.indexOf(item));
                controlEventMusic.setNameSong1(nameOfSong);
                controlEventMusic.openFragmentPlayMusic();
                controlEventMusic.setSeekbar1(mSeekBar);
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

    public void setOnHeadlineSelectedListener(ControlPlayMedia callback) {
        this.controlEventMusic = callback;
    }
}


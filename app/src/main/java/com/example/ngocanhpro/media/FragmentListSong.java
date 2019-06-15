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
import android.widget.Toast;


import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;

public class FragmentListSong extends Fragment {
    private SongAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Song> mListSong;
    ControlEventMusic controlEventMusic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false) ;

        recyclerView = (RecyclerView) v.findViewById(R.id.song_list);
        mAdapter = new SongAdapter(mListSong);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                controlEventMusic.openFragmentPlayMusic();
                controlEventMusic.playSong(mListSong.indexOf(item));
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


    public void setOnHeadlineSelectedListener(ControlEventMusic callback) {
        this.controlEventMusic = callback;
    }
    public interface ControlEventMusic {
        void openFragmentPlayMusic();
        void playSong(int pos);
    }

}

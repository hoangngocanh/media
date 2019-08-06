package com.example.ngocanhpro.media.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.SongAdapter;
import com.example.ngocanhpro.media.db.DBHandler;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;

import java.util.ArrayList;

public class FragmentSongsDataBase extends Fragment {
    private ArrayList<Song> mSongs;
    private DBHandler mDB;
    private IControlPlayMedia mIControlPlayMedia;
    private RecyclerView mRecyclerView;
    private long mIdPlaylist;


    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.mIControlPlayMedia = control;
        mDB = new DBHandler(getContext());
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.song_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        updateListSong();
        return v;
    }

    public void updateListSong() {
        mSongs = mDB.selectListSongByIdPlaylist(mIdPlaylist);
        mRecyclerView.setAdapter(new SongAdapter(mSongs, new SongAdapter.IOnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                mIControlPlayMedia.setListSong(mSongs);
                mIControlPlayMedia.playSong(mSongs.indexOf(item));
            }
        },getContext()));
    }

    public void setIdPlaylist(long id) {
        mIdPlaylist = id;
    }
}


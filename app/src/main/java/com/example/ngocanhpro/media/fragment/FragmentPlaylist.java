package com.example.ngocanhpro.media.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.PlaylistAdapter;
import com.example.ngocanhpro.media.db.DBHandler;
import com.example.ngocanhpro.media.enity.Playlist;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;

import java.util.ArrayList;

public class FragmentPlaylist extends Fragment {
    IControlPlayMedia iControlPlayMedia;
    private ArrayList<Playlist> mListPlaylits = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DBHandler mDB;

    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.iControlPlayMedia = control;
        mDB = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_playlist, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.playlist_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        updatePlaylist();

        return v;
    }

    public void updatePlaylist() {
        mListPlaylits = mDB.selectAllPlaylist();
        mRecyclerView.setAdapter(new PlaylistAdapter(mListPlaylits, new PlaylistAdapter.IOnItemClickListener() {
            @Override public void onItemClick(Playlist item) {
                long id = item.getId();
                iControlPlayMedia.openFragmentSongsPlaylist();
                iControlPlayMedia.setIdPlaylist(id);
            }
        },getContext()));

    }




}

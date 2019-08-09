package com.example.ngocanhpro.media.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;


import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.SongAdapter;
import com.example.ngocanhpro.media.enity.Song;

import java.util.ArrayList;
import java.util.Random;

public class FragmentSearch extends Fragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Song> mListSong = new ArrayList<>();
    private ArrayList<Song> mResutlFillList = new ArrayList<>();
    IControlPlayMedia iControlPlayMedia;
    EditText edSearch;

    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.iControlPlayMedia = control;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false) ;

        edSearch = (EditText) v.findViewById(R.id.ed_search);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.result_fill_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        final SongAdapter mAdapter = new SongAdapter(mListSong, new SongAdapter.IOnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT+mListSong.indexOf(item)).show();
                iControlPlayMedia.setListSong(mListSong);
                iControlPlayMedia.playSong(mListSong.indexOf(item));
            }
        },getContext());
        mRecyclerView.setAdapter(mAdapter);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                for (final Song mWords : mListSong) {
                    if (mWords.getTitle().toLowerCase().startsWith(s.toString())) {
                        mResutlFillList.clear();
                        mResutlFillList.add(mWords);
                        final SongAdapter mAdapter = new SongAdapter(mResutlFillList, new SongAdapter.IOnItemClickListener() {
                            @Override public void onItemClick(Song item) {
                                iControlPlayMedia.setListSong(mResutlFillList);
                                iControlPlayMedia.playSong(mResutlFillList.indexOf(item));
                            }
                        },getContext());
                        mRecyclerView.setAdapter(mAdapter);

                    }
                }
                Log.v(">>>>>>>>>>>> "+mListSong.size(),""+s.toString());

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;

    }

    public void setListSong(ArrayList<Song> array) {
        mListSong = array;
    }






}
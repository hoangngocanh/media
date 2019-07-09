package com.example.ngocanhpro.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.ngocanhpro.media.enity.Kind;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentListSong extends Fragment  {
    private RecyclerView mRecyclerView;
    private ArrayList<Song> mListSong;
    IControlPlayMedia iControlPlayMedia;
    TextView tvNameOfSong;
    SeekBar seekBar;
    Button btnPlayMusic, btnShuffle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false) ;
        btnPlayMusic = (Button) v.findViewById(R.id.play_icon);
        btnShuffle = (Button) v.findViewById(R.id.btn_shuffle);
        seekBar = (SeekBar) v.findViewById(R.id.seek_song);
        tvNameOfSong = (TextView) v.findViewById(R.id.nameSong);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.song_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                iControlPlayMedia.openFragmentPlayMusic();
                iControlPlayMedia.playSong(mListSong.indexOf(item));
            }
        },getContext()));



        tvNameOfSong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iControlPlayMedia.openFragmentPlayMusic();
            }

        });
        setSeekBarTouch(seekBar);

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (iControlPlayMedia.isPlaying()) {
                    iControlPlayMedia.pauseMedia();
                    btnPlayMusic.setBackgroundResource(R.drawable.ic_play);
                } else {
                    btnPlayMusic.setBackgroundResource(R.drawable.ic_pause);
                    iControlPlayMedia.playMedia();
                }
            }
        });

        // Đảo danh sách phát
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                shuffleSongs(mListSong);
            }
        });




        return v;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setListSong(ArrayList<Song> songs) {
         mListSong = songs;
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

    //trộn danh sách phát ngẫu nhiên
    public void shuffleSongs(ArrayList<Song> array) {
        Collections.shuffle(array);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle()+mListSong.indexOf(item),Toast.LENGTH_SHORT).show();
                iControlPlayMedia.openFragmentPlayMusic();
                iControlPlayMedia.playSong(mListSong.indexOf(item));
            }
        },getContext()));
    }

    // tạo danh sách phát theo nghệ sĩ
    public ArrayList<Kind> makeListArist(ArrayList<Song> array) {
        ArrayList<Kind> arrayArists = new ArrayList<>();
        arrayArists.add(new Kind(array.get(0).getArtist(),1));
        int check = 1;
        for (int i = 1; i < array.size(); i++) {
            check =1;
            for (int j = 0; j < arrayArists.size(); j++) {
                if (array.get(i).getArtist().equals(arrayArists.get(j).getName())) {
                    check = 0;
                    arrayArists.get(j).setNum(arrayArists.get(j).getNum()+1);
                }
            }
            if (check == 1) {
                arrayArists.add(new Kind(array.get(i).getArtist(), 1));
            }

        }

        for (int k = 0; k < arrayArists.size(); k++) {
            Log.v("Arists "+arrayArists.get(k).getName(),""+arrayArists.get(k).getNum());
        }
        return arrayArists;
    }

    public void sortByArist(){
        ArrayList<Kind> arrayArists = new ArrayList<Kind>();
        arrayArists = makeListArist(mListSong);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(new KindAdapter(arrayArists, new KindAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Kind item) {

                final ArrayList<Song> list = getListByKind(item.getName());
                iControlPlayMedia.setListSong(list);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                mRecyclerView.setAdapter(new SongAdapter(list, new SongAdapter.OnItemClickListener() {
                    @Override public void onItemClick(Song item) {
                        Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        iControlPlayMedia.openFragmentPlayMusic();
                        iControlPlayMedia.playSong(list.indexOf(item));
                    }
                },getContext()));
            }

        }));
    }

    public ArrayList<Song> getListByKind(String kind) {
        ArrayList<Song> array = new ArrayList<Song>();
        for (int i = 0; i < mListSong.size(); i++) {
            if(mListSong.get(i).getArtist().equals(kind)) {
                array.add(mListSong.get(i));
            }
        }
        return array;
    }

    public void sortBySong(){
        iControlPlayMedia.setListSong(mListSong);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT).show();
                iControlPlayMedia.openFragmentPlayMusic();
                iControlPlayMedia.playSong(mListSong.indexOf(item));
            }
        },getContext()));
    }

}
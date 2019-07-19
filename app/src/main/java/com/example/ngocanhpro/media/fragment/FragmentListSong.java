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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.SongAdapter;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.enity.Kind1;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentListSong extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private RecyclerView mRecyclerView;
    private ArrayList<Song> mListSong = new ArrayList<>();
    IControlPlayMedia iControlPlayMedia;
    TextView tvNameOfSong;
    SeekBar seekBar;
    Button btnPlayMusic, btnShuffle;
    CardView cardMusic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_song, container, false) ;
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        btnPlayMusic = (Button) v.findViewById(R.id.play_icon);
        btnShuffle = (Button) v.findViewById(R.id.btn_shuffle);
        seekBar = (SeekBar) v.findViewById(R.id.seek_song);
        tvNameOfSong = (TextView) v.findViewById(R.id.nameSong);

        cardMusic = (CardView) v.findViewById(R.id.card_music);

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
                setVisibleCardMusic();
            }

        });
        setSeekBarTouch(seekBar);

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            if (iControlPlayMedia.isPlaying()) {
                iControlPlayMedia.pauseMedia();
                btnPlayMusic.setBackgroundResource(R.drawable.ic_play1);
            } else {
                btnPlayMusic.setBackgroundResource(R.drawable.ic_pause1);
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


        iControlPlayMedia.setListSong(mListSong);

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };


        return new CursorLoader(
                getContext(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor musicCursor) {
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            ArrayList<Song> arraySong = new ArrayList<>();
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int idAlbum = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);

            //Xoá dữ liệu tránh add thêm bài hát đã trungf lặp
            mListSong.clear();
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long albumId = musicCursor.getLong(idAlbum);
                mListSong.add(new Song(thisId, thisTitle, thisArtist, albumId));
            }
            while (musicCursor.moveToNext());
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void setBtnPlay() {
        if (btnPlayMusic != null)
        btnPlayMusic.setBackgroundResource(R.drawable.ic_play1);
    }
    public void setBtnPause() {
        if (btnPlayMusic != null)
        btnPlayMusic.setBackgroundResource(R.drawable.ic_pause1);

    }
    public void setVisibleCardMusic() {
        cardMusic.setVisibility(View.VISIBLE);
    }
    public void setGoneCardMusic() {
        cardMusic.setVisibility(View.GONE);
    }
}
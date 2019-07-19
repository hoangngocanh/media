package com.example.ngocanhpro.media.fragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.SongAdapter;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.enity.Kind1;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentSongs extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private RecyclerView mRecyclerView;
    private ArrayList<Song> mListSong = new ArrayList<>();
    IControlPlayMedia iControlPlayMedia;
    TextView tvNameOfSong , tvNameAlbum, tvInforAlbum;
    SeekBar seekBar;
    Button btnPlayMusic;
    ImageView imgAlbum;
    long keywordAlbum;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_songs, container, false) ;
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        btnPlayMusic = (Button) v.findViewById(R.id.play_icon);
        seekBar = (SeekBar) v.findViewById(R.id.seek_song);
        tvNameOfSong = (TextView) v.findViewById(R.id.nameSong);
        tvNameAlbum = (TextView) v.findViewById(R.id.album_name);
        tvInforAlbum = (TextView) v.findViewById(R.id.album_infor);
        imgAlbum = (ImageView) v.findViewById(R.id.img_album);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.songs);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setAdapter(new SongAdapter(mListSong, new SongAdapter.OnItemClickListener() {
            @Override public void onItemClick(Song item) {
                Toast.makeText(getActivity(),"playing " + item.getTitle(),Toast.LENGTH_SHORT+mListSong.indexOf(item)).show();
                iControlPlayMedia.setListSong(mListSong);
                Log.v("::"+mListSong.size(),">>>"+mListSong.indexOf(item));
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
                    btnPlayMusic.setBackgroundResource(R.drawable.ic_play1);
                } else {
                    btnPlayMusic.setBackgroundResource(R.drawable.ic_pause1);
                    iControlPlayMedia.playMedia();
                }
            }
        });

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
        final Uri ART_CONTENT_URI = android.net.Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, keywordAlbum);
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.loadImage(String.valueOf(albumArtUri),new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                imgAlbum.setImageBitmap(loadedImage);
            }
        });


        return v;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
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
            int nameAlbumColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);

            //Xoá dữ liệu tránh add thêm bài hát đã trungf lặp
            mListSong.clear();
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String nameAlbum = musicCursor.getString(nameAlbumColumn);
                long albumId = musicCursor.getLong(idAlbum);
                if (albumId == keywordAlbum) {
                    mListSong.add(new Song(thisId, thisTitle, thisArtist, albumId));
                    tvNameAlbum.setText(nameAlbum);
                    tvInforAlbum.setText(thisArtist);
                }
            }
            while (musicCursor.moveToNext());
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void setKeyWord (long id) {
        keywordAlbum = id;
    }

    public void setBtnPlay() {
        if (btnPlayMusic != null)
            btnPlayMusic.setBackgroundResource(R.drawable.ic_play1);
    }
    public void setBtnPause() {
        if (btnPlayMusic != null)
            btnPlayMusic.setBackgroundResource(R.drawable.ic_pause1);

    }
}
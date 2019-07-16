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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.adapter.AlbumAdapter;
import com.example.ngocanhpro.media.adapter.ArtistAdapter;
import com.example.ngocanhpro.media.enity.Album;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;

import java.util.ArrayList;

public class FragmentListAlbum extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    IControlPlayMedia iControlPlayMedia;
    private ArrayList<Album> mListAlbum = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.iControlPlayMedia = control;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_album, container, false);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.album_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));



        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,


        };

        return new CursorLoader(
                getContext(),
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor musicCursor) {
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            //get columns
            int idCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int artistCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.AlbumColumns.ARTIST);
            int numSongkCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS);


            //add songs to list
            do {
                long id = musicCursor.getLong(idCol);
                String nameAlbum = musicCursor.getString(albumCol);
                String nameArtist = musicCursor.getString(artistCol);
                int numTrack = musicCursor.getInt(numSongkCol);
                mListAlbum.add(new Album(id, nameAlbum, nameArtist, numTrack));

            }
            while (musicCursor.moveToNext());
        }

        mRecyclerView.setAdapter(new AlbumAdapter(mListAlbum, new AlbumAdapter.OnItemClickListener() {
            @Override public void onItemClick(Album item) {

            }
        },getContext()));



    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}

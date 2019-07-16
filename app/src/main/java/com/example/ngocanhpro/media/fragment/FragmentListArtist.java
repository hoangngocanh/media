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
import com.example.ngocanhpro.media.adapter.ArtistAdapter;
import com.example.ngocanhpro.media.enity.Album;
import com.example.ngocanhpro.media.enity.Artist;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;

import java.util.ArrayList;

public class FragmentListArtist extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    IControlPlayMedia iControlPlayMedia;
    private ArrayList<Artist> mListArtist = new ArrayList<>();
    private ArrayList<Song> mListSong = new ArrayList<>();
    private RecyclerView mRecyclerView;
    ArrayList<Album> mListAlbum = new ArrayList<>();
    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.iControlPlayMedia = control;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_artist, container, false);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        loaderManager.initLoader(1, null, this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.artist_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));



        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        if(i == 0) {
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
        else {
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] projection = {
                    MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,

            };

            return new CursorLoader(
                    getContext(),
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor musicCursor) {

        int idLoader = loader.getId();
        if(musicCursor!=null && musicCursor.moveToFirst() && idLoader == 0) {
            //get columns
            int idCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int albumCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int artistCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.AlbumColumns.ARTIST);
            int numSongkCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS);

            //add albums to list
            do {
                long id = musicCursor.getLong(idCol);
                String nameAlbum = musicCursor.getString(albumCol);
                String nameArtist = musicCursor.getString(artistCol);
                int numTrack = musicCursor.getInt(numSongkCol);
                mListAlbum.add(new Album(id, nameAlbum, nameArtist, numTrack));
            }
            while (musicCursor.moveToNext());
        }

        Log.v(""+mListAlbum.size(),">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if(musicCursor!=null && musicCursor.moveToFirst() && idLoader == 1) {
            //get columns
            int idCol = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int nameCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.ArtistColumns.ARTIST);
            int numTrackCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS);
            int numAlbumCol = musicCursor.getColumnIndex
                    (MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS);

            //add songs to list
            do {
                long id = musicCursor.getLong(idCol);
                String name = musicCursor.getString(nameCol);
                Log.v("name="+name," "+2);
                int numTrack = musicCursor.getInt(numTrackCol);
                int numAlbum = musicCursor.getInt(numAlbumCol);
                ArrayList<Album> albums = new ArrayList<>();
                for (int i = 0; i < mListAlbum.size(); i++) {
                    if(name.equals(mListAlbum.get(i).getNameArtist())){
                        albums.add(new Album(mListAlbum.get(i).getId(), mListAlbum.get(i).getNameAlbum(), mListAlbum.get(i).getNameArtist(), mListAlbum.get(i).getNumSong()));
                    }
                }

                Log.v(""+albums.size(),">>>>>>>>>");
                mListArtist.add(new Artist(id, name, numTrack, numAlbum, albums));

            }
            while (musicCursor.moveToNext());

//            mRecyclerView.setAdapter(new ArtistAdapter(mListArtist,getContext()));
            mRecyclerView.setAdapter(new ArtistAdapter(mListArtist,getContext()));

        }




    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void setListSong(ArrayList<Song> Songs) {
        mListSong = Songs;
    }

    public ArrayList<Album> fillSongByArtist (String artist){
        ArrayList<Album> array = new ArrayList<>();

        return array;
    }

}

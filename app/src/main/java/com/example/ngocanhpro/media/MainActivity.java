package com.example.ngocanhpro.media;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.services.MusicService;

import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IControlPlayMedia,  LoaderManager.LoaderCallbacks<Cursor>, IMusicRemote {
    private ArrayList<Song> mListSong = new ArrayList<>();
    private ArrayList<Song> mList = new ArrayList<>();
    private MusicService mMusicSrv;
    private Intent mPlayIntent;
    public FragmentPlaySong fragmentPlaySong = new FragmentPlaySong();
    public FragmentListSong fragmentListSong = new FragmentListSong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mList = mListSong;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Hiển thị fragment

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // chèn fragment danh sách bài hát lên main activity
        ft.replace(R.id.container, fragmentListSong);
        ft.commit();
//        getSongList();

        getSupportLoaderManager().initLoader(0, null, this);
        fragmentListSong.setListSong(mListSong);


    }

    //Kết nối service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            mMusicSrv = binder.getService();
            //pass list
            mMusicSrv.setList(mList);
            setImusic();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if(mPlayIntent ==null){
            mPlayIntent = new Intent(this, MusicService.class);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("activity", "pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("activity", "stop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_artists) {
            fragmentListSong.sortByArist();
        } else if (id == R.id.nav_albums) {

        } else if (id == R.id.nav_songs) {
            fragmentListSong.sortBySong();
        } else if (id == R.id.nav_playlists) {

        } else if (id == R.id.nav_snapdragon) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FragmentListSong) {
            FragmentListSong fragmentListSong = (FragmentListSong) fragment;
            fragmentListSong.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof  FragmentPlaySong) {
            fragmentPlaySong.setOnHeadlineSelectedListener(this);
        }
    }

    @Override
    //lấy tên bài hát đang phát
    public String getNameSong() {
        return mMusicSrv.getSongTitle();
    }

    @Override
    //mở fragment playmusic (hiển thị cửa sổ  chơi nhạc)
    public void openFragmentPlayMusic(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragmentPlaySong.isAdded()) {
                ft.show(fragmentPlaySong);
                Log.d("fragment",": Added");

        } else {
            ft.add(R.id.container, fragmentPlaySong, "findThisFragment")
                    .addToBackStack(null);
        }
        ft.commit();
    }
    @Override
    //phát bài hát được chỉ định vị trí pos
    public void playSong(int pos){
        mMusicSrv.setSong(pos);
        mMusicSrv.playSong();
    }

    @Override
    public void setListSong(ArrayList<Song> array) {
        mMusicSrv.setList(array);
    }


                @Override
    //Dừng bài hát
    public void pauseMedia(){
            mMusicSrv.pausePlayer();
    }
    @Override
    //Phát tiếp bài hát
    public void playMedia() {
        mMusicSrv.go();
    }
    @Override
    //Phát bài hát tiếp theo
    public void nextSong(){
        mMusicSrv.playNext();
    }
    @Override
    //chuyển bài hát trước
    public void prevSong(){
        mMusicSrv.playPrev();
    }

    @Override
    //Kiểm tra player đang phát hay dừng
    public boolean isPlaying(){
        return mMusicSrv.isPng();
    }

    @Override
    //Trả về tông thời gian phát bài hát
    public  int getDur(){
        return mMusicSrv.getDur();
    }

    @Override
    //Tua đến vị trí posn
    public void seek(int posn) {
        mMusicSrv.seek(posn);
    }

    @Override
    //Trả về thời gian đã chạy của bài hát
    public int getCurrentPosition(){
        return mMusicSrv.getPosn();
    }


    @NonNull
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
                this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor musicCursor) {
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int idAlbum = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);


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

    @Override
    public void updateSeekbar(int position) {
        fragmentPlaySong.updateSeekbar(position);
        fragmentListSong.updateSeekbar(position);
    }

    @Override
    public void updateMaxSeekbar(int duration) {
        fragmentPlaySong.updateMaxSeekbar(duration);
        fragmentListSong.updateMaxSeekbar(duration);
    }

    @Override
    public void updateTextNameSong(String s) {
        fragmentPlaySong.setTextNameSong(s);
        fragmentListSong.setTextNameSong(s);
    }

    @Override
    public void updateTextTimePlay(String s) {
        fragmentPlaySong.setTextTimePosn(s);
    }

    public void setImusic(){
        mMusicSrv.setmIMusicRemote(this);
    }

}

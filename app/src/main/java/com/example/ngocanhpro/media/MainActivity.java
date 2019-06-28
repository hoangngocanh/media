package com.example.ngocanhpro.media;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.ContentResolver;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.services.MusicService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ControlPlayMedia,  LoaderManager.LoaderCallbacks<Cursor> {
    public ArrayList<Song> mListSong = new ArrayList<>();
    public MusicService musicSrv;
    private Intent playIntent;
    FragmentPlaySong fragmentPlaySong = new FragmentPlaySong();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Hiển thị fragment
        FragmentListSong fragmentListSong = new FragmentListSong();
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
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(mListSong);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mListSong.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FragmentListSong) {
            FragmentListSong fragmentListSong = (FragmentListSong) fragment;
            fragmentListSong.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof  FragmentPlaySong) {
            FragmentPlaySong fragmentPlaySong = (FragmentPlaySong) fragment;
            fragmentPlaySong.setOnHeadlineSelectedListener(this);
        }
    }

    @Override
    public void openFragmentPlayMusic(){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragmentPlaySong.isHidden()) {
                ft.show(fragmentPlaySong);

        } else {
            ft.add(R.id.container, fragmentPlaySong, "findThisFragment")
                    .addToBackStack(null);
        }

//        ft.add(R.id.container, fragmentPlaySong, "findThisFragment")
//                .addToBackStack(null);

//            if(fm.findFragmentById(R.id.fragment2).isHidden()) {
//                ft.show(fragmentPlaySong);
//                Log.v("show", "fragmentplaysong");
//
//        } else {
//            ft .add(R.id.container, fragmentPlaySong, "findThisFragment")
//                .addToBackStack(null);
//        }
        ft.commit();
    }
    @Override
    public void playSong(int pos){
        musicSrv.setSong(pos);
        musicSrv.playSong();
    }

    @Override
    public  void setSeekbar1(SeekBar seekbar) {
        musicSrv.setUIControls1(seekbar);
    }

    @Override
    public  void setSeekbar2(SeekBar seekbar) {
        musicSrv.setUIControls2(seekbar);
    }

    @Override
    public void setNameSong1(TextView tv) {
        musicSrv.setTextViewTitleSong1(tv);
    }
    @Override
    public void setNameSong2(TextView tv) {
        musicSrv.setTextViewTitleSong2(tv);
    }

    @Override
    public void setTextTime(TextView tv1, TextView tv2) {
        musicSrv.setTextTime(tv1, tv2);
    }

    @Override
    public void pauseMedia(){
            musicSrv.pausePlayer();
    }
    @Override
    public void playMedia() {
        musicSrv.go();
    }
    @Override
    public void nextSong(){
        musicSrv.playNext();
    }
    @Override
    public void prevSong(){
        musicSrv.playPrev();
    }

    @Override
    public boolean isPlaying(){
        return musicSrv.isPng();
    }

    @Override
    public  int getDur(){
        return musicSrv.getDur();
    }
    @Override
    public int getCurrentPosition(){
        return musicSrv.getPosn();
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
                MediaStore.Audio.Media.DURATION
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
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mListSong.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }



    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}

package com.example.ngocanhpro.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.fragment.FragmentListAlbum;
import com.example.ngocanhpro.media.fragment.FragmentListArtist;
import com.example.ngocanhpro.media.fragment.FragmentListSong;
import com.example.ngocanhpro.media.fragment.FragmentPlaySong;
import com.example.ngocanhpro.media.fragment.FragmentSongs;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.interf.IMusicRemote;
import com.example.ngocanhpro.media.services.MusicService;

import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
            IControlPlayMedia, IMusicRemote {
    private ArrayList<Song> mList = new ArrayList<>();
    private MusicService mMusicSrv;
    private Intent mPlayIntent;
    public FragmentPlaySong fragmentPlaySong = new FragmentPlaySong();
    public FragmentListSong fragmentListSong = new FragmentListSong();
    public FragmentListArtist fragmentListArtist = new FragmentListArtist();
    public FragmentListAlbum fragmentListAlbum = new FragmentListAlbum();
    public FragmentSongs fragmentSongs = new FragmentSongs();
    private boolean mMusicBound=false;

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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // chèn fragment danh sách bài hát lên main activity
        ft.replace(R.id.container, fragmentListSong);
        ft.commit();



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
            mMusicBound = true;
            setImusic();


        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
            fragmentListSong.setGoneCardMusic();
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
//            fragmentListSong.sortByArist();
            openFragmentArtist();
        } else if (id == R.id.nav_albums) {
            opentFragmetAlbum();
        } else if (id == R.id.nav_songs) {
            opentFragmentListSong();
        } else if (id == R.id.nav_playlists) {
            opentFragmentPlayList();
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
        } else if (fragment instanceof  FragmentListArtist) {
            fragmentListArtist.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentSongs) {
            fragmentSongs.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentListAlbum) {
            fragmentListAlbum.setOnHeadlineSelectedListener(this);
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
    public void openFragmentSongs() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentSongs.isAdded()) {
            ft.show(fragmentSongs);

        } else {
            ft.replace(R.id.container, fragmentSongs, "fragmentSong").addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    //phát bài hát được chỉ định vị trí pos
    public void playSong(int pos){
        mMusicSrv.setSong(pos);
        mMusicSrv.playSong();
        fragmentListSong.setVisibleCardMusic();
    }

    @Override
    public void playSongById(long id) {
        mMusicSrv.playSong(id);
    }

    @Override
    public void setListSong(ArrayList<Song> array) {
        if(mMusicSrv != null)
            mMusicSrv.setList(array);
    }

    @Override
    //Dừng bài hát
    public void pauseMedia(){
            mMusicSrv.pausePlayer();
    }

    @Override
    public boolean isMediaInit() {
        return mMusicBound;
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
    public Song getSong() {
        return mMusicSrv.getSong();
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

    @Override
    public void updateUIbtnPause() {
        fragmentListSong.setBtnPause();
        fragmentPlaySong.setBtnPause();
    }

    @Override
    public void updateUIbtnPlay() {
        fragmentListSong.setBtnPlay();
        fragmentPlaySong.setBtnPlay();
    }

    @Override
    public void setId(long id) {
        fragmentSongs.setKeyWord(id);
    }

        public void setImusic(){
        mMusicSrv.setmIMusicRemote(this);
    }

    public void openFragmentArtist() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentListArtist.isAdded()) {
            ft.show(fragmentListArtist);

        } else {
            ft.replace(R.id.container, fragmentListArtist, "fragmentArtist").addToBackStack(null);
        }
        ft.commit();
    }


    public void opentFragmetAlbum() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentListAlbum.isAdded()) {
            ft.show(fragmentListAlbum);

        } else {
            ft.replace(R.id.container, fragmentListAlbum, "fragmentAlbum").addToBackStack(null);
        }
        ft.commit();
    }

    public void opentFragmentPlayList() {
        AppCompatActivity activity = (AppCompatActivity) this;

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentSongs).addToBackStack(null).commit();
        fragmentSongs.setKeyWord((4));

    }

    private void opentFragmentListSong() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentListSong.isAdded()) {
            ft.show(fragmentListSong);

        } else {
            ft.replace(R.id.container, fragmentListSong, "fragmentSong").addToBackStack(null);
        }
        ft.commit();
    }


}

package com.example.ngocanhpro.media;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.db.DBHandler;
import com.example.ngocanhpro.media.enity.Song;
import com.example.ngocanhpro.media.fragment.FragmentListAlbum;
import com.example.ngocanhpro.media.fragment.FragmentListArtist;
import com.example.ngocanhpro.media.fragment.FragmentListSong;
import com.example.ngocanhpro.media.fragment.FragmentPlaySong;
import com.example.ngocanhpro.media.fragment.FragmentPlaylist;
import com.example.ngocanhpro.media.fragment.FragmentSongs;
import com.example.ngocanhpro.media.fragment.FragmentSongsDataBase;
import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.interf.IMusicRemote;
import com.example.ngocanhpro.media.interf.IUpdateLists;
import com.example.ngocanhpro.media.services.MusicService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.FileNotFoundException;
import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
            IControlPlayMedia, IMusicRemote, IUpdateLists {
    private ArrayList<Song> mList = new ArrayList<>();
    private MusicService mMusicSrv;
    private Intent mPlayIntent;

    public FragmentListSong fragmentListSong = new FragmentListSong();
    public FragmentListArtist fragmentListArtist = new FragmentListArtist();
    public FragmentListAlbum fragmentListAlbum = new FragmentListAlbum();
    public FragmentSongs fragmentSongs = new FragmentSongs();
    public FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
    public FragmentSongsDataBase fragmentSongsDataBase = new FragmentSongsDataBase();
    private boolean mMusicBound=false;
    ImageButton btnPlay, btnPlayMain, btnPrev, btnNext;
    TextView tvNameSong, tvNameArtist, tvTimePlay, tvTimeMax;
    SeekBar seekBar;
    ImageView imgSmallCover, imgCoverSong;
    RelativeLayout panelUp;
    private  ImageLoader mImageLoader;
    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        imgSmallCover = (ImageView) findViewById(R.id.img_song_small_cover);
        imgCoverSong = (ImageView) findViewById(R.id.img_song_cover);
        panelUp = (RelativeLayout) findViewById(R.id.panel_up);
        panelUp.setVisibility(View.GONE);

        btnPlay = (ImageButton) findViewById(R.id.play_button);
        btnPlayMain = (ImageButton) findViewById(R.id.play_button_main);
        btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        tvNameArtist = (TextView) findViewById(R.id.songs_artist_name);
        tvNameSong = (TextView) findViewById(R.id.songs_title) ;
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        tvTimeMax = (TextView) findViewById(R.id.end_time);
        tvTimePlay = (TextView) findViewById(R.id.start_time);

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

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mMusicSrv.isPng()) {
                    mMusicSrv.pausePlayer();
                    btnPlay.setImageResource(R.drawable.round_play_arrow_black_48dp);
                } else {
                    btnPlay.setImageResource(R.drawable.round_pause_black_48dp);
                    mMusicSrv.go();
                }
            }
        });

        btnPlayMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mMusicSrv.isPng()) {
                    mMusicSrv.pausePlayer();
                    btnPlayMain.setImageResource(R.drawable.play_button);
                } else {
                    btnPlayMain.setImageResource(R.drawable.pause_button);
                    mMusicSrv.go();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mMusicSrv.playNext();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mMusicSrv.playPrev();
            }
        });

        setSeekBarTouch(seekBar);




    }

    public void setSeekBarTouch(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    mMusicSrv.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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
        }if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
        } else if (fragment instanceof  FragmentListArtist) {
            fragmentListArtist.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentSongs) {
            fragmentSongs.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentListAlbum) {
            fragmentListAlbum.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentPlaylist) {
            fragmentPlaylist.setOnHeadlineSelectedListener(this);
        } else if (fragment instanceof FragmentSongsDataBase) {
            fragmentSongsDataBase.setOnHeadlineSelectedListener(this);
        }
    }

    @Override
    //lấy tên bài hát đang phát
    public String getNameSong() {
        return mMusicSrv.getSongTitle();
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
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        panelUp.setVisibility(View.VISIBLE);
        upLoadImageSong();

    }

    //Set hình ảnh cho ảnh bìa bài hát và ảnh tiêu đề
    public void upLoadImageSong(){
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, mMusicSrv.getSong().getAlbumID());

        mImageLoader = ImageLoader.getInstance();
        try {
            mImageLoader.loadImage(String.valueOf(albumArtUri), new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    imgCoverSong.setImageBitmap(loadedImage);
                    imgSmallCover.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                }
            });
        } catch (Exception e){
            imgCoverSong.setImageResource(R.drawable.img_music);
            imgSmallCover.setImageResource(R.drawable.img_music);
        }
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
        seekBar.setProgress(position);
    }

    @Override
    public void updateMaxSeekbar(int duration) {

        seekBar.setMax(duration);
    }

    @Override
    public void updateTitleSong(String t, String a) {
        tvNameSong.setText(t);
        tvNameArtist.setText(a);
    }

    @Override
    public void updateTextTime(String timePlay, String timeMax) {

        tvTimePlay.setText(timePlay);
        tvTimeMax.setText(timeMax);
    }

    @Override
    public void updateUIbtnPause() {
        btnPlayMain.setImageResource(R.drawable.pause_button);
        btnPlay.setImageResource(R.drawable.round_pause_black_48dp);

    }

    @Override
    public void updateUIbtnPlay() {
        btnPlayMain.setImageResource(R.drawable.play_button);
        btnPlay.setImageResource(R.drawable.round_play_arrow_black_48dp);
    }

    @Override
    public void updateUIimageSong() {
        upLoadImageSong();
    }

        @Override
    public void setIdAlbum(long id) {
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentPlaylist.isAdded()) {
            ft.show(fragmentPlaylist);

        } else {
            ft.replace(R.id.container, fragmentPlaylist, "fragmentPlaylist").addToBackStack(null);
        }
        ft.commit();
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

    @Override
    public void openFragmentSongsPlaylist() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentSongsDataBase.isAdded()) {
            ft.show(fragmentSongsDataBase);

        } else {
            ft.replace(R.id.container, fragmentSongsDataBase, "fragmentSong").addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void updatePlaylist() {
        fragmentPlaylist.updatePlaylist();
    }

    @Override
    public void setIdPlaylist(long id) {
        fragmentSongsDataBase.setIdPlaylist(id);
    }

    }

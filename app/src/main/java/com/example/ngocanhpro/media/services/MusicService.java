package com.example.ngocanhpro.media.services;

import android.app.Service;
import android.os.Handler;
import android.os.IBinder;
import android.content.Intent;

import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import android.app.Notification;
import android.app.PendingIntent;

import com.example.ngocanhpro.media.interf.IMusicRemote;
import com.example.ngocanhpro.media.MainActivity;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Song;


public class MusicService extends  Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private int mInterval = 500; // khoảng thời gian cập nhật seek bar mili giây
    private MediaPlayer mPlayer; //media mPlayer
    private ArrayList<Song> mSongs; // Danh sách bài hát
    private int mSongPosn = 0; // vị trí bài hát được phát
    private final IBinder mMusicBind = new MusicBinder();
    private String songTitle=""; // Tên bài hát phát
    private static final int NOTIFY_ID=1;
    private IMusicRemote mIMusicRemote;
    private Handler myHandler = new Handler();


    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        mSongPosn =0;
        //create mPlayer
        mPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    //Khởi tạo music layer
    public void initMusicPlayer(){
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    //Lấy vị trí seekbar
    public int getPosn(){
        return mPlayer.getCurrentPosition();
    }

    //Lấy tổng thời gian phát
    public int getDur(){
        return mPlayer.getDuration();
    }

    //Kiểm tra xem bài hát đang phát hay ngừng
    public boolean isPng(){
        return mPlayer.isPlaying();
    }

    //Dừng bài hát đang phát
    public void pausePlayer(){
        mPlayer.pause();
    }

    //Tua bài hát bằng seek bar
    public void seek(int posn){
        mPlayer.seekTo(posn);
    }

    //Phát bài hát
    public void go(){
        mPlayer.start();
    }

    //Chuyển bái hát vừa phát hoặc bài hát xếp trước
    public void playPrev(){

        if(mSongPosn == 0) {
            mSongPosn = mSongs.size()-1;
        } else {
            mSongPosn--;
        }
        playSong();
    }

    //Chuyển bài hát tiếp theo
    public void playNext(){
        if(mSongPosn == mSongs.size() -1) {
            mSongPosn = 0;
        } else {
            mSongPosn++;
        }
        playSong();
    }

    public void playSong(){
        mPlayer.reset();
        //get song
        Song song = mSongs.get(mSongPosn);
        songTitle=song.getTitle();
        long currSong = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();
        Log.d("Phát bài hát"," playSong()");

        mIMusicRemote.updateTextNameSong(songTitle);

    }



    //Đặt danh sách bài hát từ data cho Service
    public void setList(ArrayList<Song> theSongs){
        mSongs =theSongs;
    }

    //Đặt bài hát phát
    public void setSong(int songIndex){
        mSongPosn =songIndex;
    }

    //Lấy bài hát đang phát
    public Song getSong(){
        return mSongs.get(mSongPosn);
    }

    //Lấy tên bài hát đang phát
    public String getSongTitle(){
        return songTitle;
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    //Phát tiếp bài hát sau khi bài hát vừa phát kết thúc
    public void onCompletion(MediaPlayer mp) {
        Log.v("tag","bài hát tiếp theo ");
        mp.reset();
        playNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        Log.v("tag","Chuẩn bị phát");
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);

        //Cập nhật thời gian phát seekbar
        mIMusicRemote.updateMaxSeekbar(getDur());
        myHandler.postDelayed(UpdateSongTime,100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(mPlayer.isPlaying()) {
                mIMusicRemote.updateSeekbar(getPosn());
                mIMusicRemote.updateTextTimePlay(convertToTimeFommat(getPosn()));
            }
            myHandler.postDelayed(this, mInterval);
        }
    };

    //Chuyển định dạng mili giây về phút:giây
    public String convertToTimeFommat(int t) {
        int phut = t/60000;
        int giay = (t-phut*60000)/1000;
        return phut+":"+giay;
    }

    public void setmIMusicRemote(IMusicRemote mIMusicRemote) {
        this.mIMusicRemote = mIMusicRemote;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

}


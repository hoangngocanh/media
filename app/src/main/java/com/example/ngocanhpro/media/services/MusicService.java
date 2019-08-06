package com.example.ngocanhpro.media.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.graphics.Color;
import android.os.Build;
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
import android.support.v4.app.NotificationCompat;
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
    private ArrayList<Song> mSongs = new ArrayList<>(); // Danh sách bài hát
    private int mSongPosn = 0; // vị trí bài hát được phát
    private final IBinder mMusicBind = new MusicBinder();
    private String songTitle=""; // Tên bài hát phát
    private static final int NOTIFY_ID=1;
    private IMusicRemote mIMusicRemote;
    private Handler myHandler = new Handler();

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

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

        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    // Dùng với android 8 trở lên
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_play)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
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
        //Sửa lại nút hiển thị giao diện play khi dưng bài hát
        mIMusicRemote.updateUIbtnPlay();
    }

    //Tua bài hát bằng seek bar
    public void seek(int posn){
        mPlayer.seekTo(posn);
    }

    //Phát bài hát
    public void go(){
        mPlayer.start();
        //Sửa lại nút hiển thị giao diện pause khi đang chơi bài hát
        mIMusicRemote.updateUIbtnPause();
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

        mIMusicRemote.updateTitleSong(song.getTitle(), song.getArtist());

        //Sửa lại nút hiển thị giao diện pause khi phát bài hát
        mIMusicRemote.updateUIbtnPause();
        mIMusicRemote.updateUIimageSong();

    }

    public void playSong(long id){
        mPlayer.reset();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try{
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();
        Log.d("Phát bài hát"," playSong()");


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
        //Sửa lại nút hiển thị giao diện play khi dưng bài hát
        mIMusicRemote.updateUIbtnPlay();
        playNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        Log.v("tag","Chuẩn bị phát");
        mp.start();

        //Cập nhật thời gian phát seekbar
        mIMusicRemote.updateMaxSeekbar(getDur());
        myHandler.postDelayed(UpdateSongTime,100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(mPlayer.isPlaying()) {
                mIMusicRemote.updateSeekbar(getPosn());
                mIMusicRemote.updateTextTime(convertToTimeFommat(getPosn()),convertToTimeFommat(getDur()));
            }
            myHandler.postDelayed(this, mInterval);
        }
    };

    public boolean isPlayer() {
        return (mPlayer != null);
    }

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



}


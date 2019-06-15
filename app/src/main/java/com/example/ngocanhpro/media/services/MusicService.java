package com.example.ngocanhpro.media.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.MainActivity;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Song;


import java.util.ArrayList;


public class MusicService extends  Service implements
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {
    private int mInterval = 500; // khoảng thời gian cập nhật seek bar mili giây
    private SeekBar mSeekBar;
    private MediaPlayer player; //media player
    private ArrayList<Song> songs; // Danh sách bài hát
    private int songPosn = 0; // vị trí bài hát được phát
    private final IBinder musicBind = new MusicBinder();
    private String songTitle=""; // Tên bài hát phát
    private static final int NOTIFY_ID=1;
    private boolean mIsPlaySong = false; // Kiểm tra xem bài hát phát không
    TextView tvTitleSong;
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }


    //Khởi tạo music layer
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //Lấy vị trí seekbar
    public int getPosn(){
        return player.getCurrentPosition();
    }

    //Lấy tổng thời gian phát
    public int getDur(){
        return player.getDuration();
    }

    //Kiểm tra xem bài hát đang phát hay ngừng
    public boolean isPng(){
        return player.isPlaying();
    }

    //Dừng bài hát đang phát
    public void pausePlayer(){
        player.pause();
    }

    //Tua bài hát bằng seek bar
    public void seek(int posn){
        player.seekTo(posn);
    }

    //Phát bài hát
    public void go(){
        player.start();
        mProgressRunner.run();
    }

    //Chuyển bái hát vừa phát hoặc bài hát xếp trước
    public void playPrev(){
        songPosn--;
        if(songPosn == 0) {
            songPosn=songs.size()-1;
        }
        playSong();
    }

    //Chuyển bài hát tiếp theo
    public void playNext(){
        songPosn++;
        if(songPosn == songs.size()) {
            songPosn=0;
        }
        playSong();
    }

    public void playSong(){
        mIsPlaySong = true;
        player.reset();
        //get song
        Song song = songs.get(songPosn);
        songTitle=song.getTitle();
        long currSong = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();


    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
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

        int duration = mp.getDuration();
//        mSeekBar.setMax(duration);
//        mSeekBar.postDelayed(mProgressRunner, mInterval);

    }

    //Đặt danh sách bài hát từ data cho Service
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    //Đặt bài hát phát
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    //Lấy bài hát đang phát
    public Song getSong(){
        return songs.get(songPosn);
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    //Phát tiếp bài hát sau khi bài hát vừa phát kết thúc
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0){
            mp.reset();
            playNext();
//           tvTitleSong.setText(songTitle); // cập nhật lại tên bài hát trên text view name song khi bắt đầu phát

        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //Cập nhật seek bar mỗi giây
    private Runnable mProgressRunner = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar != null) {
                mSeekBar.setProgress(player.getCurrentPosition());

                if(player.isPlaying()) {
                    mSeekBar.postDelayed(mProgressRunner, mInterval);
                }
            }
        }
    };

    /**
     * Sets seekBar to control while playing music
     * @param seekBar - Seek bar instance that's already on our UI thread
     */
    public void setUIControls(SeekBar seekBar) {
        mSeekBar = seekBar;
      //  mCurrentPosition = currentPosition;
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public boolean getIsPlaySong() {
        return mIsPlaySong;
    }

    public void setTextViewTitleSong(TextView tv){
        tvTitleSong = tv;
    }

    public String getSongTitle(){
        return songTitle;
    }
}


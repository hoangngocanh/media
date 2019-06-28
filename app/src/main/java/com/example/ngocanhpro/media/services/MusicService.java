package com.example.ngocanhpro.media.services;

import android.app.Service;
import android.os.Handler;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.FragmentListSong;
import com.example.ngocanhpro.media.IMusicRemote;
import com.example.ngocanhpro.media.MainActivity;
import com.example.ngocanhpro.media.R;
import com.example.ngocanhpro.media.enity.Song;


public class MusicService extends  Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private int mInterval = 500; // khoảng thời gian cập nhật seek bar mili giây
    private SeekBar mSeekBar1, mSeekBar2;
    private MediaPlayer player; //media player
    private ArrayList<Song> songs; // Danh sách bài hát
    private int songPosn = 0; // vị trí bài hát được phát
    private final IBinder musicBind = new MusicBinder();
    private String songTitle=""; // Tên bài hát phát
    private static final int NOTIFY_ID=1;
    private boolean mIsPlaySong = false; // Kiểm tra xem bài hát phát không
    TextView tvTitleSong1, tvTitleSong2, tvTotalTime, tvTimeRun;
    private boolean isPrepared = false;


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
        mProgressRunner1.run();
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

        int duration = mp.getDuration();
        mSeekBar1.setMax(duration); // cài đặt giá trị max cho seekbar
        mSeekBar1.postDelayed(mProgressRunner1, mInterval);
        mSeekBar2.setMax(duration);
        mSeekBar2.postDelayed(mProgressRunner2, mInterval);
        tvTitleSong1.setText(songTitle); // cập nhật lại tên bài hát trên text view name song khi bắt đầu phát
        tvTitleSong2.setText(songTitle);
        tvTotalTime.setText("" + convertToTimeFommat(player.getDuration()));// cập nhật tổng thời gian phát
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //Cập nhật seek bar, thời gian phát bìa hát mỗi giây
    private Runnable mProgressRunner1 = new Runnable() {
        @Override
        public void run() {
            if (mSeekBar1 != null) {
                mSeekBar1.setProgress(player.getCurrentPosition());

                if (player.isPlaying()) {
                    mSeekBar1.postDelayed(mProgressRunner1, mInterval);
                }
            }

        }
    };

    //Cập nhật seek bar, thời gian phát bìa hát mỗi giây
    private Runnable mProgressRunner2 = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                tvTimeRun.setText(""+convertToTimeFommat(player.getCurrentPosition()));
            }
            if (mSeekBar2 != null) {
                mSeekBar2.setProgress(player.getCurrentPosition());

                if (player.isPlaying()) {
                    mSeekBar2.postDelayed(mProgressRunner2, mInterval);
                }
            }

        }
    };


    /**
     * Sets seekBar to control while playing music
     * @param seekBar - Seek bar instance that's already on our UI thread
     */
    public void setUIControls1(SeekBar seekBar) {
        mSeekBar1 = seekBar;
        //  mCurrentPosition = currentPosition;
        mSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
    public void setUIControls2(SeekBar seekBar) {
        mSeekBar2 = seekBar;
        //  mCurrentPosition = currentPosition;
        mSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    /* đặt text view tên bài hát

     */
    public void setTextViewTitleSong1(TextView tv) {
        tvTitleSong1 = tv;
    }
    public void setTextViewTitleSong2(TextView tv) {
        tvTitleSong2 = tv;
    }

    public void setTextTime(TextView tv1, TextView tv2) {
        tvTimeRun = tv1;
        tvTotalTime = tv2;
    }

    public boolean getIsPlaySong() {
        return mIsPlaySong;
    }

    public String getSongTitle(){
        return songTitle;
    }

    //Chuyển định dạng mili giây về phút:giây
    public String convertToTimeFommat(int t) {
        int phut = t/60000;
        int giay = (t-phut*60000)/1000;
        return phut+":"+giay;
    }

}


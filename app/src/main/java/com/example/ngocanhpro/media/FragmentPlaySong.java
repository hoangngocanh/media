package com.example.ngocanhpro.media;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentPlaySong extends Fragment{
    SeekBar seekBar;
    TextView tvNameOfSong, tvTimeRun, tvTotalTime;

    IControlPlayMedia iControlPlayMedia;
    public void setOnHeadlineSelectedListener(IControlPlayMedia control) {
        this.iControlPlayMedia = control;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_music, container, false);
        final TextView nameSongView = (TextView) v.findViewById(R.id.title) ;
        seekBar = (SeekBar) v.findViewById(R.id.seekBar) ;
        seekBar.setMax(iControlPlayMedia.getDur());
        setSeekBarTouch(seekBar);
        tvNameOfSong = (TextView) v.findViewById(R.id.name_song);
        tvNameOfSong.setText(iControlPlayMedia.getNameSong());
        tvTimeRun = (TextView) v.findViewById(R.id.time_run);
        tvTotalTime = (TextView) v.findViewById(R.id.total_time);
        tvTotalTime.setText(convertToTimeFommat(iControlPlayMedia.getDur()));
        Button btnPrev = (Button) v.findViewById(R.id.prev);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               iControlPlayMedia.prevSong();
            }
        });

        final Button btnPlay = (Button) v.findViewById(R.id.play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (iControlPlayMedia.isPlaying()) {
                    iControlPlayMedia.pauseMedia();
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                } else {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    iControlPlayMedia.playMedia();
                }

            }
        });

        final Button btnNext = (Button) v.findViewById(R.id.next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                iControlPlayMedia.nextSong();
            }
        });
        Log.d("FragmentListSong",":oncreatview");

        return v;

    }

    @Override
    public void onStart() {
        Log.d("fragmentListSong", ": onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("fragmentListSong", ": onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("fragmentPlaySong", ": onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("fragmentPlaySong", ": onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("fragmentPlaySong", "fragmentListSong: onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("fragmentPlaySong", ": onDestroy");
        super.onDestroy();
    }


    public void updateSeekbar(int position) {
        if(seekBar != null) {
            seekBar.setProgress(position);
//            Log.d("Seekbar",": seek"+position);
        }
    }

    public void updateMaxSeekbar(int duration) {
        if (seekBar != null) {
            seekBar.setMax(duration);
        }
    }

    public void setSeekBarTouch(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Change current position of the song playback
                    iControlPlayMedia.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void setTextNameSong(String s){
        if(tvNameOfSong != null) {
            tvNameOfSong.setText(s);
        }
    }
    public void setTextTimePosn(String posn){
        tvTimeRun.setText(posn);
    }

    public String convertToTimeFommat(int t) {
        int phut = t/60000;
        int giay = (t-phut*60000)/1000;
        return phut+":"+giay;
    }


}

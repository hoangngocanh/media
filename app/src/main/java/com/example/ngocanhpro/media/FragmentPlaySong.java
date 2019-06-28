package com.example.ngocanhpro.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentPlaySong extends Fragment {
    SeekBar seekBar;
    TextView tvNameOfSong, tvTimeRun, tvTotalTime;

    ControlPlayMedia controlPlayMedia;
    public void setOnHeadlineSelectedListener(ControlPlayMedia control) {
        this.controlPlayMedia = control;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_music, container, false);
        final TextView nameSongView = (TextView) v.findViewById(R.id.title) ;
        seekBar = (SeekBar) v.findViewById(R.id.seekBar) ;
        tvNameOfSong = (TextView) v.findViewById(R.id.name_song);
        tvTimeRun = (TextView) v.findViewById(R.id.time_run);
        tvTotalTime = (TextView) v.findViewById(R.id.total_time);
        Button btnPrev = (Button) v.findViewById(R.id.prev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               controlPlayMedia.prevSong();
            }
        });
        controlPlayMedia.setSeekbar2(seekBar);
        controlPlayMedia.setNameSong2(tvNameOfSong);
        controlPlayMedia.setTextTime(tvTimeRun,tvTotalTime);

        final Button btnPlay = (Button) v.findViewById(R.id.play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (controlPlayMedia.isPlaying()) {
                    controlPlayMedia.pauseMedia();
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                } else {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    controlPlayMedia.playMedia();
                }

            }
        });

        final Button btnNext = (Button) v.findViewById(R.id.next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                controlPlayMedia.nextSong();
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


}

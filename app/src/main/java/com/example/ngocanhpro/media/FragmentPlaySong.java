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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentPlaySong extends Fragment implements Runnable {
    SeekBar seekBar;

    ControlPlayMedia controlPlayMedia;
    public interface ControlPlayMedia {
        void pauseMedia();
        boolean isPlaying();
        int getDur();
        int getCurrentPosition();
        void playMedia();
        void nextSong();
        void prevSong();

    }


    public void setOnHeadlineSelectedListener(FragmentPlaySong.ControlPlayMedia control) {
        this.controlPlayMedia = control;
    }

    @Override
    public void run(){
        int currentPosition = controlPlayMedia.getCurrentPosition();
        int total = controlPlayMedia.getDur();


        while (controlPlayMedia.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(500);
                currentPosition = controlPlayMedia.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_music, container, false);
        final TextView nameSongView = (TextView) v.findViewById(R.id.title) ;
        seekBar = (SeekBar) v.findViewById(R.id.seekBar) ;
        Button btnPrev = (Button) v.findViewById(R.id.prev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               controlPlayMedia.prevSong();
            }
        });

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


        return v;

    }





}

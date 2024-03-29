package com.example.ngocanhpro.media.fragment;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ngocanhpro.media.interf.IControlPlayMedia;
import com.example.ngocanhpro.media.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FragmentPlaySong extends Fragment{
    SeekBar seekBar;
    TextView tvNameOfSong, tvTimeRun, tvTotalTime;
    ImageView imgSong;
    Button btnPlay;
    private ImageLoader mImageLoader;


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
        imgSong = (ImageView) v.findViewById(R.id.img_music);
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

        btnPlay = (Button) v.findViewById(R.id.play);
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

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, iControlPlayMedia.getSong().getAlbumID());
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.loadImage(String.valueOf(albumArtUri),new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                imgSong.setImageBitmap(loadedImage);
            }
        });

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
        if(tvTimeRun != null) {
            tvTimeRun.setText(posn);
        }
    }

    public String convertToTimeFommat(int t) {
        int phut = t/60000;
        int giay = (t-phut*60000)/1000;
        return phut+":"+giay;
    }

    public void setBtnPlay() {
        if (btnPlay != null)
            btnPlay.setBackgroundResource(R.drawable.ic_play);
    }
    public void setBtnPause() {
        if (btnPlay != null)
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
    }


}

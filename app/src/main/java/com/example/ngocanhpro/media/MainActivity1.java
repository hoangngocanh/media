package com.example.ngocanhpro.media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class MainActivity1 extends AppCompatActivity {

    ImageButton like, notlike,dislike,notdislike;
    ImageButton play,pause,play_main,pause_main;
    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        play = (ImageButton) findViewById(R.id.play_button);
        pause = (ImageButton) findViewById(R.id.pause_button);
        play_main = (ImageButton) findViewById(R.id.play_button_main);
        pause_main = (ImageButton) findViewById(R.id.pause_button_main);


        mLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main);

    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}

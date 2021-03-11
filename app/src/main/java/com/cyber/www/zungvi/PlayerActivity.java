package com.cyber.www.zungvi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cyber.www.zungvi.Adapters.ProgramAdapter;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    YouTubePlayer player;
    Button fullScreen;

    ListView lvProgram;

    String[] programName = {"Africa news"};
    String[] programDescription = {""};

    int[] programImages = {R.drawable.france24};


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.yt_player);

        YouTubePlayerView playerView = findViewById(R.id.youTubePlayerView);
        playerView.initialize(DeveloperKey.DEVELOPER_KEY,this);

        fullScreen = findViewById(R.id.button2);
        lvProgram = findViewById(R.id.lvProgram);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setFullscreen(true);
            }
        });

        lvProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(new Intent(getApplicationContext(), TvActivity.class));
                            overridePendingTransition(0,0);
                        }
                    }, 500);

            }
        });

        ProgramAdapter programAdapter = new ProgramAdapter(this,programName,programImages,programDescription);
        lvProgram.setAdapter(programAdapter);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        this.player = player;

        if(!b){

            player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            // ID of the live TV
            if(!DataStore.getInstance(this).channelExist()){
                player.loadVideo("b6R9-7KZ8YM");
            }else if(DataStore.getInstance(this).getTvChannel() == "ABC News"){
                player.loadVideo("w_Ma8oQLmSM");
            }else if(DataStore.getInstance(this).getTvChannel() == "Bloomberg"){
                player.loadVideo("dp8PhLsUcFE");
            }else if(DataStore.getInstance(this).getTvChannel() == "Gospel Hydration"){
                player.loadVideo("nXWPWZkC9QY");
            }else if(DataStore.getInstance(this).getTvChannel() == "Mishapi voice Tv"){
                player.loadVideo("FVDlM6Doqa4");
            }else{
                player.loadVideo("b6R9-7KZ8YM");
            }



        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,"Something went wrong "+youTubeInitializationResult,Toast.LENGTH_LONG).show();
    }

    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, Main2Activity.class));
    }
}

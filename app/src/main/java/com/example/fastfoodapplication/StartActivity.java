package com.example.fastfoodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public class StartActivity extends AppCompatActivity {

    private static final String LOGTAG = ControllerActivity.class.getName();
    private Button tutorialButton;
    private Button playButton;
    private Button leaderboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tutorialButton =findViewById(R.id.activity_start_tutorial_button);
        playButton =findViewById(R.id.activity_start_play_button);
        leaderboardButton =findViewById(R.id.activity_start_leaderboard_button);

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOGTAG, tutorialButton.getId() + " clicked");
                Intent intent = new Intent(StartActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOGTAG, playButton.getId() + " clicked");
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOGTAG,leaderboardButton.getId() + " clicked");
                Intent intent = new Intent(StartActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        ServerHandler.instance.startConnectService(this); //TODO might have to do this everywhere but have to test first
    }
}
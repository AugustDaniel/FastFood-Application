package com.example.fastfoodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {

    private static final String logTag = ControllerActivity.class.getName();
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

        tutorialButton = findViewById(R.id.activity_start_tutorial_button);
        playButton = findViewById(R.id.activity_start_play_button);
        leaderboardButton = findViewById(R.id.activity_start_leaderboard_button);

        tutorialButton.setOnClickListener(view -> {
            Log.v(logTag, tutorialButton.getId() + " clicked");
            Intent intent = new Intent(StartActivity.this, TutorialActivity.class);
            startActivity(intent);
        });

        playButton.setOnClickListener(view -> {
            Log.v(logTag, playButton.getId() + " clicked");
            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        leaderboardButton.setOnClickListener(view -> {
            Log.v(logTag, leaderboardButton.getId() + " clicked");
            Intent intent = new Intent(StartActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });
    }
}
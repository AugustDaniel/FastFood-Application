package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fastfoodlib.util.Lap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinishActivity extends AppCompatActivity {
    private static final String logTag = FinishActivity.class.getName();
    private LapAdapter lapRecyclerViewAdapter;
    private TextView scoreText;
    private TextView rankText;
    private TextView nameText;
    private Button buttonContinue;

    private String playerName;
    private String playerRank;
    private String playerScore;
    private List<Lap> laps = new ArrayList<>();

    @StringRes
    private int status = R.string.waiting_on_results_text;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finish);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_finish_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView lapRecyclerView = findViewById(R.id.activity_finish_recycler_view);
        lapRecyclerViewAdapter = new LapAdapter(laps);
        lapRecyclerView.setAdapter(lapRecyclerViewAdapter);
        lapRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        scoreText = findViewById(R.id.activity_finish_player_score_text);
        rankText = findViewById(R.id.activity_finish_player_rank_text);
        nameText = findViewById(R.id.activity_finish_player_name_text);
        buttonContinue = findViewById(R.id.activity_finish_continue_button);

        nameText.setText(status);

        buttonContinue.setOnClickListener(view -> {
            Intent intent = new Intent(FinishActivity.this, StartActivity.class);
            startActivity(intent);

            Toast.makeText(view.getContext(), R.string.thanks_text, Toast.LENGTH_LONG).show();
            Toast.makeText(view.getContext(), R.string.leaderboard_toast_text, Toast.LENGTH_LONG).show();
        });

        if (savedInstanceState == null) {
            loadResults();
        } else {
            restoreState(savedInstanceState);
        }
    }

    private void loadResults() {
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String name = getSharedPreferences("my_prefs", MODE_PRIVATE).getString("name", "Jane Doe");

                List<Lap> results = ServerHandler.getResults();
                ServerHandler.disconnect();
                Collections.sort(results);
                laps = results;

                Optional<Lap> personalBestOptional = results.stream()
                        .filter(lap -> lap.getName().equals(name))
                        .findFirst();

                if (personalBestOptional.isPresent()) {
                    Lap personalBest = personalBestOptional.get();

                    playerName = name;
                    playerRank = "#" + (results.indexOf(personalBest) + 1);
                    playerScore = personalBest.getLapTimeFormatted();
                } else {
                    playerName = getString(R.string.no_laps_set_text);
                }

                handler.post(() -> {
                    updateUI();
                    lapRecyclerViewAdapter.setLaps(results);
                    lapRecyclerViewAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.error_text), Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }

    private void updateUI() {
        nameText.setText(playerName);
        rankText.setText(playerRank);
        scoreText.setText(playerScore);
        lapRecyclerViewAdapter.setLaps(laps);
        lapRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void restoreState(Bundle savedInstanceState) {
        playerName = savedInstanceState.getString("nameText");
        playerRank = savedInstanceState.getString("rankText");
        playerScore = savedInstanceState.getString("scoreText");
        laps = (List<Lap>) savedInstanceState.getSerializable("laps");
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nameText", playerName);
        outState.putString("rankText", playerRank);
        outState.putString("scoreText", playerScore);
        outState.putSerializable("laps", (Serializable) laps);
    }
}
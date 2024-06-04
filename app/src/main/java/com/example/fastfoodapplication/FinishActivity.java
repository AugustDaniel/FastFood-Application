package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fastfoodlib.util.Lap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FinishActivity extends AppCompatActivity {

    private RecyclerView lapRecyclerView;
    private LapAdapter lapRecyclerViewAdapter;

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.O)
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

        lapRecyclerView = findViewById(R.id.activity_finish_recycler_view);
        lapRecyclerViewAdapter = new LapAdapter(this, new ArrayList<>());
        lapRecyclerView.setAdapter(lapRecyclerViewAdapter);
        lapRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView scoreText = findViewById(R.id.activity_finish_player_score_text);
        TextView rankText = findViewById(R.id.activity_finish_player_rank_text);
        TextView nameText = findViewById(R.id.activity_finish_player_name_text);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String name = getSharedPreferences("my_prefs", MODE_PRIVATE).getString("name", "Jane Doe");

                List<Lap> results = ServerHandler.getResults();
                Collections.sort(results);

                Lap personalBest = results.stream()
                        .filter(lap -> lap.getName().equals(name))
                        .findFirst()
                        .get();

                handler.post(() -> {
                    nameText.setText(name);
                    rankText.setText(Integer.toString(results.indexOf(personalBest) + 1));
                    scoreText.setText(personalBest.getLapTimeFormatted());
                });

                lapRecyclerViewAdapter.setLaps(results);
                lapRecyclerViewAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }
}
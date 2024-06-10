package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Button buttonContinue = findViewById(R.id.activity_finish_button_continue);

        nameText.setText(R.string.waiting_on_results);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.v(LOGTAG, buttonContinue.getId() + " clicked");
                Intent intent = new Intent(FinishActivity.this, StartActivity.class);
                startActivity(intent);

                Toast.makeText(view.getContext(),R.string.thanks,Toast.LENGTH_LONG).show();
                Toast.makeText(view.getContext(),R.string.see_leaderboard,Toast.LENGTH_LONG).show();
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String name = getSharedPreferences("my_prefs", MODE_PRIVATE).getString("name", "Jane Doe");

                List<Lap> results = ServerHandler.getResults();
                Collections.sort(results);

                Optional<Lap> personalBestOptional = results.stream()
                        .filter(lap -> lap.getName().equals(name))
                        .findFirst();

                if (personalBestOptional.isPresent()) {
                    Lap personalBest = personalBestOptional.get();

                    handler.post(() -> {
                        nameText.setText(name);
                        rankText.setText("#"+  Integer.toString(results.indexOf(personalBest) + 1));
                        scoreText.setText(personalBest.getLapTimeFormatted());
                    });
                } else {
                    handler.post(() -> {
                        nameText.setText("No laps set");
                    });
                }

                lapRecyclerViewAdapter.setLaps(results);
                lapRecyclerViewAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }
}
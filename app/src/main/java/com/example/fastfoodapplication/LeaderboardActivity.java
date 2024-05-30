package com.example.fastfoodapplication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {
    private Set<Lap> leaderboard;

    private RecyclerView lapRecyclerView;
    private LapAdapter lapRecyclerViewAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        lapRecyclerView = findViewById(R.id.activity_leaderboard_recycler_view_laps);

        lapRecyclerViewAdapter = new LapAdapter(this, new ArrayList<>(leaderboard));
        lapRecyclerView.setAdapter(lapRecyclerViewAdapter);
        lapRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        executor.execute(() -> {
            try {
                leaderboard = ServerHandler.instance.requestLeaderboard();
                if(leaderboard!=null){
                lapRecyclerViewAdapter.setLaps(new ArrayList<>(leaderboard));}
                // adapter data change call
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show());
            }
        });




        System.out.println(leaderboard);
    }
}
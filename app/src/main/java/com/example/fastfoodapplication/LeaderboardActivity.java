package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {
    private List<Lap> leaderboard = new ArrayList<>();

    private RecyclerView lapRecyclerView;
    private LapAdapter lapRecyclerViewAdapter;
    private Spinner spinnerFilter;

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

        spinnerFilter = findViewById(R.id.activity_leaderboard_spinner_filter);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerFilter.setAdapter(adapter);
//        System.out.println(spinnerFilter.getSelectedItem());

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                System.out.println(parent.getItemAtPosition(position));
                int stringResourceID = getResources().getIdentifier(selectedItem, "string", getPackageName());

                lapRecyclerViewAdapter.sortList(selectedItem);
                lapRecyclerViewAdapter.notifyDataSetChanged();
                System.out.println(stringResourceID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        executor.execute(() -> {
            try {
                leaderboard = ServerHandler.requestLeaderboard();

                if (leaderboard != null) {
                    lapRecyclerViewAdapter.setLaps(leaderboard);
                    lapRecyclerViewAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show());
            }
        });
    }

}
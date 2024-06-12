package com.example.fastfoodapplication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String logTag = LeaderboardActivity.class.getName();
    private List<Lap> leaderboard = new ArrayList<>();
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

        RecyclerView lapRecyclerView = findViewById(R.id.activity_leaderboard_laps_recycler_view);

        lapRecyclerViewAdapter = new LapAdapter(new ArrayList<>(leaderboard));
        lapRecyclerView.setAdapter(lapRecyclerViewAdapter);
        lapRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Spinner spinnerFilter = findViewById(R.id.activity_leaderboard_spinner_filter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filter_options, R.layout.text_filter_spinner);
        adapter.setDropDownViewResource(R.layout.dropdown_filter_spinner);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                int stringResourceID = getResources().getIdentifier(selectedItem, "string", getPackageName());

                handler.post(() -> {
                    lapRecyclerViewAdapter.sortList(selectedItem);
                    lapRecyclerViewAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        executor.execute(() -> {
            try {
                leaderboard = ServerHandler.requestLeaderboard();
                Collections.sort(leaderboard);

                if (leaderboard != null) {
                    handler.post(() -> {
                        lapRecyclerViewAdapter.setLaps(leaderboard);
                        lapRecyclerViewAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.error_text), Toast.LENGTH_LONG).show());
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                executor.shutdownNow();
                ServerHandler.disconnect();
                finish();
            }
        });
    }
}
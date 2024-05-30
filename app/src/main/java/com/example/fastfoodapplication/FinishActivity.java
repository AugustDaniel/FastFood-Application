package com.example.fastfoodapplication;

import android.os.Build;
import android.os.Bundle;

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

public class FinishActivity extends AppCompatActivity {

    private RecyclerView lapRecyclerView;
    private LapAdapter lapRecyclerViewAdapter;
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

        ArrayList<Lap> laps =new ArrayList<Lap>();
        laps.add(new Lap("Circuit", LocalTime.of(3, 32, 45), LocalDate.of(2023, 5, 30)));
        laps.add(new Lap("Circuit", LocalTime.of(2, 32, 45), LocalDate.of(2023, 5, 30)));

        //TODO set server data in recyclerview

        lapRecyclerView = findViewById(R.id.activity_finish_recycler_view);

        lapRecyclerViewAdapter = new LapAdapter(this, laps);
        lapRecyclerView.setAdapter(lapRecyclerViewAdapter);
        lapRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
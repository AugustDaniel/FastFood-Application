package com.example.fastfoodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.spec.ECField;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadingActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LOADING_ACTIVITY";
    private ImageView spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinner = findViewById(R.id.activity_loading_spinner_imageview);

        RotateAnimation rotate = new RotateAnimation(0, 360 * 999, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(360 * 999 * 10);
        rotate.setInterpolator(new LinearInterpolator());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                ServerHandler.instance.waitForStart();
                ServerHandler.instance.sendLap(new Lap("test", LocalTime.now(), LocalDate.now()));
                handler.post(() -> {
                    Intent intent = new Intent(LoadingActivity.this, ControllerActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                Log.d(LOG_TAG, Objects.requireNonNull(e.getMessage()));
                handler.post(() -> Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show());
                finish();
            }
        });
        spinner.startAnimation(rotate);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }
}
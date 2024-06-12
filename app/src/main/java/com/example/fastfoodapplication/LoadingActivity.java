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

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadingActivity extends AppCompatActivity {

    private static final String logTag = LoadingActivity.class.getName();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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

        ImageView spinner = findViewById(R.id.activity_loading_spinner_imageview);

        RotateAnimation rotate = new RotateAnimation(0, 360 * 999, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(360 * 999 * 10);
        rotate.setInterpolator(new LinearInterpolator());

        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                ServerHandler.waitForStart();
                Log.d(logTag, "done waiting for start");
                handler.post(() -> {
                    Intent intent = new Intent(LoadingActivity.this, ControllerActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();

                if (!(e instanceof SocketException)) {
                    handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                }

                finish();
            }
        });
        spinner.startAnimation(rotate);

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
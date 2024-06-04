package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fastfoodlib.util.Lap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerActivity extends AppCompatActivity {

    private static final String LOGTAG = ControllerActivity.class.getName();
    private ImageButton controllerLeft;
    private ImageButton controllerRight;
    private ImageButton controllerGasPedal;
    private ImageButton controllerBreakPedal;
    private LinearLayout background;
    private TextView countdownText;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controllerLeft = findViewById(R.id.activity_controller_button_left);
        controllerRight = findViewById(R.id.activity_controller_button_right);
        controllerGasPedal = findViewById(R.id.activity_controller_button_gas_pedal);
        controllerBreakPedal = findViewById(R.id.activity_controller_button_break_pedal);
        background = findViewById(R.id.activity_controller_background_linear_layout);
        countdownText = findViewById(R.id.activity_controller_countdown_text_view);

        new CountDownTimer(4000, 1000) {
            private int counter = 4;

            @Override
            public void onTick(long millisUntilFinished) {
                counter--;

                if (counter != 0) {
                    countdownText.setText(String.format("%o", counter));
                } else {
                    countdownText.setText(R.string.start);
                }
            }

            @Override
            public void onFinish() {
                background.setBackgroundColor(Color.TRANSPARENT);
                countdownText.setText("");

                controllerLeft.setOnClickListener(view -> Log.v(LOGTAG, controllerLeft.getId() + " clicked"));
                controllerRight.setOnClickListener(view -> Log.v(LOGTAG, controllerRight.getId() + " clicked"));
                controllerGasPedal.setOnClickListener(view -> Log.v(LOGTAG, controllerGasPedal.getId() + " clicked"));
                controllerBreakPedal.setOnClickListener(view -> Log.v(LOGTAG, controllerBreakPedal.getId() + " clicked"));
            }
        }.start();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "Jane Doe");

//                //todo test code
                ServerHandler.sendLap(new Lap(name, LocalTime.now(), LocalDate.now()));
                ServerHandler.sendLap(new Lap(name, LocalTime.now(), LocalDate.now()));
                ServerHandler.sendLap(new Lap(name, LocalTime.now(), LocalDate.now()));

                handler.post(() -> {
                    Intent intent = new Intent(ControllerActivity.this, FinishActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ServerHandler.disconnect();
                finish();
            }
        });
    }
}
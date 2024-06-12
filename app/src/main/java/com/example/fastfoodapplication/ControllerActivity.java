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
import android.view.MotionEvent;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerActivity extends AppCompatActivity {

    private static final String logTag = ControllerActivity.class.getName();
    private ImageButton controllerLeft;
    private ImageButton controllerRight;
    private ImageButton controllerGasPedal;
    private ImageButton controllerBreakPedal;
    public TextView carNameText;
    private LinearLayout background;
    private TextView countdownText;
    private ExecutorService executor = Executors.newFixedThreadPool(2);

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

        controllerLeft = findViewById(R.id.activity_controller_left_button);
        controllerRight = findViewById(R.id.activity_controller_right_button);
        controllerGasPedal = findViewById(R.id.activity_controller_gas_pedal_button);
        controllerBreakPedal = findViewById(R.id.activity_controller_break_pedal_button);
        background = findViewById(R.id.activity_controller_background_linear_layout);
        countdownText = findViewById(R.id.activity_controller_countdown_text);
        carNameText = findViewById(R.id.activity_controller_car_name_text);

        BrokerHandler.instance.createConnection(getApplicationContext(), this);

        new CountDownTimer(4000, 1000) {
            private int counter = 4;

            @Override
            public void onTick(long millisUntilFinished) {
                counter--;

                if (counter != 0) {

                    countdownText.setText(String.format("%o", counter));
                } else {
                    countdownText.setText(R.string.start_text);
                }
            }

            @Override
            public void onFinish() {
                background.setBackgroundColor(Color.TRANSPARENT);
                countdownText.setText("");
                setOnTouch();
            }
        }.start();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                stopServer();
                finish();
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                if (ServerHandler.waitForTimeOut()) {
                    Log.d(logTag, "race timed out");
                    handler.post(() -> {
                        Intent intent = new Intent(ControllerActivity.this, FinishActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.error_text), Toast.LENGTH_LONG).show());
                stopServer();
                finish();
            }
        });
    }

    @Override
    public void finish() {
        BrokerHandler.instance.setIsOnController(false);
        BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RESET, "t");
        super.finish();
    }

    public void sendLaps(LocalTime lap) {
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d(logTag, "laptime " + lap.toString());
        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "Jane Doe");
                Log.d(logTag, "going to send lap");
                ServerHandler.sendLap(new Lap(name, lap, LocalDate.now()));
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.error_text), Toast.LENGTH_LONG).show());
                stopServer();
                finish();
            }
        });
    }

    private void stopServer() {
        ServerHandler.disconnect();
        executor.shutdownNow();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouch() {

        controllerLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(logTag, controllerLeft.getId() + " clicked");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT, "t");
                controllerLeft.setImageResource(R.drawable.arrow_left_button_pressed);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(logTag, controllerLeft.getId() + " released");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT, "f");
                controllerLeft.setImageResource(R.drawable.arrow_left_button);

            }
            return true;
        });
        controllerRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(logTag, controllerRight.getId() + " clicked");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT, "t");
                controllerRight.setImageResource(R.drawable.arrow_left_button_pressed);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(logTag, controllerRight.getId() + " released");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT, "f");
                controllerRight.setImageResource(R.drawable.arrow_left_button);

            }
            return true;
        });
        controllerGasPedal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(logTag, controllerGasPedal.getId() + " clicked");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS, "t");
                controllerGasPedal.setImageResource(R.drawable.pedal_yellow_pressed);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(logTag, controllerGasPedal.getId() + " released");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS, "f");
                controllerGasPedal.setImageResource(R.drawable.pedal_yellow);

            }
            return true;
        });
        controllerBreakPedal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.v(logTag, controllerBreakPedal.getId() + " clicked");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK, "t");
                controllerBreakPedal.setImageResource(R.drawable.pedal_red_pressed);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(logTag, controllerBreakPedal.getId() + " released");
                BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK, "f");
                controllerBreakPedal.setImageResource(R.drawable.pedal_red);
            }
            return true;
        });
    }
}
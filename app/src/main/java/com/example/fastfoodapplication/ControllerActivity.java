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
import android.view.View;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerActivity extends AppCompatActivity{

    private static final String LOGTAG = ControllerActivity.class.getName();
    private ImageButton controllerLeft;
    private ImageButton controllerRight;
    private ImageButton controllerGasPedal;
    private ImageButton controllerBreakPedal;
    public TextView carNameText;
    private LinearLayout background;
    private TextView countdownText;
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
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
                    countdownText.setText(R.string.start);
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
                executor.shutdownNow();
                ServerHandler.disconnect();
                finish();
            }
        });
    }

    private void setOnTouch() {

        controllerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerLeft.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"t");
                    controllerLeft.setImageResource(R.drawable.arrow_left_button_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerLeft.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"f");
                    controllerLeft.setImageResource(R.drawable.arrow_left_button);

                }
                return true;
            }

        });
        controllerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerRight.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"t");
                    controllerRight.setImageResource(R.drawable.arrow_left_button_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerRight.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"f");
                    controllerRight.setImageResource(R.drawable.arrow_left_button);

                }
                return true;
            }

        });
        controllerGasPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS,"t");
                    controllerGasPedal.setImageResource(R.drawable.pedal_yellow_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS,"f");
                    controllerGasPedal.setImageResource(R.drawable.pedal_yellow);

                }
                return true;
            }

        });
        controllerBreakPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerBreakPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"t");
                    controllerBreakPedal.setImageResource(R.drawable.pedal_red_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerBreakPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"f");
                    controllerBreakPedal.setImageResource(R.drawable.pedal_red);
                }
                return true;
            }

        });

        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                if (ServerHandler.waitForTimeOut()) {
                    System.out.println("done waiting for start");
                    handler.post(() -> {
                        Intent intent = new Intent(ControllerActivity.this, FinishActivity.class);
                        startActivity(intent);
                        System.out.println("started");
//                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }

    public void sendLaps(LocalTime lap){
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "Jane Doe");

                if (ServerHandler.sendLap(new Lap(name, lap, LocalDate.now()))) {
                    handler.post(() -> {
                        Intent intent = new Intent(ControllerActivity.this, FinishActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                finish();
            }
        });
    }
}
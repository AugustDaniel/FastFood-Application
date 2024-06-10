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

import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fastfoodlib.util.Lap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerActivity extends AppCompatActivity{

    private static final String LOGTAG = ControllerActivity.class.getName();
    private ImageButton controllerLeft;
    private ImageButton controllerRight;
    private ImageButton controllerGasPedal;
    private ImageButton controllerBreakPedal;
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

                controllerLeft.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"t");

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"f");

                        }
                        return true;
                    }

                });
                controllerRight.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"t");

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"f");

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

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS,"f");

                        }
                        return true;
                    }

                });
                controllerBreakPedal.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"t");

                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                            BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"f");

                        }
                        return true;
                    }

                });

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

    public void sendLaps(ArrayList<LocalTime> laps){
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "Jane Doe");

                for (LocalTime lap : laps) {
                    ServerHandler.sendLap(new Lap(name, lap, LocalDate.now()));
                }

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
    }
}
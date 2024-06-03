package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerActivity extends AppCompatActivity implements BrokerObserver {

    private static final String LOGTAG = ControllerActivity.class.getName();
    private ImageButton controllerLeft;
    private ImageButton controllerRight;
    private ImageButton controllerGasPedal;
    private ImageButton controllerBreakPedal;


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

        BrokerHandler.instance.createConnection(getApplicationContext());

        controllerLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"true");

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.LEFT,"false");

                }
                return true;
            }

        });


        controllerRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"true");

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.RIGHT,"false");

                }
                return true;
            }

        });

        controllerGasPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS,"true");

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.GAS,"false");

                }
                return true;
            }

        });

        controllerBreakPedal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " clicked");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"true");

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.v(LOGTAG,controllerGasPedal.getId() + " released");
                    BrokerHandler.instance.publishMessage(BrokerHandler.topicType.BREAK,"false");

                }
                return true;
            }

        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "Jane Doe");

                //todo test code
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
    @Override
    public void update(MqttMessage data) {

    }

}
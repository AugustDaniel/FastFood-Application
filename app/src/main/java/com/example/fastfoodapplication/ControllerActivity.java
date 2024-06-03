package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.eclipse.paho.client.mqttv3.MqttMessage;

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
    }


    @Override
    public void update(MqttMessage data) {

    }

}
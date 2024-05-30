package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity implements BrokerObserver {
    private Button continueButton;
    private TextInputEditText textfield;
    private static final String LOGTAG = ControllerActivity.class.getName();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        continueButton = findViewById(R.id.activity_register_button_continue);
        textfield = findViewById(R.id.activity_register_text_input_name);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        continueButton.setOnClickListener(view -> {
            Log.v(LOGTAG, continueButton.getId() + " clicked");

            executor.execute(() -> {
                try {
                    Log.d(LOGTAG, "Going to join race");
                    ServerHandler.instance.startRace();
                    handler.post(() -> {
                        Log.d(LOGTAG, "Going to start race");
                        Intent intent = new Intent(RegisterActivity.this, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    Log.d(LOGTAG, Objects.requireNonNull(e.getMessage()));
                    handler.post(() -> Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show());
                }
            });
        });
    }

    @Override
    public void update(MqttMessage data) {

    }

    @Override
    public void sendMessage(BrokerHandler.topicType topicType, String message) {
        BrokerObserver.super.sendMessage(topicType, message);
    }
}

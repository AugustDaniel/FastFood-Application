package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private static final String logTag = ControllerActivity.class.getName();
    private Button continueButton;
    private TextInputEditText textField;
    private static final String defaultName = "Jane Doe";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

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

        continueButton = findViewById(R.id.activity_register_continue_button);
        textField = findViewById(R.id.activity_register_name_text_input);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", defaultName);
        if (!name.equals(defaultName)) {
            textField.setText(name);
        }

        Handler handler = new Handler(Looper.getMainLooper());

        continueButton.setOnClickListener(view -> {
            Log.v(logTag, continueButton.getId() + " clicked");

            if (textField.getText() == null || textField.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.vul_naam_in, Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", textField.getText().toString());
            editor.apply();

            executor.execute(() -> {
                try {
                    Log.d(logTag, "Going to join race");
                    ServerHandler.joinRace();
                    handler.post(() -> {
                        Log.d(logTag, "Going to start race");
                        Intent intent = new Intent(RegisterActivity.this, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ServerHandler.disconnect();
                    handler.post(() -> Toast.makeText(RegisterActivity.this, getResources().getString(R.string.er_is_iets_mis_gegaan), Toast.LENGTH_LONG).show());
                }
            });
        });
    }
}

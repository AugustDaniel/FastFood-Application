package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private Button continueButton;
    private TextInputLayout textfield;
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

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOGTAG, continueButton.getId() + " clicked");
//                if (textfield.getEditText() != null) {
                ServerHandler.instance.startRace();
                Intent intent = new Intent(RegisterActivity.this, LoadingActivity.class);
                startActivity(intent);
//                } else {
//                    displayToastError();
//                }
            }
        });
    }

    private void displayToastError() {
        Toast.makeText(this, "Fill in name", Toast.LENGTH_LONG).show();
    }

}

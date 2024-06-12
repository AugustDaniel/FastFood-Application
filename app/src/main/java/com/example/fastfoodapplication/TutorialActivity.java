package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class TutorialActivity extends AppCompatActivity {

    private static final String logTag = ControllerActivity.class.getName();
    private Button continueButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        continueButton = findViewById(R.id.activity_tutorial_continue_button);
        ImageView arrowsImageView = findViewById(R.id.activity_tutorial_arrows_image_view);
        ImageView pedalsImageView = findViewById(R.id.activity_tutorial_pedals_image_view);


        continueButton.setOnClickListener(view -> {
            Log.v(logTag,continueButton.getId() + " clicked");
            Intent intent = new Intent(TutorialActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Log.d(logTag, " language: " + Locale.getDefault().getLanguage());
        if(Locale.getDefault().getLanguage().equals("nl")){
            arrowsImageView.setImageResource(R.drawable.arrow_tutorial_nl);
            pedalsImageView.setImageResource(R.drawable.pedals_tutorial_nl);
        }
    }
}

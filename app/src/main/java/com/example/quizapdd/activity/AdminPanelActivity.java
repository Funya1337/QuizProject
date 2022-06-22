package com.example.quizapdd.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapdd.R;

public class AdminPanelActivity extends AppCompatActivity {

    CardView cardHome;
    CardView cardLessons;
    CardView cardCreate;
    CardView cardDelete;
    CardView cardSettings;
    CardView cardExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        cardHome = findViewById(R.id.cardHome);
        cardLessons = findViewById(R.id.cardLessons);
        cardCreate = findViewById(R.id.cardCreate);
        cardDelete = findViewById(R.id.cardDelete);
        cardSettings = findViewById(R.id.cardSettings);
        cardExit = findViewById(R.id.cardExit);

        cardHome.setOnClickListener(view -> {
            Intent intent = new Intent(AdminPanelActivity.this, MainActivity.class);
            AdminPanelActivity.this.startActivity(intent);
        });

        cardLessons.setOnClickListener(view -> {
            Intent intent = new Intent(AdminPanelActivity.this, VideoLessonsActivity.class);
            AdminPanelActivity.this.startActivity(intent);
        });

        cardExit.setOnClickListener(view -> {
            Intent intent = new Intent(AdminPanelActivity.this, MainActivity.class);
            AdminPanelActivity.this.startActivity(intent);
        });

        cardCreate.setOnClickListener(view -> {
            Intent intent = new Intent(AdminPanelActivity.this, CreateTestActivity.class);
            AdminPanelActivity.this.startActivity(intent);
        });

    }
}
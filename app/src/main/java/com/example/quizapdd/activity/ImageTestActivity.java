package com.example.quizapdd.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizapdd.R;

public class ImageTestActivity extends AppCompatActivity {

    CardView card1;
    CardView card2;
    CardView card3;

    Boolean checker1 = false;
    Boolean checker2 = false;
    Boolean checker3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker1 = true;
                if (!checker2 && !checker3)
                    card1.setCardBackgroundColor(Color.GREEN);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker2 = true;
                if (!checker1 && !checker3)
                    card2.setCardBackgroundColor(Color.RED);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker3 = true;
                if (!checker1 && !checker2)
                    card3.setCardBackgroundColor(Color.RED);
            }
        });

    }
}
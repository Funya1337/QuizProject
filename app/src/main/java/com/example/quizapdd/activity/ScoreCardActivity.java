package com.example.quizapdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapdd.R;

import java.util.ArrayList;

public class ScoreCardActivity extends AppCompatActivity {

    ListView simpleListView;
    TextView resultTextView;
    Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);

        simpleListView = findViewById(R.id.simpleListView);
        resultTextView = findViewById(R.id.textViewResult);
        btnMain = findViewById(R.id.btnMain);

        Intent intent = getIntent();
        ArrayList value = intent.getParcelableArrayListExtra("data");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.list_view, R.id.itemTextView, value);
        simpleListView.setAdapter(arrayAdapter);

        resultTextView.setText("Было верно решено: " + String.valueOf(getPlayerScore(value)));

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreCardActivity.this, MainActivity.class);
                ScoreCardActivity.this.startActivity(intent);
            }
        });

        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(value);


    }

    public int getPlayerScore(ArrayList data) {
        int cnt = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals("Score"))
                cnt += 1;
        }
        return cnt;
    }

}
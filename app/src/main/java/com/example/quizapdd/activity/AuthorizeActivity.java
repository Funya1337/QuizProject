package com.example.quizapdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapdd.R;

public class AuthorizeActivity extends AppCompatActivity {

    Button btnLogIn;
    EditText editTextLogin;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        btnLogIn = findViewById(R.id.btnLogIn);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = String.valueOf(editTextLogin.getText());
                String password = String.valueOf(editTextPassword.getText());
                if (login.equals("test") && password.equals("test")) {
                    Intent intent = new Intent(AuthorizeActivity.this, AdminPanelActivity.class);
                    AuthorizeActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),"Wrong login or password!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
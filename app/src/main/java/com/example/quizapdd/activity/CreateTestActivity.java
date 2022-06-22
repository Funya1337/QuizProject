package com.example.quizapdd.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapdd.R;
import com.example.quizapdd.constants.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class CreateTestActivity extends AppCompatActivity {

    Button btnAdd;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        btnAdd = findViewById(R.id.btnAdd);
        editText = findViewById(R.id.editText);
        btnAdd.setOnClickListener(view -> {
            String title = String.valueOf(editText.getText());
            writeToJson(getApplicationContext(), title);
        });
    }

    public void writeToJson(Context context, String dataToSend) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Address", "NO");
            jsonObject.put("Name", "Vasya");
            jsonObject.put("Enroll_No", "123123");
            jsonObject.put("Mobile", "234234");
            jsonObject.put("Branch", "YES");
            String userString = jsonObject.toString();
            File file = new File(context.getFilesDir().getPath() + "/question_set.json");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            String data = "This is new content";
//            String filePath = getApplicationContext().getFilesDir().getPath() + "/test.txt";
//            File file = new File(filePath);
//            System.out.println(filePath);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write(data);
//            System.out.println("Done");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String readJson(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(AppConstants.CONTENT_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
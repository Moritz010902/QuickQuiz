package com.devkjg.quickquiz;

import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;


public class QuizHostActivity extends AppCompatActivity {

    Connection.Host host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_host);


        //TODO: remove test code
        host = new Connection.Host(this);
        host.enableConnection(30000);

    }

    private void recordQuestion() {

    }

    private void recordAnswers() {

    }

    private void showQuestion() {

    }

    private void showAnswers() {

    }

}
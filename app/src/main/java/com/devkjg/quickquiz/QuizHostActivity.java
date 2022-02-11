package com.devkjg.quickquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


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
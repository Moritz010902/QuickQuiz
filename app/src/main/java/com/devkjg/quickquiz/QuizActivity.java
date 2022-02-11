package com.devkjg.quickquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.gridlayout.widget.GridLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QuizActivity extends AppCompatActivity {

    Connection.Client client;

    GridLayout gridLayout;
    TextView answerA;
    TextView answerB;
    TextView answerC;
    TextView answerD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        gridLayout = findViewById(R.id.gridLayout);
        Utility.autoScaleLayoutChildren(gridLayout);

        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);

        answerA.setOnClickListener(view -> {
            answerA.animate().start();
            initiateWaitingRoom(1);
        });
        answerB.setOnClickListener(view -> {
            answerB.animate().start();
            initiateWaitingRoom(2);
        });
        answerC.setOnClickListener(view -> {
            answerC.animate().start();
            initiateWaitingRoom(3);
        });
        answerD.setOnClickListener(view -> {
            answerD.animate().start();
            initiateWaitingRoom(4);
        });

        //TODO: remove test code
        Log.i("CONNECTION", "continue as player");
        client = new Connection.Client(this);
        client.connectToHost(20000);

    }

    private void initiateWaitingRoom(int answer) {

        setContentView(R.layout.quiz_waitingroom);
        ConstraintLayout layout = findViewById(R.id.waitingRoom);
        //TODO hier weitermachen
        int size = layout.getWidth()*2/3;
        layout.findViewById(R.id.progressBar).setMinimumHeight(size);
        layout.findViewById(R.id.progressBar).setMinimumWidth(size);

        Utility.runJustBeforeBeingDrawn(layout, new Runnable() {
            @Override
            public void run() {
                int size = layout.getWidth()*2/3;
                layout.findViewById(R.id.progressBar).setMinimumHeight(size);
                layout.findViewById(R.id.progressBar).setMinimumWidth(size);
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fenster schließen");
        builder.setMessage("Möchten Sie dieses Quiz wirklich verlassen?");
        builder.setPositiveButton("Bestätigen", (dialogInterface, i) -> QuizActivity.super.onBackPressed());
        builder.setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();

    }

}
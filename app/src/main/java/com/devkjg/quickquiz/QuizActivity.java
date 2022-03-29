package com.devkjg.quickquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;


public class QuizActivity extends AppCompatActivity {

    private final String logTag = "QUIZ_ACTIVITY";
    private boolean connectedToQuiz;
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

        connectedToQuiz = true;
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
        TextView textView = layout.findViewById(R.id.textView3);
        //TODO hier weitermachen
        Runnable run = new Runnable() {
            @Override
            public void run() {

                String str = "waiting for other players ";
                String text = textView.getText().toString();
                if(text.length() == 0)
                    text = str;
                text = text.substring(str.length());

                text += ".";
                if(text.length() > 3)
                    text = ".";

                final String finalText = str+text;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(finalText);
                    }
                });
                Log.d(logTag, textView.getText().toString());
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(connectedToQuiz)
                    this.run();
            }
        };
        Thread waiting = new Thread(run);
        waiting.start();
        /*
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
*/
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fenster schließen");
        builder.setMessage("Möchten Sie dieses Quiz wirklich verlassen?");
        builder.setPositiveButton("Bestätigen", (dialog, which) -> QuizActivity.super.onBackPressed());
        builder.setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();

    }

    @Override
    protected void onDestroy() {
        connectedToQuiz = false;
        super.onDestroy();
    }
}
package com.devkjg.quickquiz;

import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speechRecognizer = new SpeechRecognizer(this, new OnRecognizeAction() {
            @Override
            public void run() {
                //TODO: implement quiz process
                Toast.makeText(getApplicationContext(), getRecognitionResult(), Toast.LENGTH_SHORT).show();
                speechRecognizer.stopListening();
            }
        });

        //TODO: remove test code
        View btnHost = findViewById(R.id.createGame);
        View btnPlayer = findViewById(R.id.enterGame);
        final boolean[] block = {false};
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(getApplicationContext(), QuizHostActivity.class);
                startActivity(intent);
                 */
                if(!block[0]) {
                    Log.e("MODE", "send broadcast packets");
                    TestConnection c = new TestConnection(getApplicationContext());
                    c.broadcastGameInvitation(1234,3000);
                    block[0] = true;
                }
            }
        });

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                startActivity(intent);
                 */
                if(!block[0]) {
                    Log.e("MODE", "listening");
                    TestConnection c = new TestConnection(getApplicationContext());
                    c.listenForGameInvitation(1234);
                    block[0] = true;
                }
            }
        });

    }
}
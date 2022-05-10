package com.devkjg.quickquiz;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.devkjg.quickquiz.connection.Role;
import com.devkjg.quickquiz.connection.TestConnection;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    Thread connectionThread;
    static TestConnection connection;

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

        connection = new TestConnection(getApplicationContext());

        //TODO: remove test code
        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
        startActivity(intent);



        View btnHost = findViewById(R.id.createGame);
        View btnPlayer = findViewById(R.id.joinGame);

        ((TextView) btnHost.findViewById(R.id.buttonText)).setText(R.string.button_create);
        ((TextView) btnPlayer.findViewById(R.id.buttonText)).setText(R.string.button_join);

        final boolean[] block = {false};
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // generate random gameId
                String gameId = "";
                Random r = new Random();
                int[] numbers = {r.nextInt(10), r.nextInt(10), r.nextInt(10), r.nextInt(10)};
                ArrayList<Integer> indexes = new ArrayList<>();
                while(indexes.size() < 4) {
                    int nextIndex = r.nextInt(4);
                    if(!Utility.contains(nextIndex, indexes)) {
                        gameId += String.valueOf(numbers[nextIndex]);
                        indexes.add(nextIndex);
                    }
                }


                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                intent.putExtra("host", true);
                startActivity(intent);

                if(!block[0]) {
                    Log.e("ROLE", "server");
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            //connection.setRole(ConnectionRole.SERVER);
                            connection.broadcastGameInvitation(1234,3000);
                        }
                    };
                    connectionThread = new Thread(run);
                    connectionThread.start();
                    block[0] = true;
                }
            }
        });

        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);

                if(!block[0]) {
                    Log.e("ROLE", "client");
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            connection.setRole(Role.CLIENT);
                            connection.listenForGameInvitation(1234);
                        }
                    };
                    connectionThread = new Thread(run);
                    connectionThread.start();
                    block[0] = true;
                }
            }
        });

    }
}
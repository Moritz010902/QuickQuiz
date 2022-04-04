package com.devkjg.quickquiz;

import android.content.Intent;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.gridlayout.widget.GridLayout;
import java.util.ArrayList;


public class LobbyActivity extends AppCompatActivity {

    boolean host;
    String myName;
    ArrayList<View> players = new ArrayList<>();

    EditText enterName;

    TextView lobbyId;
    TextView hostName;
    TextView playerAmount;
    GridLayout gridLayout;
    Button startQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_name);

        host = getIntent().getBooleanExtra("host", false);

        enterName = findViewById(R.id.enterName);
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(15);
        enterName.setFilters(inputFilter);


        //TODO: stop broadcasting gameId when activity is destroyed
    }

    public void isHost(boolean b) {
        host = b;
    }

    public void join(View v) {
        myName = enterName.getText().toString();
        if(enterName.length() != 0) {
            setContentView(R.layout.activity_lobby);

            lobbyId = findViewById(R.id.textView6);
            hostName = findViewById(R.id.textView5);
            playerAmount = findViewById(R.id.textView8);

            gridLayout = findViewById(R.id.gridLayout);
            gridLayout.setColumnCount(1);
            startQuiz = findViewById(R.id.button_start);

            if(host) {
                hostName.setText(myName);
            } else {
                addPlayer(myName);
                startQuiz.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addPlayer(String name) {

        TextView player = new TextView(getApplicationContext());
        player.setText(name);

        players.add(player);
        gridLayout.addView(player);
        playerAmount.setText(String.valueOf(players.size()));
    }

    public void startQuiz(View v) {
        Intent intent = new Intent(getApplicationContext(), QuizHostActivity.class);
        startActivity(intent);

        //TODO: broadcast event to all players
    }

}
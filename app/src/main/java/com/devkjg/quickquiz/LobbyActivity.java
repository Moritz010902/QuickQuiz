package com.devkjg.quickquiz;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Random;

public class LobbyActivity extends AppCompatActivity {

    GridLayout gridLayout;
    ArrayList<View> players = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(0);


        //TODO: stop broadcasting gameId when activity is destroyed
    }

    public void addPlayer(String name) {

        TextView player = new TextView(getApplicationContext());
        player.setText(name);

        players.add(player);
        gridLayout.addView(player);
    }

}
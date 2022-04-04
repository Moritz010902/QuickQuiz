package com.devkjg.quickquiz;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.InputFilter;
import android.text.method.BaseKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class JoinActivity extends AppCompatActivity {

    String gameID;
    EditText field1;
    EditText field2;
    EditText field3;
    EditText field4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        gameID = "";

        field1 = findViewById(R.id.editTextNumber2);
        field2 = findViewById(R.id.editTextNumber3);
        field3 = findViewById(R.id.editTextNumber4);
        field4 = findViewById(R.id.editTextNumber5);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(1);
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if(hasFocus) {
                if(gameID.length() == 0) {
                    field1.requestFocus();
                    inputMethodManager.showSoftInput(field1, 0);
                } else if(gameID.length() == 1) {
                    field2.requestFocus();
                    inputMethodManager.showSoftInput(field2, 0);
                } else if(gameID.length() == 2) {
                    field3.requestFocus();
                    inputMethodManager.showSoftInput(field3, 0);
                } else {
                    field4.requestFocus();
                    inputMethodManager.showSoftInput(field4, 0);
                }
            }
        };

        View.OnKeyListener keyListener = (v, keyCode, event) -> {

            int key = event.getKeyCode();

            //returning true would avoid the call of method onBackPressed()
            if (key == KeyEvent.KEYCODE_BACK)
                return false;
            //filters KEY_DOWN event to avoid a second call of this method
            if (event.getAction()!=KeyEvent.ACTION_DOWN)
                return true;
            if(key == KeyEvent.KEYCODE_ENTER) {
                if(inputMethodManager.isActive(field1))
                    inputMethodManager.hideSoftInputFromWindow(field1.getWindowToken(), 0);
                else if(inputMethodManager.isActive(field2))
                    inputMethodManager.hideSoftInputFromWindow(field2.getWindowToken(), 0);
                else if(inputMethodManager.isActive(field3))
                    inputMethodManager.hideSoftInputFromWindow(field3.getWindowToken(), 0);
                else if(inputMethodManager.isActive(field4))
                    inputMethodManager.hideSoftInputFromWindow(field4.getWindowToken(), 0);

                if(gameID.length() == 4) {
                    int id = Integer.parseInt(gameID);
                    //TODO: search for a game with given id
                    boolean found = true;
                    if(found) {
                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Dieses Quiz existiert nicht.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if(key == KeyEvent.KEYCODE_DEL || key == KeyEvent.KEYCODE_FORWARD_DEL) {
                if(gameID.length() > 0)
                    gameID = gameID.substring(0, gameID.length()-1);
            } else if(gameID.length() < 4){
                String text = String.valueOf(KeyCharacterMap.load(event.getDeviceId()).getDisplayLabel(key));
                try {
                    Integer.parseInt(text);
                } catch (Exception e) {
                    return true;
                }
                gameID += text;
            }

            if(gameID.length() > 0) {
                field1.setText(String.valueOf(gameID.charAt(0)));
            } else {
                field1.setText(null);
            }

            if(gameID.length() > 1) {
                field2.setText(String.valueOf(gameID.charAt(1)));
            } else {
                field2.setText(null);
            }

            if(gameID.length() > 2) {
                field3.setText(String.valueOf(gameID.charAt(2)));
            } else {
                field3.setText(null);
            }

            if(gameID.length() > 3) {
                field4.setText(String.valueOf(gameID.charAt(3)));
            } else {
                field4.setText(null);
            }

            focusChangeListener.onFocusChange(v, true);
            return true;
        };

        field1.setFilters(filters);
        field2.setFilters(filters);
        field3.setFilters(filters);
        field4.setFilters(filters);

        field1.setCursorVisible(false);
        field2.setCursorVisible(false);
        field3.setCursorVisible(false);
        field4.setCursorVisible(false);

        field1.setOnKeyListener(keyListener);
        field2.setOnKeyListener(keyListener);
        field3.setOnKeyListener(keyListener);
        field4.setOnKeyListener(keyListener);

        field1.setOnFocusChangeListener(focusChangeListener);
        field2.setOnFocusChangeListener(focusChangeListener);
        field3.setOnFocusChangeListener(focusChangeListener);
        field4.setOnFocusChangeListener(focusChangeListener);

    }

}
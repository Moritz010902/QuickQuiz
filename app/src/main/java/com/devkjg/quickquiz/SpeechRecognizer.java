package com.devkjg.quickquiz;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;


public class SpeechRecognizer {

    String logTag = "SPEECH_RECOGNITION";
    Context context;
    OnRecognizeAction onRecognizeAction;
    android.speech.SpeechRecognizer recognizer;
    Intent speechRecognizerIntent;
    static final int recordAudioRequestCode = 683541;


    SpeechRecognizer(Context context, OnRecognizeAction onRecognizeAction) {

        this.context = context;
        this.onRecognizeAction = onRecognizeAction;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.RECORD_AUDIO}, recordAudioRequestCode);

        if(android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {

            recognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Spracherkennung nicht verfügbar.")
                    .setPositiveButton("beenden", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        }

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.i(logTag, "Spracherkennung bereit");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.i(logTag, "Sprache wird erkannt ...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                Log.i(logTag, "Ergebnis: " + data.get(0));
                onRecognizeAction.setRecognitionResult(data.get(0));
                onRecognizeAction.run();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

    }

    public void startListening() {
        recognizer.startListening(speechRecognizerIntent);
        Log.i(logTag, "Spracherkennung wird gestartet");
    }

    public void stopListening() {
        recognizer.stopListening();
        Log.i(logTag, "Spracherkennung gestoppt");
    }

    public void setOnRecognizeAction(OnRecognizeAction onRecognizeAction) {
        this.onRecognizeAction = onRecognizeAction;
    }

}

package com.devkjg.quickquiz;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.InvalidObjectException;


public class QuizHostActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    boolean isRecording;

    ConstraintLayout quizInput;
    ConstraintLayout inputQuestion;
    ConstraintLayout inputQuestionButtons;
    ConstraintLayout inputAnswers;
    ConstraintLayout inputAnswersButtons;

    TextView textViewTitleQuestion;
    TextView textViewTextQuestion;
    Button recordQuestion;
    Button recordQuestion2;
    Button buttonContinue;

    TextView textViewA;
    TextView textViewB;
    TextView textViewC;
    TextView textViewD;
    Button recordA;
    Button recordB;
    Button recordC;
    Button recordD;
    ImageView deleteA;
    ImageView deleteB;
    ImageView deleteC;
    ImageView deleteD;
    Button buttonBack;
    Button buttonStart;

    Connection.Host host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_host);

        speechRecognizer = new SpeechRecognizer(getApplicationContext(), null);

        quizInput = findViewById(R.id.quiz_input);
        inputQuestion = findViewById(R.id.input_question);
        inputQuestionButtons = findViewById(R.id.input_question_btns);
        inputAnswers = findViewById(R.id.input_answers);
        inputAnswersButtons = findViewById(R.id.input_answers_btns);

        textViewTitleQuestion = findViewById(R.id.textView_titleQuestion);
        textViewTextQuestion = findViewById(R.id.textView_textQuestion);

        recordQuestion = findViewById(R.id.record_question);
        recordQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewTextQuestion.setText(getRecognitionResult());

                            recordQuestion.setVisibility(View.INVISIBLE);
                            textViewTitleQuestion.setVisibility(View.VISIBLE);
                            textViewTextQuestion.setVisibility(View.VISIBLE);
                            inputQuestionButtons.setVisibility(View.VISIBLE);

                            speechRecognizer.stopListening();
                            recordQuestion.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordQuestion.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordQuestion.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordQuestion.setTag(1);
                    isRecording = true;
                } else if(recordQuestion.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordQuestion.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordQuestion.setTag(0);
                    isRecording = false;
                }
            }
        });
        recordQuestion2 = findViewById(R.id.record_question2);
        recordQuestion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewTextQuestion.setText(getRecognitionResult());
                            speechRecognizer.stopListening();
                            recordQuestion2.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordQuestion2.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordQuestion2.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordQuestion2.setTag(1);
                    isRecording = true;
                } else if(recordQuestion2.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordQuestion2.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordQuestion2.setTag(0);
                    isRecording = false;
                }
            }
        });
        buttonContinue = findViewById(R.id.button_continue2);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputQuestion.setVisibility(View.INVISIBLE);
                inputQuestionButtons.setVisibility(View.INVISIBLE);
                inputAnswers.setVisibility(View.VISIBLE);
                inputAnswersButtons.setVisibility(View.VISIBLE);
            }
        });

        textViewA = findViewById(R.id.textView_textA);
        textViewB = findViewById(R.id.textView_textB);
        textViewC = findViewById(R.id.textView_textC);
        textViewD = findViewById(R.id.textView_textD);

        recordA = findViewById(R.id.record_A);
        recordA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewA.setText(getRecognitionResult());
                            speechRecognizer.stopListening();
                            recordA.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordA.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordA.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordA.setTag(1);
                    isRecording = true;
                } else if(recordA.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordA.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordA.setTag(0);
                    isRecording = false;
                }
            }
        });
        recordB = findViewById(R.id.record_B);
        recordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewB.setText(getRecognitionResult());
                            speechRecognizer.stopListening();
                            recordB.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordB.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordB.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordB.setTag(1);
                    isRecording = true;
                } else if(recordB.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordB.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordB.setTag(0);
                    isRecording = false;
                }
            }
        });
        recordC = findViewById(R.id.record_C);
        recordC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewC.setText(getRecognitionResult());
                            speechRecognizer.stopListening();
                            recordC.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordC.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordC.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordC.setTag(1);
                    isRecording = true;
                } else if(recordC.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordC.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordC.setTag(0);
                    isRecording = false;
                }
            }
        });
        recordD = findViewById(R.id.record_D);
        recordD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRecording) {
                    speechRecognizer.setOnRecognizeAction(new OnRecognizeAction() {
                        @Override
                        public void run() {
                            textViewD.setText(getRecognitionResult());
                            speechRecognizer.stopListening();
                            recordD.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                            recordD.setTag(0);
                            isRecording = false;
                        }
                    });
                    speechRecognizer.startListening();
                    recordD.setBackgroundColor(getResources().getColor(R.color.green, null));
                    recordD.setTag(1);
                    isRecording = true;
                } else if(recordD.getTag().equals(1)) {
                    speechRecognizer.stopListening();
                    recordD.setBackgroundColor(getResources().getColor(R.color.purple_500, null));
                    recordD.setTag(0);
                    isRecording = false;
                }
            }
        });

        deleteA = findViewById(R.id.delete_A);
        deleteA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewA.setText("");
            }
        });
        deleteB = findViewById(R.id.delete_B);
        deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewB.setText("");
            }
        });
        deleteC = findViewById(R.id.delete_C);
        deleteC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewC.setText("");
            }
        });
        deleteD = findViewById(R.id.delete_D);
        deleteD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewD.setText("");
            }
        });

        buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAnswers.setVisibility(View.INVISIBLE);
                inputAnswersButtons.setVisibility(View.INVISIBLE);
                inputQuestion.setVisibility(View.VISIBLE);
                inputQuestionButtons.setVisibility(View.VISIBLE);
            }
        });
        buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show question and answers and start timer
            }
        });


        //TODO: remove test code
        host = new Connection.Host(this);
        //host.enableConnection(30000);

    }

    private void showQuestion() {

    }

    private void showAnswers() {

    }

}
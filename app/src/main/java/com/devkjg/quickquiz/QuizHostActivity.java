package com.devkjg.quickquiz;

import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.*;
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

    TextView textViewTitleA;
    TextView textViewTitleB;
    TextView textViewTitleC;
    TextView textViewTitleD;
    ImageView isRightAnswerA;
    ImageView isRightAnswerB;
    ImageView isRightAnswerC;
    ImageView isRightAnswerD;
    EditText textViewA;
    EditText textViewB;
    EditText textViewC;
    EditText textViewD;
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

    ConstraintLayout controlVoting;

    TextView showQuestion;
    TextView showCountdown;
    ProgressBar progressBarCountdown;
    Button finishCountdown;
    ImageView pauseCountdown;

    Handler countdownHandler;
    final Thread[] countdownThread = {null};
    final int[] pStatus = {30};

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

        textViewTitleA = findViewById(R.id.textView_titleA);
        textViewTitleA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals(0)) {
                    isRightAnswerA.setImageResource(R.drawable.ic_right_answer);
                    isRightAnswerA.setTag(1);
                    v.setTag(1);
                } else {
                    isRightAnswerA.setImageResource(R.drawable.ic_right_answer_inactive);
                    isRightAnswerA.setTag(0);
                    v.setTag(0);
                }
            }
        });
        textViewTitleB = findViewById(R.id.textView_titleB);
        textViewTitleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals(0)) {
                    isRightAnswerB.setImageResource(R.drawable.ic_right_answer);
                    isRightAnswerB.setTag(1);
                    v.setTag(1);
                } else {
                    isRightAnswerB.setImageResource(R.drawable.ic_right_answer_inactive);
                    isRightAnswerB.setTag(0);
                    v.setTag(0);
                }
            }
        });
        textViewTitleC = findViewById(R.id.textView_titleC);
        textViewTitleC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals(0)) {
                    isRightAnswerC.setImageResource(R.drawable.ic_right_answer);
                    isRightAnswerC.setTag(1);
                    v.setTag(1);
                } else {
                    isRightAnswerC.setImageResource(R.drawable.ic_right_answer_inactive);
                    isRightAnswerC.setTag(0);
                    v.setTag(0);
                }
            }
        });
        textViewTitleD = findViewById(R.id.textView_titleD);
        textViewTitleD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals(0)) {
                    isRightAnswerD.setImageResource(R.drawable.ic_right_answer);
                    isRightAnswerD.setTag(1);
                    v.setTag(1);
                } else {
                    isRightAnswerD.setImageResource(R.drawable.ic_right_answer_inactive);
                    isRightAnswerD.setTag(0);
                    v.setTag(0);
                }
            }
        });

        isRightAnswerA = findViewById(R.id.isAnswerRight_A);
        isRightAnswerB = findViewById(R.id.isAnswerRight_B);
        isRightAnswerC = findViewById(R.id.isAnswerRight_C);
        isRightAnswerD = findViewById(R.id.isAnswerRight_D);

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
                quizInput.setVisibility(View.INVISIBLE);
                controlVoting.setVisibility(View.VISIBLE);

                pStatus[0] = 30;
                progressBarCountdown.setMax(pStatus[0]);
                progressBarCountdown.setProgress(pStatus[0]);
                countdownThread[0].start();

            }
        });


        controlVoting = findViewById(R.id.controlVoting);
        showQuestion = findViewById(R.id.textView_textQuestion2);
        showCountdown = findViewById(R.id.show_countdown);
        progressBarCountdown = findViewById(R.id.progressBar_countdown);
        finishCountdown = findViewById(R.id.button_finishCountdown);
        pauseCountdown = findViewById(R.id.imageView_pauseCountdown);

        countdownHandler = new Handler();
        countdownThread[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus[0] >= 0) {
                    countdownHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBarCountdown.setProgress(pStatus[0], true);
                            showCountdown.setText(pStatus[0] + "s");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus[0]--;
                }
            }
        });

        showCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseCountdown.setVisibility(View.VISIBLE);
                showCountdown.setVisibility(View.INVISIBLE);
                //TODO: fix thread problem
                synchronized (countdownThread[0]) {
                    try {
                        countdownThread[0].wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pauseCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseCountdown.setVisibility(View.INVISIBLE);
                showCountdown.setVisibility(View.VISIBLE);
                countdownThread.notifyAll();
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
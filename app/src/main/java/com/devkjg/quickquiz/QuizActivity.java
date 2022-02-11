package com.devkjg.quickquiz;

import android.app.AlertDialog;
import android.content.Context;
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

    Client client;

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
        client = new Client(this);
        client.connectToHost(30000);

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


    /*
    https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip#:~:text=%20Java%20Socket%20Server%20Examples%20%28TCP%2FIP%29%20%201,Server%20%28single-threaded%29%0ANext%2C%20let%E2%80%99s%20see%20a%20more...%20More%20
    https://www.codejava.net/java-se/networking/java-socket-client-examples-tcp-ip
    https://www.viresist.org/java-tutorials/wie-kann-ich-uberprufen-ob-eine-app-auf-android-lauft/
     */
    static class Client extends QuizActivity {

        Context context;
        private final String logTag = "CONNECTION";

        private Socket socket;
        private static String SERVER_IP;
        private static final int SERVER_PORT = 6868;
        private PrintWriter output;
        private BufferedReader input;

        private Listener listener;
        private Message message;


        Client(Context context) {
            this.context = context;
        }

        public void connectToHost(long timeout) {

            long timestamp = System.currentTimeMillis();
            Runnable run = new Runnable() {
                @Override
                public void run() {

                    try {
                        SERVER_IP = getLocalIpAddress();
                        socket = new Socket(SERVER_IP, SERVER_PORT);
                        Log.i(logTag, "connected");
                        output = new PrintWriter(socket.getOutputStream());
                        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        message = new Message(output);
                        listener = new Listener(input);
                        listener.startListening();
                        client.send(Issue.ANSWER, "0");
                    } catch (IOException e) {
                        //TODO: check weather the QuizActivity is active or not
                        if((System.currentTimeMillis()-timestamp) < timeout) {
                            try {
                                synchronized (Thread.currentThread()) {
                                    Thread.currentThread().wait(2000);
                                }
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            Log.i(logTag, "try to connect ... ("+((int) (timeout-(System.currentTimeMillis()-timestamp))/1000)+"s left)");
                            this.run();
                        } else {
                            finish();
                        }
                    }

                }
            };
            new Thread(run).start();

        }

        public void disconnectFromHost() {
            listener.stopListening();
        }

        public void send(int issue, String message) {
            //TODO: check weather a connection exists
            String text = issue + ":" + message;
            this.message.setText(text);
            this.message.send();
        }

        private String getLocalIpAddress() throws UnknownHostException {

            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();

            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
        }


        private class Listener {

            boolean listen;
            Runnable run;

            Listener(BufferedReader reader) {

                run = new Runnable() {
                    @Override
                    public void run() {

                        while(listen) {
                            try {
                                final String message = reader.readLine();
                                if(message != null) {
                                    //TODO: handle message
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    String msg = String.valueOf(Integer.parseInt(message.split(":")[1])+1);
                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    client.send(QuizActivity.Client.Issue.ANSWER, msg);
                                    Log.i(logTag, "(client) new data: \"" + msg + "\"");
                                } else {
                                    //TODO: check if connection is still alive
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };

            }

            void startListening() {
                if(!listen) {
                    listen = true;
                    new Thread(run).start();
                }
            }

            void stopListening() {
                listen = false;
            }

        }

        private class Message {

            Runnable run;
            String text;

            Message(PrintWriter writer) {
                run = new Runnable() {
                    @Override
                    public void run() {
                        writer.write(text);
                        writer.flush();
                    }
                };
            }

            void setText(String text) {
                this.text = text;
            }

            void send() {
                new Thread(run).start();
            }

        }

        abstract static class Issue {

            static final int CONNECTION = 0;
            static final int CONNECTION_REQUEST = 1;
            static final int CONNECTION_CONFIRM = 2;
            static final int PROCESS = 3;
            static final int ANSWER = 4;

        }

    }
}
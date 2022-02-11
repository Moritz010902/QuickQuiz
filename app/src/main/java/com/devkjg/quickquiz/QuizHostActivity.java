package com.devkjg.quickquiz;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class QuizHostActivity extends AppCompatActivity {

    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_host);

        //TODO: remove test code
        client = new Client();
        client.enableConnection(30000);

    }

    private void recordQuestion() {

    }

    private void recordAnswers() {

    }

    private void showQuestion() {

    }

    private void showAnswers() {

    }

    /**
     https://www.tutorialspoint.com/client-server-programming-in-android
     */
    static class Client extends QuizHostActivity {

        private final String logTag = "CONNECTION";
        private Socket socket;
        private final int SERVER_PORT = 6868;
        private PrintWriter output;
        private BufferedReader input;

        private Listener listener;
        private Message message;


        public void enableConnection(long timeout) {

            long timestamp = System.currentTimeMillis();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    Log.i(logTag, "host: prepare ... ("+((int) (timeout-(System.currentTimeMillis()-timestamp))/1000)+"s left)");
                    try {
                        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                        try {
                            socket = serverSocket.accept();
                            output = new PrintWriter(socket.getOutputStream());
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            //TODO: research for socket connection possibilities

                            message = new Message(output);
                            listener = new Listener(input);
                            listener.startListening();

                        } catch (IOException e) {
                            if((System.currentTimeMillis()-timestamp) < timeout)
                                this.run();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(run).start();

        }

        public void disableConnection() {
            listener.stopListening();
        }

        public void send(int issue, String message) {
            //TODO: check weather a connection exists
            String text = issue + ":" + message;
            this.message.setText(text);
            this.message.send();
            //TODO: send to all connected clients
        }
        /*
        public void send(ArrayList<Client>() clients, int issue, String message) {

        }
        */

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
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    client.send(Issue.ANSWER, msg);
                                    Log.i(logTag, "(host) new data: \"" + msg + "\"");
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
                    Log.i(logTag, "host: ready for communication");
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
                        Log.i(logTag, "send data: \"" + text  + "\"");
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
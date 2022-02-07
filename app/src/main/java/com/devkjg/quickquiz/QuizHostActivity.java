package com.devkjg.quickquiz;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_host);
    }

    private void recordQuestion() {

    }

    private void recordAnswers() {

    }

    private void showQuestion() {

    }

    private void showAnswer() {

    }

    static abstract class Connection extends QuizHostActivity {

        private Socket socket;
        private static final int SERVER_PORT = 8080;
        private PrintWriter output;
        private BufferedReader input;

        private Listener listener;
        private Message message;


        public void enableConnection() {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                        try {
                            socket = serverSocket.accept();
                            output = new PrintWriter(socket.getOutputStream());
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            //TODO: research for socket connection possibilities
                            /*
                            https://www.tutorialspoint.com/client-server-programming-in-android
                             */
                            message = new Message(output);
                            listener = new Listener(input);
                            listener.startListening();

                        } catch (IOException e) {
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
            String text = issue + ":" + message;
            this.message.setText(text);
            this.message.send();
        }
        /*
        public void send(ArrayList<Client>() clients, int issue, String message) {

        }
        */
        private String getLocalIpAddress() throws UnknownHostException {

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
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
            static final int PROCESS = 1;
            static final int ANSWER = 2;

        }

    }

}
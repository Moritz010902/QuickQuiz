package com.devkjg.quickquiz;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;


/*
https://developer.android.com/training/data-storage/app-specific#java
https://www.programmerall.com/article/58251638107/
 */
public class TestConnection extends AppCompatActivity {

    private Context context;
    private InetAddress address = null;
    private Integer port = null;
    private QuoteClient client = null;


    TestConnection(Context context) {
        this.context = context;
    }

    public void broadcastGameInvitation(int gameId, long frequency) {
        new MulticastServer(String.valueOf(gameId), frequency);
    }

    public void listenForGameInvitation(int gameId) {
        new MulticastClient(String.valueOf(gameId));
    }

    private void connectTo(int gameId, InetAddress address, int port) {
        client = new QuoteClient(gameId, address, port);
    }


    private class QuoteClient {

        private DatagramSocket socket;
        private InetAddress hostAddress;
        private int port;


        QuoteClient(int gameId, InetAddress hostAddress, int port) {
            try{

                socket = new DatagramSocket();
                this.hostAddress = hostAddress;
                this.port = port;

                // response to invitation
                sendMessage(Issue.CONNECTION_REQUEST, String.valueOf(gameId), new RunOnComplete() {
                    @Override
                    public void run() {
                        if(Message.isIssue(getResult(), Issue.CONNECTION_CONFIRM)) {
                            Intent intent = new Intent(context.getApplicationContext(), LobbyActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String issue, String message, RunOnComplete onComplete) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {

                        String text = issue.hashCode() + ":" + message;
                        byte[] buf = text.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, hostAddress, port);
                        socket.send(packet);

                        // get response
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        // display response
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Quote of the Moment: " + received);

                        socket.close();
                        if(onComplete != null) {
                            onComplete.setResult(received);
                            onComplete.run();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(run).start();

        }

    }

    private class MulticastClient {

        private MulticastSocket socket;
        private InetAddress address;
        private WifiManager.MulticastLock multicastLock;


        MulticastClient(String expectMessage) {
            try{

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                socket = new MulticastSocket(4336);
                address = InetAddress.getByName("230.100.221.1");
                socket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
                socket.joinGroup(address);

                //receive messages from multicast addresses
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                multicastLock = wifiManager.createMulticastLock("multicast.quickquiz");
                multicastLock.acquire();

                //get a quote
                DatagramPacket packet;
                byte[] buf = new byte[256];
                //TODO: implement timeout
                boolean listen = true;
                while (listen) {
                    packet = new DatagramPacket(buf, buf.length);

                    Log.d("RECEIVE", "start listening");
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Quote received: " + received);

                    if(!Message.isValid(received))
                        continue;
                    if(Message.getContent(received).equals(expectMessage)) {
                        connectTo(Integer.parseInt(expectMessage), packet.getAddress(), packet.getPort());
                        listen = false;
                        disable();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void disable() {
            try {

                if(multicastLock.isHeld())
                    multicastLock.release();
                socket.leaveGroup(address);
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class QuoteServer {

        QuoteServer() {
            try {
                new QuoteServerThread().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class MulticastServer {

        MulticastServer(String message, long timeout) {
            try {
                new MulticastServerThread(message, timeout).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class QuoteServerThread extends Thread {

        protected DatagramSocket socket;
        protected BufferedReader in;
        protected boolean moreQuotes = true;


        public QuoteServerThread() throws IOException {
            this("QuoteServerThread");
        }

        public QuoteServerThread(String name) throws IOException {
            super(name);
            socket = new DatagramSocket(4346);
            File file = new File(context.getFilesDir(), "clients.txt");
            if(!file.exists())
                if(!file.createNewFile())
                    Log.e("SERVER", "failed to create registry file");
            FileInputStream fin = context.openFileInput(file.getName());
            in = new BufferedReader(new InputStreamReader(fin, StandardCharsets.UTF_8));
        }

        public void run() {

            while (moreQuotes) {
                try {
                    byte[] buf = new byte[256];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    // figure out response
                    String dString;
                    if (in == null) {
                        dString = new Date().toString();
                    } else {
                        dString = getNextQuote();
                    }
                    buf = dString.getBytes();

                    // send the response to the client at "address" and "port"
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    moreQuotes = false;
                }
            }
            socket.close();
        }

        protected String getNextQuote() {
            String returnValue = null;
            try {
                if ((returnValue = in.readLine()) == null) {
                    in.close();
                    //moreQuotes = false;
                    returnValue = "No more quotes. Goodbye.";
                }
            } catch (IOException e) {
                returnValue = "IOException occurred in server.";
            }
            return returnValue;
        }
    }

    private class MulticastServerThread extends QuoteServerThread {

        private long timeout;
        private String message;

        public MulticastServerThread(String message, long timeout) throws IOException {
            super("MulticastServerThread");
            this.timeout = timeout;
            this.message = message;
        }

        public void run() {
            try {
                InetAddress group = InetAddress.getByName("230.100.221.1");
                while (moreQuotes) {

                    // construct quote
                    byte[] buf = new Message(Issue.BROADCAST, message).getText().getBytes();
                    // send it
                    MulticastSocket s = new MulticastSocket(4336);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4336);
                    s.setBroadcast(true);
                    s.joinGroup(group);
                    s.send(packet);
                    Log.d("DATA SEND", Arrays.toString(buf) + " | " + System.currentTimeMillis()/3600);
                    //socket.send(packet);

                    // sleep for a while
                    synchronized (Thread.currentThread()) {
                        Thread.currentThread().wait(timeout);
                    }

                }
                socket.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
    }


    private static class Message {

        private static String finalMessage;

        Message(String issue, String message) {
            finalMessage = issue.hashCode() + ":" + message;
        }

        String getText() {
            return finalMessage;
        }

        static String getContent(String message) {
            String[] content = message.split(Pattern.quote(":"));
            if(content.length != 2)
                return null;
            return content[1];
        }

        static Boolean isIssue(String message, String issue) {
            String[] content = message.split(Pattern.quote(":"));
            if(content.length != 2)
                return null;
            return Integer.parseInt(content[0]) == issue.hashCode();
        }

        Boolean isIssue(String issue) {
            String[] content = finalMessage.split(Pattern.quote(":"));
            if(content.length != 2)
                return null;
            return Integer.parseInt(content[0]) == issue.hashCode();
        }

        static boolean isValid(String message) {
            return (message.split(Pattern.quote(":"))).length == 2;
        }

    }

    private abstract static class Issue {

        static final String BROADCAST = "broadcast";
        static final String CONNECTION_REQUEST = "connection_request";
        static final String CONNECTION_CONFIRM = "connection_confirm";
        static final String PROCESS = "process";
        static final String ANSWER = "answer";

    }

    private abstract class RunOnComplete implements Runnable {

        String result = "";

        String getResult() {
            return result;
        }

        void setResult(String result) {
            this.result = result;
        }

    }

}

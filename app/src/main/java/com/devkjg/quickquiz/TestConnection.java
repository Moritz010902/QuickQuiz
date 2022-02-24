package com.devkjg.quickquiz;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

/*
https://developer.android.com/training/data-storage/app-specific#java
 */
public class TestConnection extends AppCompatActivity {

    Context context;


    TestConnection(Context context) {
        this.context = context;
    }

    public void broadcastGameInvitation(long frequency) {
        new MulticastServer(frequency);
    }

    public void listenForGameInvitation() {
        new MulticastClient();
    }


    private class QuoteClient {

        QuoteClient(InetAddress hostAddress) {
            try{

                // get a datagram socket
                DatagramSocket socket = new DatagramSocket();

                // send request
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length, hostAddress, 4346);
                socket.send(packet);

                // get response
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // display response
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Quote of the Moment: " + received);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class MulticastClient {

        MulticastClient() {
            try{

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                MulticastSocket socket = new MulticastSocket(4336);
                InetAddress address = InetAddress.getByName("230.100.221.1");
                socket.joinGroup(address);

                DatagramPacket packet;
                Log.d("STATUS", "is connected -> " + socket.isClosed());
                // get a few quotes
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);

                Log.d("RECEIVE", "start listening");
                socket.receive(packet);Log.d("RECEIVE", "data received");

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Quote of the Moment: " + received);


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

        MulticastServer(long timeout) {
            try {
                new MulticastServerThread(timeout).start();
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

        public MulticastServerThread(long timeout) throws IOException {
            super("MulticastServerThread");
            this.timeout = timeout;
        }

        public void run() {
            try {
                InetAddress group = InetAddress.getByName("230.100.221.1");
                while (moreQuotes) {

                    // construct quote
                    String dString;
                    if (in == null) {
                        dString = new Date().toString();
                    } else {
                        dString = getNextQuote();
                    }
                    byte[] buf = dString.getBytes();
                    buf = "quizid:xyz".getBytes();
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

}

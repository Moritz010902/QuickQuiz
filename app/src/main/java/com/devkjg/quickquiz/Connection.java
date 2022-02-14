package com.devkjg.quickquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;

/*
https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
    https://www.tutorialspoint.com/client-server-programming-in-android
    https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip#:~:text=%20Java%20Socket%20Server%20Examples%20%28TCP%2FIP%29%20%201,Server%20%28single-threaded%29%0ANext%2C%20let%E2%80%99s%20see%20a%20more...%20More%20
    https://www.codejava.net/java-se/networking/java-socket-client-examples-tcp-ip
*/
public abstract class Connection extends AppCompatActivity {


    static final String logTag = "CONNECTION";
    final int SERVER_PORT = 6868;


    private static String getLocalIpAddress(Context context) throws UnknownHostException {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        Log.e(logTag, String.valueOf(wifiInfo.getIpAddress()));
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    private static InetAddress getLocalIpAddressHost(Context context) throws UnknownHostException {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        byte[] ip = {0};
        Log.e(logTag, String.valueOf(wifiInfo.getSSID()));
        return InetAddress.getByAddress(ip);
    }


    static class Host extends Connection {

        private Context context;
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;

        private Listener listener;
        private Message message;


        Host(Context context) {
            this.context = context;
            /*
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String address = InetAddress.getLocalHost().getHostAddress();
                Log.e(logTag, address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            */
            String ip;
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    // filters out 127.0.0.1 and inactive interfaces
                    if (iface.isLoopback() || !iface.isUp() || iface.isVirtual())
                        continue;

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        ip = addr.getHostAddress();
                        //System.out.println(iface.getDisplayName() + " " + ip);
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }


            try {
                Enumeration e = NetworkInterface.getNetworkInterfaces();
                int ctr=0;
                while(e.hasMoreElements())
                {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    Enumeration ee = n.getInetAddresses();
                    while (ee.hasMoreElements() && ctr<3)
                    {       ctr++;
                        if(ctr==3)
                            break;
                        InetAddress i = (InetAddress) ee.nextElement();
                        if(ctr==2) {
                            Log.e(logTag, i.getHostAddress());
                        }

                    }
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            }

            try(final DatagramSocket socket = new DatagramSocket()){
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
                socket.disconnect();
                Log.e(logTag, ip);
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }

        }

        public void enableConnection(long timeout) {

            long timestamp = System.currentTimeMillis();
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    Log.i(logTag, "host: prepare ... ("+((int) (timeout-(System.currentTimeMillis()-timestamp))/1000)+"s left)");
                    try {
                        ServerSocket serverSocket = new ServerSocket(SERVER_PORT, 10, getLocalIpAddressHost(context));Log.e(logTag, serverSocket.getInetAddress().getHostAddress());
                        try {
                            socket = serverSocket.accept();Log.e(logTag, String.valueOf(socket.isConnected()));Log.e(logTag, socket.getInetAddress().getHostAddress());
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
                                    send(Issue.ANSWER, msg);
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

    }


    static class Client extends Connection {

        Context context;
        private Socket socket;
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
                        Log.e(logTag, getLocalIpAddress(context) + " / " + SERVER_PORT);
                        socket = new Socket(getLocalIpAddress(context), SERVER_PORT);
                        Log.i(logTag, "connected");
                        output = new PrintWriter(socket.getOutputStream());
                        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        message = new Message(output);
                        listener = new Listener(input);
                        listener.startListening();
                        send(Issue.ANSWER, "0");
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

                            if (!((Activity) context).isDestroyed()) {
                                Log.i(logTag, "try to connect ... ("+((int) (timeout-(System.currentTimeMillis()-timestamp))/1000)+"s left)");
                                this.run();
                            }

                        } else {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Verbindung fehlgeschlagen");
                                    builder.setMessage("Stellen Sie sicher, dass sich alle Teilnehmer im selben Netzwerk befinden");
                                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((Activity) context).finish();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.setCancelable(false);
                                    dialog.show();
                                }
                            });

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


        private class Listener {

            boolean listen;
            Runnable run;

            Listener(BufferedReader reader) {

                run = new Runnable() {
                    @Override
                    public void run() {

                        while (listen) {
                            try {
                                final String message = reader.readLine();
                                if (message != null) {
                                    //TODO: handle message
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    String msg = String.valueOf(Integer.parseInt(message.split(":")[1]) + 1);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    send(Issue.ANSWER, msg);
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
                if (!listen) {
                    listen = true;
                    new Thread(run).start();
                }
            }

            void stopListening() {
                listen = false;
            }

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

    private abstract static class Issue {

        static final int CONNECTION = 0;
        static final int CONNECTION_REQUEST = 1;
        static final int CONNECTION_CONFIRM = 2;
        static final int PROCESS = 3;
        static final int ANSWER = 4;

    }

}

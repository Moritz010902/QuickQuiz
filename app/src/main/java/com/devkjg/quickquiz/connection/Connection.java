package com.devkjg.quickquiz.connection;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;
import com.devkjg.quickquiz.LobbyActivity;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;


/*
https://developer.android.com/training/data-storage/app-specific#java
https://www.programmerall.com/article/58251638107/
 */
public class Connection {

    private Context context;
    private Integer role;
    private InetAddress connectedAddress = null;
    private Integer connectedPort = null;
    private Client connectedClient = null;
    private Server server = null;
    protected final String multicastAddress = "230.100.221.1";
    protected final int multicastPort = 4336;
    protected final int port = 4346;


    public Connection(Context context) {
        this.context = context;
    }

    public void setRole(int role) {
        this.role = role;
    }

    private void checkRole(int requiredRole) {
        try {
            if(role == null)
                throw new UndefinedConnectionRoleException();
            if(role != requiredRole)
                throw new WrongConnectionRoleException();
        } catch (WrongConnectionRoleException | UndefinedConnectionRoleException e) {
            e.printStackTrace();
        }
    }

    public void broadcastGameInvitation(int gameId, long frequency) {
        checkRole(Role.SERVER);
        server = new Server();
        new MulticastServer(String.valueOf(gameId), frequency);
    }

    public void listenForGameInvitation(int gameId) {
        checkRole(Role.CLIENT);
        new MulticastClient(String.valueOf(gameId));
    }

    private void connectTo(int gameId, InetAddress address, int port) {
        checkRole(Role.CLIENT);
        connectedClient = new Client(gameId, address, port);
    }

    private void checkIsConnected() {
        try {
            if(connectedClient == null)
                throw new NoConnectionException();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }

    public void sendToHost(String issue, String message, RunOnComplete runOnComplete) {
        checkRole(Role.CLIENT);
        checkIsConnected();
        connectedClient.sendMessage(new Message(issue, message), runOnComplete);
    }


    private class Client {

        private final String logTag = "CLIENT";
        private DatagramSocket socket;
        private InetAddress hostAddress;
        private int hostPort;


        Client(int gameId, InetAddress hostAddress, int hostPort) {
            try{

                socket = new DatagramSocket(hostPort);
                this.hostAddress = hostAddress;
                this.hostPort = hostPort;
                Log.d(logTag, hostAddress.getHostAddress());
                // response to invitation
                sendMessage(new Message(Issue.CONNECTION_REQUEST, String.valueOf(gameId)), new RunOnComplete() {
                    @Override
                    public void run() {
                        assert Message.isValid(getResult());
                        if(Message.isIssue(getResult(), Issue.CONNECTION_CONFIRM) == Boolean.TRUE) {
                            if(Message.getContent(getResult()).equals(String.valueOf(gameId))) {
                                Intent intent = new Intent(context.getApplicationContext(), LobbyActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(Message message, RunOnComplete onComplete) {

            Runnable run = () -> {
                try {

                    String text = message.getText();
                    byte[] buf = text.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, hostAddress, hostPort);
                    socket.send(packet);
                    Log.i(logTag, "send: " + message.getText());

                    // get response
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    // display response
                    String received = new String(packet.getData(), 0, packet.getLength());
                    Log.i(logTag, "received: " + received);

                    socket.close();
                    if(onComplete != null) {
                        onComplete.setResult(received);
                        onComplete.run();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            new Thread(run).start();

        }

    }

    private class MulticastClient {

        private final String logTag = "MULTICAST_CLIENT";
        private MulticastSocket socket;
        private InetAddress address;
        private WifiManager.MulticastLock multicastLock;


        MulticastClient(String expectMessage) {
            try{

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                socket = new MulticastSocket(multicastPort);
                address = InetAddress.getByName(multicastAddress);
                socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                socket.joinGroup(address);

                //receive messages from multicast addresses
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                multicastLock = wifiManager.createMulticastLock("multicast.quickquiz");
                multicastLock.acquire();
                Log.i(logTag, "successfully created");

                DatagramPacket packet;
                byte[] buf = new byte[256];
                //TODO: implement timeout
                boolean listen = true;
                while (listen) {

                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    Log.i(logTag, "received: " + received);

                    if(!Message.isValid(received))
                        continue;
                    if(Message.getContent(received).equals(expectMessage)) {
                        connectTo(Integer.parseInt(expectMessage), packet.getAddress(), port);
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
                Log.i(logTag, "disabled");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class Server {

        private final String logTag = "SERVER";
        private ServerThread serverThread;

        Server() {
            try {
                serverThread = new ServerThread();
                serverThread.start();
                Log.i(logTag, "successfully started");
            } catch (IOException e) {
                e.printStackTrace();
            }
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        public void broadcast(Message message, RunOnComplete onComplete) {
            serverThread.broadcast(message, onComplete);
        }

        public void send(Client client, Message message, RunOnComplete onComplete) {
            serverThread.send(client, message, onComplete);
        }

    }

    private class MulticastServer {

        private final String logTag = "MULTICAST_SERVER";

        MulticastServer(String message, long timeout) {
            try {
                new MulticastServerThread(message, timeout).start();
                Log.i(logTag, "successfully started");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ServerThread extends Thread {

        private final String logTag = "SERVER";
        protected DatagramSocket socket;
        protected BufferedReader in;
        protected boolean moreQuotes = true;


        public ServerThread() throws IOException {
            this("ServerThread");
        }

        public ServerThread(String name) throws IOException {
            super(name);
            socket = new DatagramSocket(port);

            File file = new File(context.getFilesDir(), "clients.txt");
            if(!file.exists())
                if(!file.createNewFile())
                    Log.e(logTag, "failed to create registry file");
            FileInputStream fin = context.openFileInput(file.getName());
            in = new BufferedReader(new InputStreamReader(fin, StandardCharsets.UTF_8));

            Log.i(logTag, "successfully created");
        }

        public void run() {

            while (moreQuotes) {
                try {

                    byte[] buf = new byte[256];
                    Log.d(logTag, "listen");
                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    Log.i(logTag, "received: " + new String(packet.getData(), 0, packet.getLength()));

                    // figure out response
                    String response = getResponse(new String(packet.getData(), 0, packet.getLength())).getText();
                    buf = response.getBytes();

                    // send the response to the client at "address" and "port"
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                    Log.i(logTag, "respond: " + response);

                } catch (IOException e) {
                    e.printStackTrace();
                    moreQuotes = false;
                }
            }
            socket.close();
        }

        public void broadcast(Message message, RunOnComplete onComplete) {



        }

        public void send(Client client, Message message, RunOnComplete onComplete) {



        }

        protected Message getResponse(String message) {
            if(!Message.isValid(message))
                return null;

            try {
                if (Message.isIssue(message, Issue.CONNECTION_REQUEST) == Boolean.TRUE) {
                    //TODO: replace "1234" by variable gameId
                    if (Message.getContent(message).equals("1234"))
                        return new Message(Issue.CONNECTION_CONFIRM, "1234");
                }
            } catch (NullPointerException e) {
                return null;
            }
            return null;
        }

        /*
        protected String getResponse(String message) {
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
        */
    }

    private class MulticastServerThread extends Thread {

        private final String logTag = "MULTICAST_SERVER";
        protected long timeout;
        protected String message;
        protected boolean moreQuotes = true;

        public MulticastServerThread(String message, long timeout) throws IOException {
            super("MulticastServerThread");
            this.timeout = timeout;
            this.message = message;
            Log.i(logTag, "successfully created");
        }

        public void run() {
            try {
                InetAddress group = InetAddress.getByName(multicastAddress);
                MulticastSocket socket = new MulticastSocket(multicastPort);
                socket.setBroadcast(true);
                socket.joinGroup(group);

                while (moreQuotes) {

                    // construct quote
                    byte[] buf = new Message(Issue.BROADCAST, message).getText().getBytes();

                    // send it
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, multicastPort);
                    socket.send(packet);

                    Log.i(logTag, "send: " + message);

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

    private class UndefinedConnectionRoleException extends Exception {
        public UndefinedConnectionRoleException() {
            super("Please define your connections role first");
        }
    }

    private class WrongConnectionRoleException extends Exception {
        public WrongConnectionRoleException() {
            super("Your connection object is not permitted to call this method because it has the wrong role");
        }
    }

    private class NoConnectionException extends Exception {
        public NoConnectionException() {
            super("No existing connection between your and any other devices");
        }
    }

}

package com.example.fastfoodapplication;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler {

    private static final String IP_ADDRESS = "192.168.1.103";
    private static final int PORT = 8000;
    public static ServerHandler instance = new ServerHandler();
    private final static String LOG_TAG = "SERVER_HANDLER_INSTANCE";
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private ServerHandler() {
    }

    private void checkConnection() throws Exception {
//        if (isConnected()) {
//            return;
//        }

        connect();
    }

    public void connect() throws Exception {
        Log.d(LOG_TAG, "Socket trying to connect...");
        socket = new Socket();
        socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), 1000); // TODO: Change IP_ADDRESS if needed
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Log.d(LOG_TAG, "Socket connected");
    }

    public void startRace() throws Exception {
        checkConnection();
        output.writeByte(0);
        output.flush();
    }

    public void sendLap(Map.Entry<String, LocalTime> lapTime) throws Exception {
        checkConnection();
        output.writeObject(lapTime);
        output.flush();
    }

    public void waitForStart() throws Exception {
        checkConnection();
        input.readBoolean();
    }

    public List<Map.Entry<String, LocalTime>> getResults() throws Exception {
        checkConnection();
        return (List<Map.Entry<String, LocalTime>>) input.readObject();
    }

    public Set<Map.Entry<String, LocalTime>> requestLeaderboard() throws Exception {
        checkConnection();
        Log.d(LOG_TAG, "got past connection check in requestleaderboard");
        output.writeByte(1);
        output.flush();
        return (Set<Map.Entry<String, LocalTime>>) input.readObject();
    }

    private void cleanup() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (isConnected()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
}


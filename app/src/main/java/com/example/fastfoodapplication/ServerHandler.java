package com.example.fastfoodapplication;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 55000;
    public static ServerHandler instance = new ServerHandler();
    private final static String LOG_TAG = "SERVER_HANDLER_INSTANCE";
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean racing = false;

    private ServerHandler() {
    }

    private void checkConnection() throws IOException {
//        if (isConnected()) {
//            return;
//        }

        connect();
    }

    public void connect() throws IOException {
        Log.d(LOG_TAG, "Socket trying to connect...");
        socket = new Socket(IP_ADDRESS, PORT); // TODO: Change IP_ADDRESS if needed
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Log.d(LOG_TAG, "Socket connected");
    }

    public void startRace() throws Exception {
        checkConnection();
        output.writeByte(0);
        output.flush();
        output.reset();
        racing = true;
    }

    public void sendLap(Map.Entry<String, LocalTime> lapTime) throws Exception {
        checkConnection();
        output.writeObject(lapTime);
        output.flush();
        output.reset();
    }

    public void waitForStart() throws Exception {
        checkConnection();
        input.readBoolean();
    }

    public List<Map.Entry<String, LocalTime>> getResults() throws Exception {
        checkConnection();
        return (List<Map.Entry<String, LocalTime>>) input.readObject();
    }

    public Set<Map.Entry<String, LocalTime>> requestLeaderboard() throws IOException, ClassNotFoundException {
        checkConnection();
        Log.d(LOG_TAG, "got past connection check in requestleaderboard");
        output.writeByte(1);
        output.flush();
        output.reset();
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

    public void resetConnection() {
        cleanup();
        racing = false;
    }

    private boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public boolean isRacing() {
        return racing;
    }
}


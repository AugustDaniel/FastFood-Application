package com.example.fastfoodapplication;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import com.fastfoodlib.util.*;

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

    public void connect() throws Exception {
        Log.d(LOG_TAG, "Socket trying to connect...");
        socket = new Socket();
        socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), 1000); // TODO: Change IP_ADDRESS if needed
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Log.d(LOG_TAG, "Socket connected");
    }

    public void startRace() throws Exception {
        try {
            output.writeByte(0);
            output.flush();
        } catch (Exception e) {
            connect();
            startRace();
        }
    }

    public void sendLap(Lap lap) throws Exception {
        Log.d(LOG_TAG, "sending lap");
        output.writeObject(lap);
        output.flush();
    }

    public void waitForStart() throws Exception {
        Log.d(LOG_TAG, "waiting for start");
        input.readBoolean();
    }

    public List<Lap> getResults() throws Exception {
        return (List<Lap>) input.readObject();
    }

    public Set<Lap> requestLeaderboard() throws Exception {
        Log.d(LOG_TAG, "requesting leaderboard");
        try {
            output.writeByte(1);
            output.flush();
        } catch (Exception e) {
            connect();
            requestLeaderboard();
        }
        return (Set<Lap>) input.readObject();
    }
}


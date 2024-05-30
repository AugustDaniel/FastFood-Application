package com.example.fastfoodapplication;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import com.fastfoodlib.util.*;

public class ServerHandler {

    private static final String IP_ADDRESS = "145.49.11.211";
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

    public void joinRace() throws Exception {
        try {
            checkConnection();
            output.writeObject(Options.JOIN_RACE);
            output.flush();
        } catch (Exception e) {
            connect();
            joinRace();
        }
    }

    public void sendLap(Lap lap) throws Exception {
        Log.d(LOG_TAG, "sending lap");
        output.writeObject(lap);
    }

    public void waitForStart() throws Exception {
        Log.d(LOG_TAG, "waiting for start");
        while (true) {
            input.readObject();
            output.writeObject(Options.START_RACE);
            output.flush();

            boolean start = input.readBoolean();

            if (start) {
                break;
            }
        }
    }

    public List<Lap> getResults() throws Exception {
        return (List<Lap>) input.readObject();
    }

    public List<Lap> requestLeaderboard() throws Exception {
        Log.d(LOG_TAG, "requesting leaderboard");
        try {
            checkConnection();
            output.writeObject(Options.REQUEST_LEADERBOARD);
            output.flush();
        } catch (Exception e) {
            connect();
            requestLeaderboard();
            e.printStackTrace();
        }

        List<Lap> laps = (List<Lap>) input.readObject();

        Log.d(LOG_TAG, laps.toString());
        return laps;
    }

    public void checkConnection() throws Exception{
        if (socket == null || input == null || output == null) connect();
    }
}


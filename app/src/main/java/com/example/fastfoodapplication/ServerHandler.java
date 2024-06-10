package com.example.fastfoodapplication;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import com.fastfoodlib.util.*;

public class ServerHandler {

    private static final String IP_ADDRESS = "145.49.39.222";
    private static final int PORT = 8000;
    private final static String LOG_TAG = "SERVER_HANDLER";
    private static Socket socket;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;
    private static int lapsInRace = 0;
    private static int lapCounter = 0;

    private ServerHandler() {
    }

    public static void connect() throws IOException {
        Log.d(LOG_TAG, "Socket trying to connect...");
        socket = new Socket();
        socket.connect(new InetSocketAddress(IP_ADDRESS, PORT), 1000); // TODO: Change IP_ADDRESS if needed
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Log.d(LOG_TAG, "Socket connected");
    }

    public static void disconnect() {
        Log.d(LOG_TAG, "Disconnecting socket");
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            input = null;
            output = null;
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkNullPointers() throws IOException {
        if (socket == null || input == null || output == null) connect();
    }

    private static void writeObject(Object o) throws IOException {
        checkNullPointers();
        output.writeObject(o);
        output.flush();
    }

    private static Object readObject() throws IOException, ClassNotFoundException {
        checkNullPointers();
        return input.readObject();
    }

    public static void joinRace() throws IOException {
        writeObject(Options.JOIN_RACE);
    }

    public static boolean sendLap(Lap lap) throws IOException {
        Log.d(LOG_TAG, "sending lap");
        writeObject(lap);
        lapCounter++;

        if (lapCounter == lapsInRace) {
            lapCounter = 0;
            return true;
        }

        return false;
    }

    public static void waitForStart() throws IOException, ClassNotFoundException {
        Log.d(LOG_TAG, "waiting for start");
        while (true) {
            readObject();
            writeObject(Options.START_RACE);

            boolean start = input.readBoolean();

            if (start) {
                lapsInRace = input.readInt();
                break;
            }
        }
    }

    public static List<Lap> getResults() throws IOException, ClassNotFoundException {
        return (List<Lap>) readObject();
    }

    public static List<Lap> requestLeaderboard() throws IOException, ClassNotFoundException {
        Log.d(LOG_TAG, "requesting leaderboard");
        writeObject(Options.REQUEST_LEADERBOARD);
        return (List<Lap>) readObject();
    }
}
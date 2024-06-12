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

    private final static String logTag = ServerHandler.class.getName();
    private static final String ipAddress = "145.49.39.222";
    private static final int port = 8000;
    private static Socket socket;
    private static ObjectInputStream input;
    private static ObjectOutputStream output;

    private ServerHandler() {
    }

    public static void connect() throws IOException {
        Log.d(logTag, "Socket trying to connect...");
        socket = new Socket();
        socket.connect(new InetSocketAddress(ipAddress, port), 1000); //Change ipAddress if needed
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Log.d(logTag, "Socket connected");
    }

    public static void disconnect() {
        Log.d(logTag, "Disconnecting socket");
        try {
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

    public static void sendLap(Lap lap) throws IOException {
        Log.d(logTag, "sending lap");
        writeObject(lap);
    }

    public static void waitForStart() throws IOException, ClassNotFoundException {
        Log.d(logTag, "waiting for start");
        while (true) {
            readObject();
            writeObject(Options.START_RACE);

            boolean start = input.readBoolean();

            if (start) {
                break;
            }
        }
    }

    public static boolean waitForTimeOut() throws IOException {
        Log.d(logTag, "waiting for timeout");
        return input.readBoolean();
    }

    public static List<Lap> getResults() throws IOException, ClassNotFoundException {
        return (List<Lap>) readObject();
    }

    public static List<Lap> requestLeaderboard() throws IOException, ClassNotFoundException {
        Log.d(logTag, "requesting leaderboard");
        writeObject(Options.REQUEST_LEADERBOARD);
        return (List<Lap>) readObject();
    }
}
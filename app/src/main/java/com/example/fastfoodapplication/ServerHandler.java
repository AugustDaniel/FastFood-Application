package com.example.fastfoodapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerHandler {

    public static ServerHandler instance = new ServerHandler();
    private final static String LOG_TAG = "SERVER_HANDLER_INSTANCE";
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private Future<?> thread;
    private boolean racing = false;

    private ServerHandler() {
    }

    public void startConnectService(Context context) {
        if (thread != null && !thread.isDone()) {
            return;
        }

        thread = threadPool.submit(() -> {
            while (true) {
                try {
                    socket = new Socket("localhost", 8000); //TODO might have to change localhost
                    output = new ObjectOutputStream(socket.getOutputStream());
                    input = new ObjectInputStream(socket.getInputStream());

                    Log.d(LOG_TAG, "socket connected");

                    while (socket.isConnected()) {
                        // keep waiting while socket is connected
                    }

                    Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show(); //TODO string res
                } catch (Exception e) {
                    //TODO more robust error handling
                    e.printStackTrace();
                    cleanup();
                }
            }
        });
    }

    public Set<Map.Entry<String, LocalTime>> requestRace() {

        return null;
    }

    public Set<Map.Entry<String, LocalTime>> requestLeaderboard() {
        if (!isConnected() || racing) return null;

        Set<Map.Entry<String, LocalTime>> leaderboard = new LinkedHashSet<>();
        try {
            output.writeByte(0);
            output.flush();
            output.reset();
            leaderboard = (Set<Map.Entry<String, LocalTime>>) input.readObject();
            input.reset();
        } catch (Exception e) {
            //TODO more robust error handling
            e.printStackTrace();
            cleanup();
        }

        return leaderboard;
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

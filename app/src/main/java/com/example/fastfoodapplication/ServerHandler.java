package com.example.fastfoodapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
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

    private static String IP_ADDRESS = "145.49.14.188";
    private static int PORT = 55000;
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
                    Log.d(LOG_TAG, "Socket trying to connect...");
                    socket = new Socket(IP_ADDRESS, PORT); // TODO: Change IP_ADDRESS if needed
                    output = new ObjectOutputStream(socket.getOutputStream());
                    input = new ObjectInputStream(socket.getInputStream());

                    showToast(context, "Connected");
                    Log.d(LOG_TAG, "Socket connected");

                    int port = socket.getPort();
                    while (socket.getPort() == port) {
                        Thread.sleep(1000);
                    }

                    Log.d(LOG_TAG, "Socket disconnected");
                    showToast(context, "Disconnected");
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Socket connection error", e);
                    showToast(context, "Connection error");
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Thread interrupted", e);
                    Thread.currentThread().interrupt();
                } finally {
                    cleanup();
                }
            }
        });
    }

    private void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        );
    }

    public Set<Map.Entry<String, LocalTime>> requestRace() {

        return null;
    }

    public Set<Map.Entry<String, LocalTime>> requestLeaderboard() {
        if (!isConnected() || racing) return null;

        Log.d(LOG_TAG, "got past connction check in requestleaderboard");

        Set<Map.Entry<String, LocalTime>> leaderboard = new LinkedHashSet<>();
        try {
            output.writeByte(0);
            output.flush();
            output.reset();
            leaderboard = (Set<Map.Entry<String, LocalTime>>) input.readObject();
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

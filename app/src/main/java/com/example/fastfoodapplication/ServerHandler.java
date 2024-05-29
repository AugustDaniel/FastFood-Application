package com.example.fastfoodapplication;

import static android.app.PendingIntent.getActivity;

import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerHandler {

    public ServerHandler instance = new ServerHandler();

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private Future<?> thread;

    private ServerHandler() {
    }

    public void startConnectService() {
        if (!thread.isDone()) {
            return;
        }

        thread = threadPool.submit(() -> {
            while (true) {
                try {
                    socket = new Socket("localhost", 8000); //TODO might have to change localhost
                    input = new ObjectInputStream(socket.getInputStream());
                    output = new ObjectOutputStream(socket.getOutputStream());

                    while (socket.isConnected()) {
                        // keep waiting while socket is connected
                    }
                } catch (Exception e) {
                    //TODO more robust errorhandling

                    e.printStackTrace();
                    cleanup();
                }
            }
        });
    }



    private void cleanup() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

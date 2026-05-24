package org.lib;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class InstanceManager {

    private static final String LOCK_FILE = System.getProperty("java.io.tmpdir") + "/ContactManager.lock";
    private static final int PORT = 49205;
    private Stage stage;

    public InstanceManager(Stage stage) {
        this.stage = stage;
    }

    public InstanceManager() {

    }

    public boolean isAlreadyRunning() {
        File lock = new File(LOCK_FILE);

        if (lock.exists()) {
            try (Socket s = new Socket("localhost", PORT)) {
                PrintWriter out = new PrintWriter(s.getOutputStream());
                out.println("SHOW");
                out.close();
                s.close();
                return true;
            } catch (IOException e) {
                System.out.println("Stale lock found. Deleting...");
                lock.delete();
                return false;
            }
        }

        return false;
    }
    public void startListener() {
        // Create the lock file
        File lock = new File(LOCK_FILE);
        try {
            lock.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create lock file.");
        }
        System.out.println(lock.getPath());

        // Delete it on exit
        Runtime.getRuntime().addShutdownHook(new Thread(lock::delete));

        // Start listening on a background thread
        Thread listener = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                while (true) {
                    try (Socket client = server.accept()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String message = in.readLine();
                        if (message.equals("SHOW")) {
                            System.out.println("Another instance was run. Focusing window now.");
                            Platform.runLater(() -> {
                                stage.toFront();
                                stage.requestFocus();
                            });
                        }
                        in.close();
                    }
                }
            } catch (IOException e) {
                System.err.println("Listener error: " + e.getMessage());
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

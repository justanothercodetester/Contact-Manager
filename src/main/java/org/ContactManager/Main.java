package org.ContactManager;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.awt.Image;
import javafx.stage.Stage;
import org.lib.BuildInformation;
import org.lib.InstanceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static InstanceManager im;
    public static MainController controller;
    public static Stage window;

    @Override
    public void start(Stage stage) throws Exception {

        im.setStage(stage);

        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.setTitle("Contact Manager " + BuildInformation.get("app.version"));
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("/contact icon.png"))));
        stage.show();

        setupTrayIcon(stage);
        window = stage;
    }

    public void setupTrayIcon(Stage stage) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        Platform.setImplicitExit(false);

        SystemTray tray = SystemTray.getSystemTray();

        Image image;

        try {
            image = ImageIO.read(getClass().getResource("/contact icon.png"));
        } catch (IOException ex) {
            System.out.println("Unable to read contact icon image");
            System.out.println(ex.getMessage());
            Platform.setImplicitExit(true);
            return;
        }

        PopupMenu popup = new PopupMenu();

        MenuItem showItem = new MenuItem("Show");
        showItem.addActionListener(e -> Platform.runLater(stage::show));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> Platform.runLater(() -> {
            stage.show();
            controller.exit();
        }));

        popup.add(showItem);
        popup.addSeparator();
        popup.add(exitItem);

        TrayIcon trayIcon = new TrayIcon(image, "My App", popup);
        trayIcon.setImageAutoSize(true);

        // Double-click to restore
        trayIcon.addActionListener(e -> Platform.runLater(stage::show));

        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            System.out.println("Unable to add tray icon");
            System.out.println(ex.getMessage());
            Platform.setImplicitExit(true);
            return;
        }

        stage.setOnCloseRequest(event -> {
            event.consume();  // cancels the close
            stage.hide();
        });
    }

    public static void main(String[] args) {
        im = new InstanceManager();
        if (im.isAlreadyRunning()) {
            System.exit(0);
        }
        im.startListener();
        launch(args);
    }
}
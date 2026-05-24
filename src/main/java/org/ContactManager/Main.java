package org.ContactManager;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lib.BuildInformation;
import org.lib.InstanceManager;

import java.util.Objects;

public class Main extends Application {

    /*
    * TODO:
    *  * 1. Make toString method in Contact class also return first letter of middle name between first and last name
    *  * 2. Make it so that information is show on the right panel with a copy button next to each information
    *  * 3. Add a dark mode
    *  4. Add search functionality
    *  5. Add all dream features from there
    * */

    private static InstanceManager im;
    public static Stage window;

    @Override
    public void start(Stage stage) throws Exception {

        im.setStage(stage);

        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.setTitle("Contact Manager " + BuildInformation.get("app.version"));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/contact icon.png"))));
        stage.show();
        window = stage;
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
package org.ContactManager;

import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.lib.BuildInformation;

public class AboutController {

    @FXML
    private TextArea textArea;

    @FXML
    private void initialize() {
        textArea.setText("Product version\n"
                + BuildInformation.get("app.name") + ' '
                + BuildInformation.get("app.version") + "\n\n"
                + "Build information\n"
                + "Version "
                + BuildInformation.get("app.version") + "\n"
                + "Date: " + BuildInformation.get("build.date") + "\n"
                + "JavaFX version: " + BuildInformation.get("javafx.version") + "\n"
                + "Java version: " + BuildInformation.get("jre.version") + "\n\n"
                + "Operating System\n"
                + System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ", " + System.getProperty("os.version")
                + "\n\nCopyright (c) 2026 " + BuildInformation.get("app.developer") + "\n"
                + "All rights reserved. Use is subject to license terms."
                + "\n\nThis file is available and licensed under the following license:\n\n"
                + '\n' + BuildInformation.getLicense()
        );
        textArea.setEditable(false);
    }

    public void closeWindow() {
        ((Stage) textArea.getScene().getWindow()).close();
    }

}

package org.ContactManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lib.BuildInformation;
import org.lib.Contact;

public class MainController {
    //Right pane stuff
    public Label nameLabel;
    public Label companyLabel;
    public Label phoneLabel;
    public Label emailLabel;
    public Label addressLabel;
    public Label birthdayLabel;
    public Label notesLabel;

    @FXML
    private Label noSelectionLabel;

    @FXML
    public VBox contactDetailsPane;
    //End of right pane stuff

    public Button editContactButton;
    public MenuItem editContactMenu;
    public Button deleteContactButton;
    public MenuItem deleteContactMenu;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Contact> contactListView;
    @FXML
    private BorderPane root;

    @FXML
    public void initialize() {
        // Add listener for when an item is selected/focused
        contactListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listFocused();
            }
        });

        // Add click handler to deselect when clicking outside the list
        root.setOnMousePressed(event -> {
            if (!event.getTarget().toString().contains("ListView")) {
                contactListView.getSelectionModel().clearSelection();
                listUnfocused();
            }
        });
    }

    public void addContactButton() {
        AddContactDialogue.add(contactListView);
    }

    public void editContactButton() {
        AddContactDialogue.edit(getFocusedContact());
        listFocused();
        contactListView.refresh();
    }

    public void deleteContactButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you wish to delete " + getFocusedContact() + "'s contact?");
        alert.getButtonTypes().set(0, ButtonType.YES);
        alert.getButtonTypes().add(1, ButtonType.NO);
        alert.getButtonTypes().remove(2);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES){
            contactListView.getItems().remove(getFocusedContact());
            noSelectionLabel.setVisible(true);
            contactDetailsPane.setVisible(false);
            listUnfocused();
        }
    }

    public void listFocused() {
        editContactButton.setDisable(false);
        editContactMenu.setDisable(false);
        deleteContactButton.setDisable(false);
        deleteContactMenu.setDisable(false);
        noSelectionLabel.setVisible(false);
        contactDetailsPane.setVisible(true);

        //Display details about contact
        Contact c = getFocusedContact();
        nameLabel.setText(c.toString());
        companyLabel.setText(c.company);
        phoneLabel.setText(c.getPhone());
        emailLabel.setText(c.getEmail());
        addressLabel.setText(c.getAddress());
        birthdayLabel.setText(c.getBirthday());
        notesLabel.setText(c.notes);
    }

    public void listUnfocused(){
        editContactButton.setDisable(true);
        editContactMenu.setDisable(true);
        deleteContactButton.setDisable(true);
        deleteContactMenu.setDisable(true);
    }

    public Contact getFocusedContact() {
        return contactListView.getSelectionModel().getSelectedItem();
    }

    public void copyCompany(ActionEvent event) {
    copyToClipboard(companyLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

public void copyPhone(ActionEvent event) {
    copyToClipboard(phoneLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

public void copyEmail(ActionEvent event) {
    copyToClipboard(emailLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

public void copyAddress(ActionEvent event) {
    copyToClipboard(addressLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

public void copyBirthday(ActionEvent event) {
    copyToClipboard(birthdayLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

public void copyNotes(ActionEvent event) {
    copyToClipboard(notesLabel.getText());
    
    if (event.getSource() instanceof Button) {
        Button source = (Button) event.getSource();
        source.setDisable(true);
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
            source.setDisable(false);
        });
        pause.play();
    }
}

    public static void copyToClipboard(String text) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(text);
        cb.setContents(data, null);
    }

    public void darkMode(ActionEvent event) {
        RadioMenuItem source = (RadioMenuItem) event.getSource();
        if (source.isSelected())
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        else
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
    }

    public void showInfo() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("About " + BuildInformation.get("app.name"));
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/contact icon.png"))));
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/about.fxml"));

        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FXML Load Error");
            alert.setHeaderText("Unable to load about.fxml");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void close() {
        Main.window.close();
    }

}

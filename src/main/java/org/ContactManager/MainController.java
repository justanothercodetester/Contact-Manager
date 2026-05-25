package org.ContactManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import org.lib.AES;
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

    public MenuItem exportContactMenuItem;

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

    public void exportContact() {

        String password = "";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("");
        alert.setTitle("Encryption");
        alert.setContentText("Would you like to turn on password protection for this contact?\n" +
                "Only use this if the contact you are exporting contains sensitive data");
        alert.getButtonTypes().set(0, ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == ButtonType.CANCEL)
            return;

        if (result.get() == ButtonType.YES) {
            AtomicBoolean isCancelled = new AtomicBoolean(false);

            Stage stage = new Stage();

            VBox root = new VBox();

            Label instructions = new Label("Enter a password:");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("abc123");
            Label instructions2 = new Label("Confirm password:");
            PasswordField confirmField = new PasswordField();
            confirmField.setPromptText("Enter password again");
            Button confirm = new Button("Submit password");
            confirm.setOnAction(e -> {
                if (checkPassword(passwordField, confirmField))
                    stage.close();
            });
            confirmField.setOnAction(e -> {
                if (checkPassword(passwordField, confirmField))
                    stage.close();
            });

            root.getChildren().addAll(instructions, passwordField, instructions2, confirmField, confirm);
            root.setSpacing(5);
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root, 300, 200);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Password");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(e -> {
                isCancelled.set(true);
            });
            stage.showAndWait();

            if (isCancelled.get())
                return;

            password = passwordField.getText();
        }

        Contact c = getFocusedContact();

        JSONObject contact = new JSONObject();
        contact.put("First Name", c.firstName);
        contact.put("Middle Name", c.middleName);
        contact.put("Last Name", c.lastName);
        contact.put("Company", c.company);
        contact.put("Phone", c.getPhone());
        contact.put("Email", c.getEmail());
        contact.put("Address", c.getAddress());
        contact.put("Birthday", c.getBirthday());
        contact.put("Notes", c.notes);

        String content = contact.toString();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Contact");
        chooser.setInitialFileName("example");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Contact File", "*.JContact"));
        File file = chooser.showSaveDialog(Main.window);
        if (file == null)
            return;

        if (!password.isEmpty())
            content = AES.encrypt(content, password);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (FileNotFoundException e) {
            showError("Unable to locate file", "Error locating file, please try again later.");
        } catch (IOException e) {
            showError("Error writing to file", "Error writing to file.\n" + e.getMessage());
        }
    }

    public boolean checkPassword(PasswordField passwordField, PasswordField confirmField) {
        if (passwordField.getText().equals(confirmField.getText()))
            return true;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Passwords do not match");
        alert.setHeaderText("");
        alert.setContentText("The passwords you entered do not match.");
        alert.show();

        return false;
    }

    public void importContact() {
        String content = "";
        String password = "";
        boolean requiresPassword = false;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Contact");
        chooser.setInitialFileName("example");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Contact File", "*.JContact"));
        File file = chooser.showOpenDialog(Main.window);
        if (file == null)
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();

            String str;

            while ((str = reader.readLine()) != null)
                builder.append(str).append("\n");

            content = builder.toString();

        } catch (FileNotFoundException e) {
            showError("File not found", "File not found. Please try again later.");
        } catch (IOException e) {
            showError("Error reading file", "Error reading file.\n" + e.getMessage());
        }

        if (content.isEmpty())
            return;

        if (!content.contains(":")) {
            AtomicBoolean isCancelled = new AtomicBoolean(false);

            requiresPassword = true;

            Stage stage = new Stage();

            VBox root = new VBox();

            Label instructions = new Label("Please enter a password:");
            PasswordField field = new PasswordField();
            field.setPromptText("abc123");
            field.setOnAction(e -> stage.close());
            Button confirm = new Button("Submit password");
            confirm.setOnAction(e -> stage.close());

            root.getChildren().addAll(instructions, field, confirm);
            root.setSpacing(5);
            root.setAlignment(Pos.CENTER);

            Scene scene = new Scene(root, 300, 100);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Password Required");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(e -> {
                isCancelled.set(true);
            });
            stage.showAndWait();

            if (isCancelled.get())
                return;

            password = field.getText();
        }

        if (requiresPassword)
            content = AES.decrypt(content, password);

        if (content.startsWith("Error")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong password");
            alert.setHeaderText("");
            alert.setContentText("The password provided was wrong. Please try again later");
            alert.show();
            return;
        }

        JSONObject object = new JSONObject(content);

        Contact c = new Contact((String) object.get("First Name"), (String) object.get("Middle Name"), (String) object.get("Last Name"), (String) object.get("Company"));
        c.setPhone((String) object.get("Phone"));
        c.setEmail((String) object.get("Email"));
        c.setAddress((String) object.get("Address"));
        c.setBirthday((String) object.get("Birthday"));
        c.notes = (String) object.get("Notes");

        contactListView.getItems().add(c);
        //contactListView.refresh();
    }

    public void listFocused() {
        editContactButton.setDisable(false);
        editContactMenu.setDisable(false);
        deleteContactButton.setDisable(false);
        deleteContactMenu.setDisable(false);
        exportContactMenuItem.setDisable(false);
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
        exportContactMenuItem.setDisable(true);
    }

    public Contact getFocusedContact() {
        return contactListView.getSelectionModel().getSelectedItem();
    }

    public void copyCompany(ActionEvent event) {
        copyToClipboard(companyLabel.getText());
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

    public void copyPhone(ActionEvent event) {
        copyToClipboard(phoneLabel.getText());
        Button source = (Button) event.getSource();
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
        });
        pause.play();
    }

    public void copyEmail(ActionEvent event) {
        copyToClipboard(emailLabel.getText());
        Button source = (Button) event.getSource();
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
        });
        pause.play();
    }

    public void copyAddress(ActionEvent event) {
        copyToClipboard(addressLabel.getText());
        Button source = (Button) event.getSource();
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
        });
        pause.play();
    }

    public void copyBirthday(ActionEvent event) {
        copyToClipboard(birthdayLabel.getText());
        Button source = (Button) event.getSource();
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
        });
        pause.play();
    }

    public void copyNotes(ActionEvent event) {
        copyToClipboard(notesLabel.getText());
        Button source = (Button) event.getSource();
        source.setText("Copied...");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            source.setText("Copy");
        });
        pause.play();
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

    public void showError(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(text);
        alert.showAndWait();
    }
}

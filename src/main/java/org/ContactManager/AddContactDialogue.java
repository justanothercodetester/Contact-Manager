package org.ContactManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.lib.Contact;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class AddContactDialogue {

    public static void add(ListView<Contact> contactListView) {
        Stage stage = new Stage();

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(15, 20, 15, 20));

        //First name
        Label firstNameLabel = new Label("*First name:");
        firstNameLabel.setMinWidth(100);
        TextField firstNameTextBox = new TextField();
        firstNameTextBox.setPromptText("eg. John");
        HBox firstName = new HBox(firstNameLabel, firstNameTextBox);
        firstName.setAlignment(Pos.CENTER_LEFT);
        firstName.setSpacing(10);
        HBox.setHgrow(firstNameTextBox, Priority.ALWAYS);
        root.getChildren().add(firstName);

        //Middle name
        Label middleNameLabel = new Label("Middle name:");
        middleNameLabel.setMinWidth(100);
        TextField middleNameTextBox = new TextField();
        middleNameTextBox.setPromptText("eg. James");
        HBox middleName = new HBox(middleNameLabel, middleNameTextBox);
        middleName.setAlignment(Pos.CENTER_LEFT);
        middleName.setSpacing(10);
        HBox.setHgrow(middleNameTextBox, Priority.ALWAYS);
        root.getChildren().add(middleName);

        //Last name
        Label lastNameLabel = new Label("Last name:");
        lastNameLabel.setMinWidth(100);
        TextField lastNameTextBox = new TextField();
        lastNameTextBox.setPromptText("eg. Doe");
        HBox lastName = new HBox(lastNameLabel, lastNameTextBox);
        lastName.setAlignment(Pos.CENTER_LEFT);
        lastName.setSpacing(10);
        HBox.setHgrow(lastNameTextBox, Priority.ALWAYS);
        root.getChildren().add(lastName);

        //Company
        Label companyLabel = new Label("Company:");
        companyLabel.setMinWidth(100);
        TextField companyTextBox = new TextField();
        companyTextBox.setPromptText("eg. ABC inc.");
        HBox company = new HBox(companyLabel, companyTextBox);
        company.setAlignment(Pos.CENTER_LEFT);
        company.setSpacing(10);
        HBox.setHgrow(companyTextBox, Priority.ALWAYS);
        root.getChildren().add(company);

        //Phone number
        Label phoneLabel = new Label("Phone number:");
        phoneLabel.setMinWidth(100);
        TextField phoneTextBox = new TextField();
        phoneTextBox.setPromptText("eg. 5551234567");
        // Restrict input to numbers only
        phoneTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneTextBox.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        HBox phone = new HBox(phoneLabel, phoneTextBox);
        phone.setAlignment(Pos.CENTER_LEFT);
        phone.setSpacing(10);
        HBox.setHgrow(phoneTextBox, Priority.ALWAYS);
        root.getChildren().add(phone);

        //Email
        Label emailLabel = new Label("Email:");
        emailLabel.setMinWidth(100);
        TextField emailTextBox = new TextField();
        emailTextBox.setPromptText("eg. john.doe@example.com");
        HBox email = new HBox(emailLabel, emailTextBox);
        email.setAlignment(Pos.CENTER_LEFT);
        email.setSpacing(10);
        HBox.setHgrow(emailTextBox, Priority.ALWAYS);
        root.getChildren().add(email);

        //Address
        Label addressLabel = new Label("Address:");
        addressLabel.setMinWidth(100);
        TextField addressTextBox = new TextField();
        addressTextBox.setPromptText("eg. 123 Main St, City, State ZIP");
        HBox address = new HBox(addressLabel, addressTextBox);
        address.setAlignment(Pos.CENTER_LEFT);
        address.setSpacing(10);
        HBox.setHgrow(addressTextBox, Priority.ALWAYS);
        root.getChildren().add(address);

        //Birthday
        Label birthdayLabel = new Label("Birthday:");
        birthdayLabel.setMinWidth(100);
        TextField birthdayTextBox = new TextField();
        birthdayTextBox.setPromptText("eg. MM/DD/YYYY");
        // Restrict input to numbers and slashes, format as date
        birthdayTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9/]*")) {
                birthdayTextBox.setText(newValue.replaceAll("[^0-9/]", ""));
            }
        });
        HBox birthday = new HBox(birthdayLabel, birthdayTextBox);
        birthday.setAlignment(Pos.CENTER_LEFT);
        birthday.setSpacing(10);
        HBox.setHgrow(birthdayTextBox, Priority.ALWAYS);
        root.getChildren().add(birthday);

        //Notes
        Label notesLabel = new Label("Notes:");
        notesLabel.setMinWidth(100);
        TextArea notesTextArea = new TextArea();
        notesTextArea.setPromptText("Additional notes...");
        notesTextArea.setPrefRowCount(3);
        notesTextArea.setWrapText(true);
        HBox notes = new HBox(notesLabel, notesTextArea);
        notes.setAlignment(Pos.TOP_LEFT);
        notes.setSpacing(10);
        HBox.setHgrow(notesTextArea, Priority.ALWAYS);
        root.getChildren().add(notes);

        //Warning
        Label warning = new Label("* indicates a required field");
        warning.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
        root.getChildren().add(warning);

        //Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        root.getChildren().add(spacer);

        //Buttons
        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(80);
        saveButton.setOnAction(e -> {
            //Check required first name field
            if (firstNameTextBox.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Field empty");
                alert.setHeaderText(null);
                alert.setContentText("Please type a name into the first name field.");
                alert.showAndWait();
                return;
            }

            // Validate email if not empty
            String emailText = emailTextBox.getText().trim();
            if (!emailText.isEmpty()) {
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                if (!Pattern.matches(emailRegex, emailText)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Email");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a valid email address.");
                    alert.showAndWait();
                    return;
                }
            }

            // Validate birthday if not empty
            String birthdayText = birthdayTextBox.getText().trim();
            if (!birthdayText.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate.parse(birthdayText, formatter);
                } catch (DateTimeParseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a valid date in MM/DD/YYYY format.");
                    alert.showAndWait();
                    return;
                }
            }

            for (int i = 0; i < contactListView.getItems().toArray().length; i++) {
                Contact c = contactListView.getItems().get(i);
                if (firstNameTextBox.getText().equals(c.firstName) &&
                middleNameTextBox.getText().equals(c.middleName) &&
                lastNameTextBox.getText().equals(c.lastName)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Name already exists");
                    alert.setHeaderText(null);
                    alert.setContentText("Another contact with the exact name already exists.");
                    alert.showAndWait();
                    return;
                }
            }

            Contact contact = new Contact(firstNameTextBox.getText(), middleNameTextBox.getText(), lastNameTextBox.getText(), companyTextBox.getText());
            contact.setPhone(phoneTextBox.getText());
            contact.setEmail(emailTextBox.getText());
            contact.setAddress(addressTextBox.getText());
            if (!birthdayTextBox.getText().isEmpty()) {
                contact.setBirthday(birthdayTextBox.getText());
            }
            contact.notes = notesTextArea.getText();

            contactListView.getItems().add(contact);

            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root, 500, 550);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Add contact dialogue");
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void edit(Contact contact) {
        Stage stage = new Stage();

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(15, 20, 15, 20));

        //First name
        Label firstNameLabel = new Label("*First name:");
        firstNameLabel.setMinWidth(100);
        TextField firstNameTextBox = new TextField(contact.firstName);
        firstNameTextBox.setPromptText("eg. John");
        HBox firstName = new HBox(firstNameLabel, firstNameTextBox);
        firstName.setAlignment(Pos.CENTER_LEFT);
        firstName.setSpacing(10);
        HBox.setHgrow(firstNameTextBox, Priority.ALWAYS);
        root.getChildren().add(firstName);

        //Middle name
        Label middleNameLabel = new Label("Middle name:");
        middleNameLabel.setMinWidth(100);
        TextField middleNameTextBox = new TextField(contact.middleName);
        middleNameTextBox.setPromptText("eg. James");
        HBox middleName = new HBox(middleNameLabel, middleNameTextBox);
        middleName.setAlignment(Pos.CENTER_LEFT);
        middleName.setSpacing(10);
        HBox.setHgrow(middleNameTextBox, Priority.ALWAYS);
        root.getChildren().add(middleName);

        //Last name
        Label lastNameLabel = new Label("Last name:");
        lastNameLabel.setMinWidth(100);
        TextField lastNameTextBox = new TextField(contact.lastName);
        lastNameTextBox.setPromptText("eg. Doe");
        HBox lastName = new HBox(lastNameLabel, lastNameTextBox);
        lastName.setAlignment(Pos.CENTER_LEFT);
        lastName.setSpacing(10);
        HBox.setHgrow(lastNameTextBox, Priority.ALWAYS);
        root.getChildren().add(lastName);

        //Company
        Label companyLabel = new Label("Company:");
        companyLabel.setMinWidth(100);
        TextField companyTextBox = new TextField(contact.company);
        companyTextBox.setPromptText("eg. ABC inc.");
        HBox company = new HBox(companyLabel, companyTextBox);
        company.setAlignment(Pos.CENTER_LEFT);
        company.setSpacing(10);
        HBox.setHgrow(companyTextBox, Priority.ALWAYS);
        root.getChildren().add(company);

        //Phone number
        Label phoneLabel = new Label("Phone number:");
        phoneLabel.setMinWidth(100);
        TextField phoneTextBox = new TextField(contact.getPhone());
        phoneTextBox.setPromptText("eg. 5551234567");
        // Restrict input to numbers only
        phoneTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                phoneTextBox.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        HBox phone = new HBox(phoneLabel, phoneTextBox);
        phone.setAlignment(Pos.CENTER_LEFT);
        phone.setSpacing(10);
        HBox.setHgrow(phoneTextBox, Priority.ALWAYS);
        root.getChildren().add(phone);

        //Email
        Label emailLabel = new Label("Email:");
        emailLabel.setMinWidth(100);
        TextField emailTextBox = new TextField(contact.getEmail());
        emailTextBox.setPromptText("eg. john.doe@example.com");
        HBox email = new HBox(emailLabel, emailTextBox);
        email.setAlignment(Pos.CENTER_LEFT);
        email.setSpacing(10);
        HBox.setHgrow(emailTextBox, Priority.ALWAYS);
        root.getChildren().add(email);

        //Address
        Label addressLabel = new Label("Address:");
        addressLabel.setMinWidth(100);
        TextField addressTextBox = new TextField(contact.getAddress());
        addressTextBox.setPromptText("eg. 123 Main St, City, State ZIP");
        HBox address = new HBox(addressLabel, addressTextBox);
        address.setAlignment(Pos.CENTER_LEFT);
        address.setSpacing(10);
        HBox.setHgrow(addressTextBox, Priority.ALWAYS);
        root.getChildren().add(address);

        //Birthday
        Label birthdayLabel = new Label("Birthday:");
        birthdayLabel.setMinWidth(100);
        TextField birthdayTextBox = new TextField(contact.getBirthday());
        birthdayTextBox.setPromptText("eg. MM/DD/YYYY");
        // Restrict input to numbers and slashes, format as date
        birthdayTextBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9/]*")) {
                birthdayTextBox.setText(newValue.replaceAll("[^0-9/]", ""));
            }
        });
        HBox birthday = new HBox(birthdayLabel, birthdayTextBox);
        birthday.setAlignment(Pos.CENTER_LEFT);
        birthday.setSpacing(10);
        HBox.setHgrow(birthdayTextBox, Priority.ALWAYS);
        root.getChildren().add(birthday);

        //Notes
        Label notesLabel = new Label("Notes:");
        notesLabel.setMinWidth(100);
        TextArea notesTextArea = new TextArea(contact.notes);
        notesTextArea.setPromptText("Additional notes...");
        notesTextArea.setPrefRowCount(3);
        notesTextArea.setWrapText(true);
        HBox notes = new HBox(notesLabel, notesTextArea);
        notes.setAlignment(Pos.TOP_LEFT);
        notes.setSpacing(10);
        HBox.setHgrow(notesTextArea, Priority.ALWAYS);
        root.getChildren().add(notes);

        //Warning
        Label warning = new Label("* indicates a required field");
        warning.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
        root.getChildren().add(warning);

        //Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        root.getChildren().add(spacer);

        //Buttons
        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(80);
        saveButton.setOnAction(e -> {
            //Check required first name field
            if (firstNameTextBox.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Field empty");
                alert.setHeaderText(null);
                alert.setContentText("Please type a name into the first name field.");
                alert.showAndWait();
                return;
            }

            // Validate email if not empty
            String emailText = emailTextBox.getText().trim();
            if (!emailText.isEmpty()) {
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                if (!Pattern.matches(emailRegex, emailText)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Email");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a valid email address.");
                    alert.showAndWait();
                    return;
                }
            }

            // Validate birthday if not empty
            String birthdayText = birthdayTextBox.getText().trim();
            if (!birthdayText.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate.parse(birthdayText, formatter);
                } catch (DateTimeParseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a valid date in MM/DD/YYYY format.");
                    alert.showAndWait();
                    return;
                }
            }

            contact.firstName = firstNameTextBox.getText();
            contact.middleName = middleNameTextBox.getText();
            contact.lastName = lastNameTextBox.getText();
            contact.company = companyTextBox.getText();
            contact.setPhone(phoneTextBox.getText());
            contact.setEmail(emailTextBox.getText());
            contact.setAddress(addressTextBox.getText());
            contact.setBirthday(birthdayTextBox.getText());
            contact.notes = notesTextArea.getText();

            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root, 500, 550);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Edit contact dialogue");
        stage.setResizable(false);
        stage.showAndWait();
    }
}

package org.ContactManager;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
    public Button deleteContactButton;

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
        deleteContactButton.setDisable(false);
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
        deleteContactButton.setDisable(true);
    }

    public Contact getFocusedContact() {
        return contactListView.getSelectionModel().getSelectedItem();
    }
}

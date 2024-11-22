package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class Articles {

    @FXML
    private Button profileButton;

    private User currentUser;

    // Method to set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Profile button action
    @FXML
    private void handleProfileButtonAction() {
        if (currentUser != null) {
            // Display the profile details
            Alert profileAlert = new Alert(AlertType.INFORMATION);
            profileAlert.setTitle("User Profile");
            profileAlert.setHeaderText("Profile Details");
            profileAlert.setContentText(
                    "Username: " + currentUser.getUsername() + "\n" +
                            "Email: " + currentUser.getEmail() + "\n" +
                            "Preferences: " + currentUser.getPreferences()
            );
            profileAlert.showAndWait();
        } else {
            showError("User profile not available.");
        }
    }

    // Utility method to show error messages
    private void showError(String message) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
}

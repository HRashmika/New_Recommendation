package org.coursework.new_recommendation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    private UserLogin userLog;

    public Login() {
        // Initialize UserAuthentication with the database and collection names
        userLog = new UserLogin("News_Recommendation_System", "User_Details");
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Use UserAuthentication to validate credentials
            if (userLog.authenticate(username, password)) {
                navigateToOptionsPage(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Username or Password.");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void navigateToOptionsPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Articles.fxml"));
            Scene optionsScene = new Scene(loader.load());

            // Switch to the options page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(optionsScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load options page: " + e.getMessage());
        }
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene mainScene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeUserAuthConnection() {
        userLog.closeConnection();
    }
}

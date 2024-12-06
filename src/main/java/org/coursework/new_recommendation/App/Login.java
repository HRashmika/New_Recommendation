package org.coursework.new_recommendation.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.coursework.new_recommendation.Services.LoginService;

import java.io.IOException;

public class Login {
    private LoginService loginService;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public Login() {
        loginService = new LoginService();
    }

    // Handling user login of the user
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (loginService.authenticate(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                navigateToOptionsPage(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Username or Password.");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    // The class to go to the options
    private void navigateToOptionsPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/userOptions.fxml"));
            Scene optionsScene = new Scene(loader.load());

            UserOptions articlesController = loader.getController();
            articlesController.setCurrentUser(loginService.getUsername());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(optionsScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load options page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/main.fxml"));
            Scene mainScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load main page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

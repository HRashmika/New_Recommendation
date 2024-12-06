package org.coursework.new_recommendation.App;

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
import org.coursework.new_recommendation.Services.AdminService;

import java.io.IOException;

public class AdminLogin {

    @FXML
    private Button loginButton;

    @FXML
    private TextField adminIdField;

    @FXML
    private PasswordField passwordField;

    private AdminService adminService;

    @FXML
    private void initialize() {
        // Initialize adminService with default values (empty fields), because the loginTimes may be null
        adminService = new AdminService("", "", null);
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        String adminId = adminIdField.getText();
        String password = passwordField.getText();

        // Set admin credentials for the adminService object
        adminService.setAdminId(adminId);
        adminService.setPassword(password);

        // Check if credentials are valid
        if (adminService.checkCredentials()) {
            adminService.recordLoginTime();  // Record login time

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/administration.fxml"));
                Scene mainPageScene = new Scene(loader.load());
                Administration adminController = loader.getController();
                adminController.setCurrentAdminUsername(adminId);
                // Here,passing the current logged in administrator

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(mainPageScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Page Load Error", "Could not load main page: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Admin ID or Password.");
        }
    }

    // Navigating back to main page
    @FXML
    public void handleBackButtonAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

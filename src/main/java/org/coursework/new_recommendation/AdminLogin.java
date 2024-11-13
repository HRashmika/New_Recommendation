package org.coursework.new_recommendation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.IOException;

public class AdminLogin {

    @FXML
    private Button backButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField adminIdField;

    @FXML
    private TextField passwordField;

    @FXML
    private Text adminLoginText;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> adminCollection;

    @FXML
    public void initialize() {
        try {
            // Initialize MongoDB connection
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            adminCollection = database.getCollection("Admin_Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to MongoDB.");
        }
    }

    // Method to handle admin login validation
    @FXML
    public void handleLogin(ActionEvent event) {
        String adminId = adminIdField.getText();
        String password = passwordField.getText();

        if (checkCredentials(adminId, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login", "Login Successful!");
            navigateToMainPage(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Admin ID or Password");
        }
    }

    // Check admin credentials against MongoDB
    private boolean checkCredentials(String adminId, String password) {
        try {
            // Query the MongoDB collection for admin credentials
            Document admin = adminCollection.find(new Document("AdminID", adminId)
                    .append("Password", password)).first();
            return admin != null;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    // Navigate to main page after successful login
    private void navigateToMainPage(ActionEvent event) {
        try {
            // Close the current window (AdminLogin window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

            // Load the main page scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainPage.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Page Error", "Could not load main page.");
        }
    }

    // Method to handle "Back" button click (navigates back to the previous screen)
    @FXML
    public void handleBackButtonAction(ActionEvent event) throws IOException {
        // Close the current window (AdminLogin window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        // Load the previous scene (e.g., a main menu or login screen)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

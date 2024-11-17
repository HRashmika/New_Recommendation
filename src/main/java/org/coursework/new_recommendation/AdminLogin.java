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
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
    private PasswordField passwordField;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> adminCollection;

    @FXML
    private void initialize() {
        // Establish MongoDB connection
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            adminCollection = database.getCollection("Admin_Login");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to MongoDB.");
        }
    }
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        // Get the Admin ID and Password from the text fields
        String adminId = adminIdField.getText();
        String password = passwordField.getText();

        // Check if credentials are valid
        if (checkCredentials(adminId, password)) {

            // Load the main page scene
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Articles.fxml"));

                Scene mainPageScene = new Scene(loader.load());

                // Get the current stage (login stage)
                Stage stage = (Stage) loginButton.getScene().getWindow();

                // Set the scene to the main page scene
                stage.setScene(mainPageScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Print the exception stack trace for debugging
                showAlert(Alert.AlertType.ERROR, "Page Load Error", "Could not load main page: " + e.getMessage());
            }
        } else {
            // Show error message if credentials are invalid
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Admin ID or Password.");
        }
    }


    // Method to check credentials against MongoDB
    private boolean checkCredentials(String adminId, String password) {
        try {
            // Query MongoDB with Admin ID and Password
            Document admin = adminCollection.find(new Document("Admin ID", adminId)
                    .append("Password", password)).first();
            return admin != null;  // Return true if matching document is found
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
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

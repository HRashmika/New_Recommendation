package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
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
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Login extends User {
    // MongoDB setup
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userDetailsCollection;
    private MongoCollection<Document> userLoginCollection;

    // UI Elements
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    public Login() {
        super(); // Initialize the User base class
        try {
            // Connect to MongoDB
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            userDetailsCollection = database.getCollection("User_Details");
            userLoginCollection = database.getCollection("User_Login");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            Document user = userDetailsCollection.find(new Document("username", username)
                    .append("password", password)).first();

            if (user != null) {
                this.setUsername(user.getString("username"));
                this.setEmail(user.getString("email"));
                this.setPreferences(user.getString("preferences"));

                String currentTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                userDetailsCollection.updateOne(
                        new Document("username", username),
                        new Document("$set", new Document("loginTime", currentTime))
                );

                userLoginCollection.updateOne(
                        Filters.and(
                                Filters.eq("username", username),
                                Filters.eq("password", password)
                        ),
                        Updates.push("loginTimes", currentTime), // Append current time to loginTimes array
                        new com.mongodb.client.model.UpdateOptions().upsert(true) // Create document if it doesn't exist
                );

                return true;
            } else {
                return false; // No matching user found
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "Error during authentication: " + e.getMessage());
            return false;
        }
    }


    // Handle login button click
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Use the authenticate method to validate credentials
            if (authenticate(username, password)) {
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

            Articles articlesController = loader.getController();

            articlesController.setCurrentUser(this.getUsername());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(optionsScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load options page: " + e.getMessage());
        }
    }


    // Handle back button click
    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene mainScene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load main page: " + e.getMessage());
        }
    }

    // Show alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Close the MongoDB connection
    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

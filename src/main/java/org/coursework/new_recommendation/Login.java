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
import javafx.fxml.Initializable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Login implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Establish MongoDB connection
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            userCollection = database.getCollection("User_Login");  // Ensure you're using the correct collection
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Connection Error", "Could not connect to MongoDB.");
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        // Get the username and password from the text fields
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if credentials are valid
        if (checkCredentials(username, password)) {
            // Load the next scene after successful login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Articles.fxml"));
                Scene optionsScene = new Scene(loader.load());

                // Get the current stage (login stage)
                Stage stage = (Stage) loginButton.getScene().getWindow();

                // Set the scene to the options page scene
                stage.setScene(optionsScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Print the exception stack trace for debugging
                showAlert(Alert.AlertType.ERROR, "Page Load Error", "Could not load options page: " + e.getMessage());
            }
        } else {
            // Show error message if credentials are invalid
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Username or Password.");
        }
    }

    // Method to check credentials against MongoDB
    private boolean checkCredentials(String username, String password) {
        try {
            // Query MongoDB with Username and Password
            Document user = userCollection.find(new Document("User Name", username)
                    .append("Password", password)).first();
            return user != null;  // Return true if matching document is found
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while checking credentials.");
        }
        return false;
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) throws IOException {
        // Navigate to the previous screen (main screen)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));  // Or your preferred screen
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

package org.coursework.new_recommendation;

import com.mongodb.client.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class SignUp {

    // FXML Fields
    @FXML
    private TextField emailTextField;

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private CheckBox techCheckBox;

    @FXML
    private CheckBox aiCheckBox;

    @FXML
    private CheckBox weatherCheckBox;

    @FXML
    private CheckBox healthcareCheckBox;

    @FXML
    private CheckBox sportsCheckBox;

    @FXML
    private CheckBox financeCheckBox;

    @FXML
    private Button signupButton;

    @FXML
    private Button backButton;

    @FXML
    private Button loginButton;

    // MongoDB client and collection
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public SignUp() {
        // Initialize MongoDB connection
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("News_Recommendation_System");
        collection = database.getCollection("User_Details");  // Use or create a collection named "User_Details"
    }

    // Handle the "Back" button click
    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        // Load the previous page or scene (e.g., login screen)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    // Handle the "Signup" button click
    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        // Handle the logic for user signup
        String email = emailTextField.getText();
        String username = userNameTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();

        // Basic validation for fields
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Please fill all the fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        // Handle the selection of preferences
        StringBuilder preferences = new StringBuilder();
        if (techCheckBox.isSelected()) preferences.append("Technology, ");
        if (aiCheckBox.isSelected()) preferences.append("AI, ");
        if (weatherCheckBox.isSelected()) preferences.append("Weather, ");
        if (healthcareCheckBox.isSelected()) preferences.append("Healthcare, ");
        if (sportsCheckBox.isSelected()) preferences.append("Sports, ");
        if (financeCheckBox.isSelected()) preferences.append("Finance, ");

        // Trim trailing comma if any
        if (preferences.toString().endsWith(", ")) {
            preferences.setLength(preferences.length() - 2);
        }

        // Create a new Document to insert into MongoDB
        Document userDocument = new Document("email", email)
                .append("username", username)
                .append("password", password)  // In a real application, passwords should be hashed
                .append("preferences", preferences.toString());

        // Insert the user data into the User_Details collection
        collection.insertOne(userDocument);

        // Confirmation message
        System.out.println("User Sign-Up Successful");
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);
        System.out.println("Preferences: " + preferences.toString());

        // Optionally: Navigate to another scene (e.g., User Home Page)
    }

    // Handle the "Login" button click (for users who already have an account)
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        // Load the login page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}

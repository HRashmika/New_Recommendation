package org.coursework.new_recommendation;

import com.mongodb.client.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class SignUp extends User {

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

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    // Constructor to initialize MongoDB connection
    public SignUp() {
        super();
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            collection = database.getCollection("User_Details");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "MongoDB connection failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        String email = emailTextField.getText();
        String username = userNameTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();

        // Validate inputs
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Error", "Please fill in all fields.");
            return;
        }

        // Check if email already exists
        Document existingUser = collection.find(new Document("email", email)).first();
        if (existingUser != null) {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Error", "Email already exists.");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Error", "Passwords do not match.");
            return;
        }

        // Collect user preferences
        StringBuilder preferencesBuilder = new StringBuilder();
        if (techCheckBox.isSelected()) preferencesBuilder.append("Technology, ");
        if (aiCheckBox.isSelected()) preferencesBuilder.append("AI, ");
        if (weatherCheckBox.isSelected()) preferencesBuilder.append("Weather, ");
        if (healthcareCheckBox.isSelected()) preferencesBuilder.append("Healthcare, ");
        if (sportsCheckBox.isSelected()) preferencesBuilder.append("Sports, ");
        if (financeCheckBox.isSelected()) preferencesBuilder.append("Finance, ");

        // Remove the trailing comma and space
        if (preferencesBuilder.toString().endsWith(", ")) {
            preferencesBuilder.setLength(preferencesBuilder.length() - 2);
        }

        // Use inherited setters from User class
        this.setEmail(email);
        this.setUsername(username);
        this.setPreferences(preferencesBuilder.toString());

        // Insert into MongoDB
        try {
            Document userDocument = new Document("email", this.getEmail())
                    .append("username", this.getUsername())
                    .append("password", password) // Password is handled here directly
                    .append("preferences", this.getPreferences());
            collection.insertOne(userDocument);

            showAlert(Alert.AlertType.INFORMATION, "Sign-Up Success", "Account created successfully!");
            navigateToOptions();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Error", "Failed to create account: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    private void navigateToOptions() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Articles.fxml"));
        AnchorPane optionsPane = loader.load();
        Scene optionsScene = new Scene(optionsPane);
        Stage stage = (Stage) signupButton.getScene().getWindow();
        stage.setScene(optionsScene);
        stage.show();
    }

    // Utility method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

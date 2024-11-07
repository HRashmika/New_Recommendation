package org.coursework.new_recommendation;

import com.mongodb.client.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bson.Document;

public class SignUp {
    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private RadioButton entertainmentRadio;

    @FXML
    private RadioButton financialRadio;

    @FXML
    private RadioButton politicalRadio;

    @FXML
    private RadioButton sportsRadio;

    @FXML
    private RadioButton weatherRadio;

    @FXML
    private Button signUpButton;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userDetailsCollection;

    @FXML
    public void initialize() {
        // Connect to MongoDB
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("News_Recommendation_System");
        userDetailsCollection = database.getCollection("user_details");
    }

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Error", "Passwords do not match!");
            return;
        }

        // Collect preferences
        StringBuilder preferences = new StringBuilder();
        if (entertainmentRadio.isSelected()) preferences.append("Entertainment, ");
        if (financialRadio.isSelected()) preferences.append("Financial, ");
        if (politicalRadio.isSelected()) preferences.append("Political, ");
        if (sportsRadio.isSelected()) preferences.append("Sports, ");
        if (weatherRadio.isSelected()) preferences.append("Weather");

        // Create a document and insert into MongoDB
        Document newUser = new Document("Email", email)
                .append("Username", username)
                .append("Password", password)
                .append("Preferences", preferences.toString());

        userDetailsCollection.insertOne(newUser);

        showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "User signed up successfully!");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

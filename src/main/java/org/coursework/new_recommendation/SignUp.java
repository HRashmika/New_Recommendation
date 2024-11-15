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
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class SignUp {

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

    @FXML
    private Label errorMessageLabel;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public SignUp() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            collection = database.getCollection("User_Details");
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
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

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessageLabel.setText("Please fill all the fields.");
            errorMessageLabel.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessageLabel.setText("Passwords do not match.");
            errorMessageLabel.setVisible(true);
            return;
        }

        StringBuilder preferences = new StringBuilder();
        if (techCheckBox.isSelected()) preferences.append("Technology, ");
        if (aiCheckBox.isSelected()) preferences.append("AI, ");
        if (weatherCheckBox.isSelected()) preferences.append("Weather, ");
        if (healthcareCheckBox.isSelected()) preferences.append("Healthcare, ");
        if (sportsCheckBox.isSelected()) preferences.append("Sports, ");
        if (financeCheckBox.isSelected()) preferences.append("Finance, ");

        if (preferences.toString().endsWith(", ")) {
            preferences.setLength(preferences.length() - 2);
        }

        Document userDocument = new Document("email", email)
                .append("username", username)
                .append("password", password)
                .append("preferences", preferences.toString());

        try {
            collection.insertOne(userDocument);
            System.out.println("User Sign-Up Successful");
        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
            errorMessageLabel.setText("Sign-up failed. Try again.");
            errorMessageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

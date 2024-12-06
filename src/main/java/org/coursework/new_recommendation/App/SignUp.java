package org.coursework.new_recommendation.App;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import org.bson.Document;
import  org.coursework.new_recommendation.Services.SignUpService;
import org.coursework.new_recommendation.Database.MongoDBConnection;

import java.io.IOException;

public class SignUp{

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
    private CheckBox politicsCheckBox;

    @FXML
    private CheckBox weatherCheckBox;

    @FXML
    private CheckBox healthcareCheckBox;

    @FXML
    private CheckBox sportsCheckBox;

    @FXML
    private CheckBox entertainmentCheckBox;

    @FXML
    private Button signupButton;

    @FXML
    private Button backButton;

    private SignUpService signUpLogic;

    public SignUp() {
        try {
            // Here, the preferred categories are stored in 2 collections
            MongoCollection<Document> userCollection = MongoDBConnection.getDatabase().getCollection("User_Details");
            MongoCollection<Document> preferencesCollection = MongoDBConnection.getDatabase().getCollection("Preferences");
            signUpLogic = new SignUpService(userCollection, preferencesCollection);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "MongoDB connection failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    // Here , sign up is handled with the help of logic in the signup services
    @FXML
    private void handleSignUpButtonAction(ActionEvent event) {
        String email = emailTextField.getText().trim();
        String username = userNameTextField.getText().trim();
        String password = passwordTextField.getText().trim();
        String confirmPassword = confirmPasswordTextField.getText().trim();

        boolean[] preferences = {
                techCheckBox.isSelected(),
                sportsCheckBox.isSelected(),
                healthcareCheckBox.isSelected(),
                politicsCheckBox.isSelected(),
                weatherCheckBox.isSelected(),
                entertainmentCheckBox.isSelected()
        };

        String result = signUpLogic.validateAndRegister(email, username, password, confirmPassword, preferences);

        if (result.equals("SUCCESS")) {
            showAlert(Alert.AlertType.INFORMATION, "Sign-Up Success", "Account created successfully!");
            navigateToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Sign-Up Error", result);
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/login.fxml"));
            AnchorPane optionsPane = loader.load();
            Scene optionsScene = new Scene(optionsPane);
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(optionsScene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load the login page: " + e.getMessage());
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

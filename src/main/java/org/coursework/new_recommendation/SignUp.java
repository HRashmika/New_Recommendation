package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

public class SignUp {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button backButton;

    @FXML
    private Button signupButton;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox technologyCheckbox;

    @FXML
    private CheckBox aiCheckbox;

    @FXML
    private CheckBox weatherCheckbox;

    @FXML
    private CheckBox healthcareCheckbox;

    @FXML
    private CheckBox sportsCheckbox;

    @FXML
    private CheckBox financeCheckbox;

    // Method to initialize the controller
    public void initialize() {
        signupButton.setOnAction(this::handleSignup);
        backButton.setOnAction(this::handleBack);
        loginButton.setOnAction(this::handleLogin);
    }

    private void handleSignup(ActionEvent event) {
        // Get text input from fields
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input and check password confirmation
        if (password.equals(confirmPassword)) {
            // Handle preferences selected
            boolean prefersTechnology = technologyCheckbox.isSelected();
            boolean prefersAI = aiCheckbox.isSelected();
            boolean prefersWeather = weatherCheckbox.isSelected();
            boolean prefersHealthcare = healthcareCheckbox.isSelected();
            boolean prefersSports = sportsCheckbox.isSelected();
            boolean prefersFinance = financeCheckbox.isSelected();

            // Here, add code to save this data to the database or handle it as required
            System.out.println("User signed up with the following details:");
            System.out.println("Email: " + email);
            System.out.println("Username: " + username);
            System.out.println("Technology: " + prefersTechnology);
            System.out.println("AI: " + prefersAI);
            System.out.println("Weather: " + prefersWeather);
            System.out.println("Healthcare: " + prefersHealthcare);
            System.out.println("Sports: " + prefersSports);
            System.out.println("Finance: " + prefersFinance);
        } else {
            System.out.println("Passwords do not match.");
        }
    }

    private void handleBack(ActionEvent event) {
        // Code to navigate back to the previous screen or main menu
        System.out.println("Back button clicked");
    }

    private void handleLogin(ActionEvent event) {
        // Code to open the login screen
        System.out.println("Login button clicked");
    }
}

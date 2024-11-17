package org.coursework.new_recommendation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MainApplication extends Application {

    @FXML
    private Button loginButton; // Corresponds to your FXML

    @FXML
    private Button signUpButton;

    @FXML
    private Button UserloginButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main application FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Articles.fxml"));
        AnchorPane root = loader.load();

        // Set the main stage
        primaryStage.setTitle("Main Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Handle the "Login" button click
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_login.fxml"));
        AnchorPane adminLoginPane = loader.load();
        Scene adminLoginScene = new Scene(adminLoginPane);

        // Use the current stage
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.setScene(adminLoginScene);
        currentStage.show();
    }

    @FXML
    private void handleUserLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane userLoginPane = loader.load();
        Scene userLoginScene = new Scene(userLoginPane);

        // Use the current stage
        Stage currentStage = (Stage) UserloginButton.getScene().getWindow();
        currentStage.setScene(userLoginScene);
        currentStage.show();
    }

    @FXML
    private void handleUserSignUpButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
        AnchorPane signUpPane = loader.load();
        Scene signUpScene = new Scene(signUpPane);

        // Use the current stage
        Stage currentStage = (Stage) signUpButton.getScene().getWindow();
        currentStage.setScene(signUpScene);
        currentStage.show();
    }
}

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
    private Button loginButton; // Make sure this corresponds to your FXML

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main application FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        AnchorPane root = loader.load();

        // Set the main stage
        primaryStage.setTitle("Main Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Method for handling the login button action
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        // Load the AdminLogin.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_login.fxml"));
        AnchorPane adminLoginPane = loader.load();

        // Create a new scene with the AdminLogin layout
        Scene adminLoginScene = new Scene(adminLoginPane);

        // Get the current stage (main stage)
        Stage stage = (Stage) loginButton.getScene().getWindow();

        // Set the scene to the admin login scene
        stage.setScene(adminLoginScene);
        stage.show();
    }
}

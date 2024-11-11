package org.coursework.new_recommendation;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainApplication extends Application {

    @FXML
    private Button signUpButton;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the main.fxml file to show the main scene
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);
        stage.setTitle("Main Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @FXML
    public void initialize() {
        // Debugging the signUpButton initialization
        System.out.println("signUpButton is: " + signUpButton);

        // Ensure the button is initialized before setting the action
        if (signUpButton != null) {
            signUpButton.setOnAction(this::handlesignUpButtonClick);  // Set the event handler programmatically
        } else {
            System.out.println("signUpButton is null.");
        }
    }

    // This method is called when the "Sign Up" button is clicked
    private void handlesignUpButtonClick(ActionEvent event) {
        try {
            // Load the SignUp.fxml file and set it as the new scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent signUpRoot = loader.load();

            // Get the current stage (window) from the button
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(new Scene(signUpRoot, 750, 600));  // Adjust size as needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

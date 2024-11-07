package org.coursework.new_recommendation;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Mainpage extends Application {

    @FXML
    private Button signupButton; // Add reference to the Signup button

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Mainpage.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("News Recommendation - Main Page");
            primaryStage.setScene(new Scene(root, 569, 404));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSignupButtonClick() {
        try {
            // Load the SignUp page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent signUpRoot = loader.load();

            // Set the SignUp scene on the same stage
            primaryStage.setScene(new Scene(signUpRoot, 750, 604));
            primaryStage.setTitle("News Recommendation - Sign Up");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

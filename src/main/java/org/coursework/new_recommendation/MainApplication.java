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
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Button UserloginButton;

    private static String loginUser;

    public static String getLoginUser() {
        return loginUser;
    }


    public static void setLoginUser(String username) {
        loginUser = username;
    }



    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            AnchorPane root = loader.load();

            primaryStage.setTitle("News Recommendation System");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }



    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_login.fxml"));
        AnchorPane adminLoginPane = loader.load();
        Scene adminLoginScene = new Scene(adminLoginPane);

        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.setScene(adminLoginScene);
        currentStage.show();
    }

    @FXML
    private void handleUserLoginButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane userLoginPane = loader.load();
        loginUser = "sample";
        Scene userLoginScene = new Scene(userLoginPane);

        Stage currentStage = (Stage) UserloginButton.getScene().getWindow();
        currentStage.setScene(userLoginScene);
        currentStage.show();
    }

    @FXML
    private void handleUserSignUpButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("signup.fxml"));
        AnchorPane signUpPane = loader.load();
        Scene signUpScene = new Scene(signUpPane);

        Stage currentStage = (Stage) signUpButton.getScene().getWindow();
        currentStage.setScene(signUpScene);
        currentStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}

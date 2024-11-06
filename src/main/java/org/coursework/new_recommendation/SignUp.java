package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SignUp {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
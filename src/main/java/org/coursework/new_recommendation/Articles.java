package org.coursework.new_recommendation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;

public class Articles {

    @FXML
    private Button homeButton;

    @FXML
    private Button recommendationsButton;

    @FXML
    private Button savedButton;

    @FXML
    private Button profileButton;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public Articles() {
        // Initialize MongoDB connection
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            userCollection = database.getCollection("User_Details");
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfileButtonAction(ActionEvent event) {
        try {
            // Fetch user profile data and login history
            Document userProfile = getUserProfile("exampleUsername"); // Replace with actual logic to get the logged-in username
            if (userProfile == null) {
                showAlert("Profile Error", "User profile not found.");
                return;
            }

            // Create and populate a new scene for the profile view
            AnchorPane profilePane = new AnchorPane();
            TableView<String> loginHistoryTable = new TableView<>();
            Button backButton = new Button("Back");

            // Populate user profile data
            javafx.scene.control.TextField userNameField = new javafx.scene.control.TextField("Username: " + userProfile.getString("username"));
            userNameField.setLayoutX(50);
            userNameField.setLayoutY(50);

            javafx.scene.control.TextField emailField = new javafx.scene.control.TextField("Email: " + userProfile.getString("email"));
            emailField.setLayoutX(50);
            emailField.setLayoutY(100);

            // Create login history table columns
            TableColumn<String, String> dateColumn = new TableColumn<>("Date");
            TableColumn<String, String> timeColumn = new TableColumn<>("Time");
            loginHistoryTable.getColumns().addAll(dateColumn, timeColumn);

            // Set login history table data (mock data for now)
            loginHistoryTable.setLayoutX(50);
            loginHistoryTable.setLayoutY(150);
            loginHistoryTable.setPrefWidth(500);
            loginHistoryTable.setPrefHeight(200);

            // Configure back button
            backButton.setLayoutX(250);
            backButton.setLayoutY(370);
            backButton.setOnAction(e -> handleBackButtonAction(event));

            // Add all components to the profile pane
            profilePane.getChildren().addAll(userNameField, emailField, loginHistoryTable, backButton);

            // Show the profile scene
            Stage currentStage = (Stage) profileButton.getScene().getWindow();
            currentStage.setScene(new Scene(profilePane, 600, 400));

        } catch (Exception e) {
            showAlert("Profile Error", "An error occurred while loading the profile: " + e.getMessage());
        }
    }

    private Document getUserProfile(String username) {
        // Fetch user profile from MongoDB
        try {
            return userCollection.find(new Document("username", username)).first();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Navigate back to the articles page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("articles.fxml"));
            AnchorPane articlesPane = loader.load();
            Scene articlesScene = new Scene(articlesPane);

            Stage currentStage = (Stage) profileButton.getScene().getWindow();
            currentStage.setScene(articlesScene);
            currentStage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to navigate back to articles: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

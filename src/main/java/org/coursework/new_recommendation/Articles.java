package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Articles {

    @FXML
    private Button profileButton;

    @FXML
    private Button articlesButton;

    @FXML
    private StackPane contentPane;

    @FXML
    private Pane articlesPane;

    @FXML
    private Pane articlesDisplayPane;

    private User currentUser;


    @FXML
    private void handleArticlesButtonAction() {
        articlesPane.setVisible(true);
    }

    @FXML
    private void handleTechnologyButtonAction() {
        displayArticlesByCategory("Technology");
    }

    @FXML
    private void handleSportsButtonAction() {
        displayArticlesByCategory("Sports");
    }

    @FXML
    private void handleHealthButtonAction() {
        displayArticlesByCategory("Health");
    }

    @FXML
    private void handleFinanceButtonAction() {
        displayArticlesByCategory("Finance");
    }

    // Fetch and display articles by category
    private void displayArticlesByCategory(String category) {
        // Clear the display pane
        articlesDisplayPane.getChildren().clear();

        List<Document> articles = getArticlesByCategory(category);
        if (articles.isEmpty()) {
            Text noArticlesText = new Text("No articles found for category: " + category);
            noArticlesText.setLayoutY(20); // Position text within the pane
            articlesDisplayPane.getChildren().add(noArticlesText);
            return;
        }

        // Display articles as text
        double yOffset = 10;
        for (Document article : articles) {
            String articleTitle = article.getString("Heading"); // Adjust key based on your MongoDB schema
            Text articleText = new Text(articleTitle);
            articleText.setLayoutY(yOffset);
            articlesDisplayPane.getChildren().add(articleText);
            yOffset += 20; // Adjust spacing between articles
        }
    }

    public List<Document> getArticlesByCategory(String category) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("News Articles");

            return collection.find(new Document("Category", category)).into(new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to fetch articles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Utility method to show error messages
    private void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    // Method to set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleProfileButtonAction() {
        if (currentUser != null) {
            Alert profileAlert = new Alert(Alert.AlertType.INFORMATION);
            profileAlert.setTitle("User Profile");
            profileAlert.setHeaderText("Profile Details");
            profileAlert.setContentText(
                    "Username: " + currentUser.getUsername() + "\n" +
                            "Email: " + currentUser.getEmail() + "\n" +
                            "Preferences: " + currentUser.getPreferences()
            );
            profileAlert.showAndWait();
        } else {
            showError("User profile not available.");
        }
    }
}

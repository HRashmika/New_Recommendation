package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import java.awt.*;
import java.util.List;
import java.util.Map;

public class Articles {

    @FXML
    private Button homeButton;

    @FXML
    private Button recommendationsButton;

    @FXML
    private Button savedButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button articlesButton;

    @FXML
    private Button technologyButton;

    @FXML
    private Button sportsButton;

    @FXML
    private Button healthButton;

    @FXML
    private Button politicsButton;

    @FXML
    private Button financeButton;

    @FXML
    private Button weatherButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField preferencesField;

    @FXML
    private StackPane contentPane;

    @FXML
    private Pane profilePane;

    @FXML
    private Pane articlesPane;

    @FXML
    private ListView<String> articlesListView; // ListView to display articles

    private final ArticleProcess articleProcess = new ArticleProcess();
    private Map<String, List<Document>> categorizedArticles;

    @FXML
    private void handleArticlesButtonAction() {

        categorizedArticles = articleProcess.processArticles();
        articlesPane.setVisible(true);
        articlesListView.getItems().clear();
    }

    @FXML
    private void handleTechnologyButtonAction() {
        displayArticlesByCategory("technology");
    }

    @FXML
    private void handleSportsButtonAction() {
        displayArticlesByCategory("sports");
    }

    @FXML
    private void handleHealthButtonAction() {
        displayArticlesByCategory("health");
    }
    @FXML
    private void handlePoliticsButtonAction() {
        displayArticlesByCategory("politics");
    }
    @FXML
    private void handleFinanceButtonAction() {
        displayArticlesByCategory("finance");
    }
    @FXML
    private void handleWeatherButtonAction() {
        displayArticlesByCategory("weather");
    }


    private void displayArticlesByCategory(String category) {
        articlesListView.getItems().clear(); // Clear previous list

        if (categorizedArticles != null && categorizedArticles.containsKey(category)) {
            List<Document> articles = categorizedArticles.get(category);

            for (Document article : articles) {
                articlesListView.getItems().add(article.getString("Heading"));
            }
        } else {
            showError("No articles available for the selected category.");
        }
    }

    private void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    @FXML
    private void handleProfileButtonAction() {
        String currentUsername = MainApplication.getLoginUser();


        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("New_Recommendation_System");
            MongoCollection<Document> userCollection = database.getCollection("User_Details");


            Document user = userCollection.find(new Document("username", currentUsername)).first();

            if (user != null) {
                String email = user.getString("email");
                String username = user.getString("username");
                String preferences = user.getString("preferences");


                Alert profileAlert = new Alert(Alert.AlertType.INFORMATION);
                profileAlert.setTitle("User Profile");
                profileAlert.setHeaderText("Profile Details");
                profileAlert.setContentText(
                        "Username: " + username + "\n" +
                                "Email: " + email + "\n" +
                                "Preferences: " + (preferences != null ? preferences : "None")
                );
                profileAlert.showAndWait();
            } else {
                showError("User details not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while fetching profile details.");
        }
    }
}

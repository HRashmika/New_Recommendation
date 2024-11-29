package org.coursework.new_recommendation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class Articles {

    @FXML
    private Button homeButton, recommendationsButton, likeArticleButton, profileButton, articlesButton;
    @FXML
    private Button technologyButton, sportsButton, healthButton, politicsButton, entertainmentButton, weatherButton;
    @FXML
    private TextField usernameField, emailField, preferencesField;
    @FXML
    private StackPane contentPane;
    @FXML
    private Pane profilePane, articlesPane, articleDetailsPane;
    @FXML
    private ListView<String> articlesListView;
    @FXML
    private Text headlineText, descriptionText, authorsText, dateText, linkText;
    @FXML
    private Pane likedArticlesPane;
    @FXML
    private ListView<String> likedArticlesListView;


    private final ObservableList<String> likedHeadlines = FXCollections.observableArrayList();
    private final ObservableList<String> dislikedHeadlines = FXCollections.observableArrayList();

    private String currentUser; // Logged-in user
    private final ArticleProcess articleProcess = new ArticleProcess();
    private Map<String, List<Document>> categorizedArticles;

    // Set the current user and load their details
    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadUserDetails();
    }

    private void loadUserDetails() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("User_Details");

            Document userDetails = collection.find(new Document("username", currentUser)).first();

            if (userDetails != null) {
                usernameField.setText(userDetails.getString("username"));
                emailField.setText(userDetails.getString("email"));
                preferencesField.setText(userDetails.getString("preferences"));
            } else {
                showError("User details not found.");
            }
        } catch (Exception e) {
            showError("Failed to load user details: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfileButtonAction() {
        switchPane(profilePane);
        if (currentUser != null) {
            loadUserDetails();
        } else {
            showError("No user is currently logged in.");
        }
    }

    @FXML
    private void handleArticlesButtonAction() {
        categorizedArticles = articleProcess.processArticles();
        switchPane(articlesPane);
        articlesListView.getItems().clear();
        articlesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleArticleClick(newValue);
        });
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
    private void handleEntertainmentButtonAction() {
        displayArticlesByCategory("entertainment");
    }

    @FXML
    private void handleWeatherButtonAction() {
        displayArticlesByCategory("weather");
    }

    private void displayArticlesByCategory(String category) {
        articlesListView.getItems().clear();
        if (categorizedArticles != null && categorizedArticles.containsKey(category)) {
            List<Document> articles = categorizedArticles.get(category);
            for (Document article : articles) {
                String headline = article.getString("headline");
                if (headline != null) {
                    articlesListView.getItems().add(headline);
                } else {
                    showError("Article headline is missing.");
                }
            }
        } else {
            showError("No articles available for the selected category.");
        }
    }

    private void handleArticleClick(String selectedHeadline) {
        if (selectedHeadline != null && !isDialogOpen) {
            Document selectedArticle = findArticleByHeadline(selectedHeadline);
            if (selectedArticle != null) {
                showArticleDialog(selectedArticle);
            } else {
                showError("Selected article not found.");
            }
        } else {
            showError("No article selected.");
        }
    }


    private Document findArticleByHeadline(String headline) {
        for (List<Document> articleList : categorizedArticles.values()) {
            for (Document article : articleList) {
                if (headline.equals(article.getString("headline"))) {
                    return article;
                }
            }
        }
        return null;
    }

    private void likeArticle(String headline) {
        if (!likedHeadlines.contains(headline)) {
            likedHeadlines.add(headline);
            dislikedHeadlines.remove(headline);
            showInfo("You liked the article: " + headline);
        } else {
            showInfo("You already liked this article.");
        }
    }

    private void dislikeArticle(String headline) {
        if (!dislikedHeadlines.contains(headline)) {
            dislikedHeadlines.add(headline);
            likedHeadlines.remove(headline);
            showInfo("You disliked the article: " + headline);
        } else {
            showInfo("You already disliked this article.");
        }
    }

    private boolean isDialogOpen = false;

    private void showArticleDialog(Document article) {
        if (isDialogOpen) return;

        isDialogOpen = true;

        String headline = article.getString("headline");
        String description = article.getString("short description");
        String authors = article.getString("authors");
        String date = article.getString("date");
        String link = article.getString("link");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Article Details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-pref-width: 400; -fx-pref-height: 300;");

        Button likeButton = new Button("Like");
        Button dislikeButton = new Button("Dislike");

        likeButton.setOnAction(e -> likeArticle(headline)); // Call renamed method
        dislikeButton.setOnAction(e -> dislikeArticle(headline));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Text("Headline: " + headline),
                new Text("Description: " + description),
                new Text("Authors: " + authors),
                new Text("Date: " + date),
                new Text("Link: " + link),
                likeButton,
                dislikeButton
        );

        dialogPane.setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.setOnHidden(e -> isDialogOpen = false);

        dialog.showAndWait();
    }


    @FXML
    private void handleLikeAction() {
        String selectedHeadline = articlesListView.getSelectionModel().getSelectedItem();
        if (selectedHeadline != null) {
            likeArticle(selectedHeadline);
        } else {
            showError("No article selected to like.");
        }
    }



    @FXML
    private void handleDislikeAction() {
        String selectedHeadline = articlesListView.getSelectionModel().getSelectedItem();
        if (selectedHeadline != null) {
            dislikeArticle(selectedHeadline);
        } else {
            showError("No article selected to dislike.");
        }
    }

    @FXML
    private void handleLikeArticleButtonAction() {
        switchPane(likedArticlesPane);
        likedArticlesListView.setItems(likedHeadlines);
        if (likedHeadlines.isEmpty()) {
            showInfo("You haven't liked any articles yet.");
        }
    }



    private void switchPane(Pane paneToShow) {
        for (Pane pane : List.of(profilePane, articlesPane, articleDetailsPane,likedArticlesPane)) {
            pane.setVisible(pane == paneToShow);
        }
    }

    private void showInfo(String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }

    private void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public ObservableList<String> getLikedHeadlines() {
        return likedHeadlines;
    }

    public ObservableList<String> getDislikedHeadlines() {
        return dislikedHeadlines;
    }
}

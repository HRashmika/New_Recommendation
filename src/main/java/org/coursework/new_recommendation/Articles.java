package org.coursework.new_recommendation;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;

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
    private Pane profilePane, articlesPane, articleDetailsPane, likedArticlesPane, recommendationsPane;
    @FXML
    private ListView<String> articlesListView, likedArticlesListView;
    @FXML
    private Text headlineText, authorsText, dateText, linkText;
    @FXML
    private TextArea descriptionText;

    @FXML
    private TableView<Document> recommendationsTableView;



    @FXML
    private TableColumn<Document, String> headlineColumn, categoryColumn;

    private final ObservableList<String> likedHeadlines = FXCollections.observableArrayList();
    private final ObservableList<String> dislikedHeadlines = FXCollections.observableArrayList();
    private String currentUser; // Logged-in user
    private final ArticleProcess articleProcess = new ArticleProcess();
    private Map<String, List<Document>> categorizedArticles;
    private Recommendation articleRecommender;
    private HostServices hostServices;
    private boolean isDialogOpen = false;

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadUserDetails();
    }

    private MongoClient mongoClient;
    private MongoDatabase database;

    // Initialize MongoDB connection once
    public void initialize() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");
            articleRecommender = new Recommendation(database);
        } catch (Exception e) {
            showError("Failed to connect to MongoDB: " + e.getMessage());
        }
            // Set up cell value factories for each column
            headlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getString("headline")));
            categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getString("category")));


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
    @FXML
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

            // Fetch category directly from MongoDB
            String category = getCategoryForHeadline(headline);
            if (category != null) {
                articleRecommender.updateScore(category, 3); // Add points
            }

            showInfo("You liked the article: " + headline);
        } else {
            showInfo("You already liked this article.");
        }
    }

    private void dislikeArticle(String headline) {
        if (!dislikedHeadlines.contains(headline)) {
            dislikedHeadlines.add(headline);
            likedHeadlines.remove(headline);

            // Fetch category directly from MongoDB
            String category = getCategoryForHeadline(headline);
            if (category != null) {
                articleRecommender.updateScore(category, -3); // Deduct points
            }

            showInfo("You disliked the article: " + headline);
        } else {
            showInfo("You already disliked this article.");
        }
    }
    @FXML
    private void handleUpdateUsernameAction() {
        String newUsername = usernameField.getText();
        if (newUsername == null || newUsername.trim().isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }
        Map<String, String> update = Map.of("username", newUsername);
        updateProfileFields(update);
    }

    @FXML
    private void handleUpdateEmailAction() {
        String newEmail = emailField.getText();
        if (newEmail == null || newEmail.trim().isEmpty()) {
            showError("Email cannot be empty.");
            return;
        }
        Map<String, String> update = Map.of("email", newEmail);
        updateProfileFields(update);
    }

    @FXML
    private void handleUpdatePreferencesAction() {
        String newPreferences = preferencesField.getText();
        if (newPreferences == null || newPreferences.trim().isEmpty()) {
            showError("Preferences cannot be empty.");
            return;
        }
        Map<String, String> update = Map.of("preferences", newPreferences);
        updateProfileFields(update);
    }


    private void updateProfileFields(Map<String, String> updatedFields) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("User_Details");

            // Fetch the existing user document
            Document userDocument = collection.find(new Document("username", currentUser)).first();

            if (userDocument != null) {
                // Update all specified fields in the local document
                for (Map.Entry<String, String> entry : updatedFields.entrySet()) {
                    String field = entry.getKey();
                    String newValue = entry.getValue();
                    if (newValue != null && !newValue.trim().isEmpty()) {
                        userDocument.put(field, newValue);
                    }
                }

                // Write the updated document back to the database
                collection.replaceOne(new Document("username", currentUser), userDocument);

                showInfo("Profile updated successfully.");
            } else {
                showError("User not found.");
            }
        } catch (Exception e) {
            showError("Failed to update profile: " + e.getMessage());
        }
    }



    private String getCategoryForHeadline(String headline) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("News_Articles");

            // Query for the document with the matching headline
            Document query = new Document("headline", headline);
            Document article = collection.find(query).first();

            if (article != null) {
                return article.getString("category");
            } else {
                showError("Category not found for the headline: " + headline);
            }
        } catch (Exception e) {
            showError("Failed to fetch category from MongoDB: " + e.getMessage());
        }
        return null; // Return null if not found
    }
    @FXML
    private void handleRecommendationsButtonAction() {
        List<Document> recommendedArticles = articleRecommender.getRecommendedArticles(currentUser);

        if (recommendedArticles.isEmpty()) {
            showError("No recommended articles found.");
        } else {
            // Print articles to the terminal for verification
            for (Document article : recommendedArticles) {
                System.out.println("Headline: " + article.getString("headline") + ", Category: " + article.getString("category"));
            }

            // Clear previous data in the TableView
            recommendationsTableView.getItems().clear();

            // Create an ObservableList to hold the recommended articles
            ObservableList<Document> recommendationList = FXCollections.observableArrayList(recommendedArticles);

            // Populate the TableView with new data
            recommendationsTableView.setItems(recommendationList);
        }

        // Make sure the recommendations pane is visible
        switchPane(recommendationsPane);
    }


    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    private HostServices getHostServices() {
        return hostServices;
    }

    public class MainApplication extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("your_layout.fxml"));
            Parent root = loader.load();

            Articles controller = loader.getController();
            controller.setHostServices(getHostServices()); // Pass HostServices to the controller

            primaryStage.setTitle("News Recommendation System");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    private void showArticleDialog(Document article) {
        if (isDialogOpen) return;

        isDialogOpen = true;

        String headline = article.getString("headline");
        String description = article.getString("short description");
        String authors = article.getString("authors");
        String date = article.getString("date");
        String link = article.getString("link");

        if (description == null || description.trim().isEmpty()) {
            description = "No description available for this article.";
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Article Details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-pref-width: 400; -fx-pref-height: 300;");

        Button likeButton = new Button("Like");
        Button dislikeButton = new Button("Dislike");
        Button openLinkButton = new Button("Open Full Article");

        likeButton.setOnAction(e -> likeArticle(headline));
        dislikeButton.setOnAction(e -> dislikeArticle(headline));
        openLinkButton.setOnAction(e -> {
            if (link != null && !link.isBlank()) {
                getHostServices().showDocument(link);
            } else {
                showError("No link available for this article.");
            }
        });

        // Use a TextArea for the description to handle long texts
        TextArea descriptionArea = new TextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);  // Enable word wrapping

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Text("Headline: " + headline),
                new Text("Authors: " + authors),
                new Text("Date: " + date),
                new Text("Link: " + link),
                new Label("Description:"),
                descriptionArea,  // Use the TextArea for the description
                likeButton,
                dislikeButton,
                openLinkButton
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
    private void handleOpenLinkAction() {
        String link = linkText.getText(); // Assuming linkText contains the article URL
        if (link != null && !link.isBlank()) {
            getHostServices().showDocument(link); // Opens the link in the default web browser
        } else {
            showError("No link available for this article.");
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
        for (Pane pane : List.of(profilePane, articlesPane, articleDetailsPane,likedArticlesPane,recommendationsPane)) {
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
        System.out.println(message); // Add this line to print the error to the console
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

package org.coursework.new_recommendation;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.stage.Stage;
import org.bson.Document;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class Articles {
    @FXML
    private Button logoutbutton;
    @FXML
    private TextField usernameField, emailField, preferencesField;

    @FXML
    private Pane profilePane, articlesPane, articleDetailsPane, likedArticlesPane, recommendationsPane;
    @FXML
    private ListView<String> articlesListView, likedArticlesListView;

    @FXML
    private TableView<Document> recommendationsTableView;
    @FXML
    private TableView<String> loginTimesTableView;
    @FXML
    private TableColumn<String, String> loginTimeColumn;

    @FXML
    private TableColumn<Document, String> headlineColumn, categoryColumn;

    private final ObservableList<String> likedHeadlines = FXCollections.observableArrayList();
    private final ObservableList<String> dislikedHeadlines = FXCollections.observableArrayList();
    private String currentUser;
    private final ArticleProcess articleProcess = new ArticleProcess();
    private Map<String, List<Document>> categorizedArticles;
    private boolean isDialogOpen = false;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private String currentArticleLink;

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadUserDetails();
    }
    public void initialize() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("News_Recommendation_System");


            headlineColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getString("headline")));
            categoryColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getString("category")));
            loginTimeColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue()));
        } catch (Exception e) {
            showError("Failed to connect to MongoDB: " + e.getMessage());
        }
    }
    private void displayLoginTimes(List<String> loginTimes) {

        ObservableList<String> loginTimesObservable = FXCollections.observableArrayList(loginTimes);
        loginTimesTableView.setItems(loginTimesObservable);
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

    private void loadUserDetails() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");

            MongoCollection<Document> userDetailsCollection = database.getCollection("User_Details");
            Document userDetails = userDetailsCollection.find(new Document("username", currentUser)).first();

            if (userDetails != null) {
                usernameField.setText(userDetails.getString("username"));
                emailField.setText(userDetails.getString("email"));
                preferencesField.setText(userDetails.getString("preferences"));
            } else {
                showError("User details not found.");
                return;
            }

            MongoCollection<Document> userLoginCollection = database.getCollection("User_Login");
            Document userLogin = userLoginCollection.find(new Document("username", currentUser)).first();

            if (userLogin != null) {
                List<String> loginTimes = userLogin.getList("loginTimes", String.class);
                if (loginTimes != null && !loginTimes.isEmpty()) {
                    displayLoginTimes(loginTimes);  // Display login times
                } else {
                    showError("No login times available.");
                }
            } else {
                showError("User login details not found.");
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

    private void showArticleDialog(Document article) {
        if (isDialogOpen) return;

        isDialogOpen = true;

        String headline = article.getString("headline");
        String description = article.getString("short description");
        String authors = article.getString("authors");
        String date = article.getString("date");
        currentArticleLink = article.getString("link");  // Store the link here

        if (description == null || description.trim().isEmpty()) {
            description = "No description available for this article.";
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Article Details");



        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #E0B0FF; -fx-pref-width: 400; -fx-pref-height: 300;");

        Button likeButton = new Button("Like");
        Button dislikeButton = new Button("Dislike");
        Button openLinkButton = new Button("Open Full Article");

        likeButton.setOnAction(e -> likeArticle(headline));
        dislikeButton.setOnAction(e -> dislikeArticle(headline));
        openLinkButton.setOnAction(e -> handleOpenArticle());

        // Use a TextArea for the description to handle long texts
        TextArea descriptionArea = new TextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);  // Enable word wrapping

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Text("Headline: " + headline),
                new Text("Authors: " + authors),
                new Text("Date: " + date),
                new Text("Link: " + currentArticleLink),  // Display the link
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
    private void handleOpenArticle() {
        if (currentArticleLink != null && !currentArticleLink.isEmpty()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(currentArticleLink);
                desktop.browse(uri);  // Opens the link in the default browser
            } catch (Exception e) {
                showError("Failed to open the link: " + e.getMessage());
            }
        } else {
            showError("No link available for this article.");
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


    private void updateUserProfileField(String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            showError(fieldName + " cannot be empty.");
            return;
        }

        Map<String, String> update = Map.of(fieldName, fieldValue);
        updateProfileFields(update);
    }

    @FXML
    private void handleUpdateUsernameAction() {
        updateUserProfileField("username", usernameField.getText());
    }

    @FXML
    private void handleUpdateEmailAction() {
        updateUserProfileField("email", emailField.getText());
    }

    @FXML
    private void handleUpdatePreferencesAction() {
        updateUserProfileField("preferences", preferencesField.getText());
    }


    private void updateProfileFields(Map<String, String> updatedFields) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("User_Details");

            Document userDocument = collection.find(new Document("username", currentUser)).first();

            if (userDocument != null) {
                for (Map.Entry<String, String> entry : updatedFields.entrySet()) {
                    String field = entry.getKey();
                    String newValue = entry.getValue();
                    if (newValue != null && !newValue.trim().isEmpty()) {
                        userDocument.put(field, newValue);
                    }
                }

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
        return null;
    }


    private void switchPane(Pane paneToShow) {
        for (Pane pane : List.of(profilePane, articlesPane, articleDetailsPane,likedArticlesPane,recommendationsPane)) {
            pane.setVisible(pane == paneToShow);
        }
    }
    @FXML
    private void handleLogOutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
    private void showInfo(String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }

    private void showError(String message) {
        System.out.println(message);
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

}

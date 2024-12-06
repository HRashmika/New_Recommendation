package org.coursework.new_recommendation.App;

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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.stage.Stage;
import org.bson.Document;
import org.coursework.new_recommendation.Services.ArticleProcess;
import org.coursework.new_recommendation.Model.ArticleType;
import org.coursework.new_recommendation.Database.MongoDBConnection;
import org.coursework.new_recommendation.Services.RecEngine;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UserOptions {
    @FXML
    private Button logoutbutton;
    @FXML
    private TextField usernameField, emailField, preferencesField;

    @FXML
    private TextField newPasswordField, confirmPasswordField;

    @FXML
    private Pane profilePane, articlesPane, articleDetailsPane, likedArticlesPane, recommendationsPane;
    @FXML
    private ListView<String> articlesListView, likedArticlesListView;
    @FXML
    private TableView<ArticleType> articlesTableView;
    @FXML
    private TableView<String> loginTimesTableView;

    @FXML
    private TableColumn<String, String> loginTimeColumn;

    private final ObservableList<String> likedCategories = FXCollections.observableArrayList();
    private final ObservableList<String> dislikedHeadlines = FXCollections.observableArrayList();
    private final ObservableList<String> likedHeadlines = FXCollections.observableArrayList();
    private final ObservableList<String> dislikedCategories = FXCollections.observableArrayList();
    private final ObservableList<String> openedCategories = FXCollections.observableArrayList();
    private final ObservableList<String> skippedCategories = FXCollections.observableArrayList();

    private boolean isDialogOpen = false;
    private String currentArticleLink;
    private String currentHeadline;

    private String currentUser;
    private final ArticleProcess articleProcess = new ArticleProcess();
    private Map<String, List<Document>> categorizedArticles;


    @FXML
    private TableColumn<ArticleType, String> headlineColumn;

    @FXML
    private TableColumn<ArticleType, String> categoryColumn;

    private RecEngine recommendationService;

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

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadUserDetails();
    }

    public void initialize() {
        MongoDatabase database = getMongoDatabase();
        recommendationService = new RecEngine();

        loginTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        // Add listener for the article selection
        articlesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleArticleClick(newValue);  // Showing the article details dialog when an article is clicked
        });
    }


    private MongoDatabase getMongoDatabase() {
        return MongoDBConnection.getDatabase();
    }

    // For category display
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

    // finding the article
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

    // Liking the article and removing the disliked article
    private void likeArticle(String headline) {
        if (!likedHeadlines.contains(headline)) {
            likedHeadlines.add(headline);
            dislikedHeadlines.remove(headline); // Remove from disliked if already disliked

            Document article = findArticleByHeadline(headline);
            if (article != null) {
                String category = article.getString("category");
                if (category != null) {
                    likedCategories.add(category);
                    new RecEngine().updateUserPreferences(currentUser, category, "like");
                } else {
                    System.out.println("Unknown category for article.");
                }
            }

            showInfo("You liked the article: " + headline);
        } else {
            showInfo("You already liked this article.");
        }
    }

    // Disliking the article and removing the liked article
    private void dislikeArticle(String headline) {
        if (!dislikedHeadlines.contains(headline)) {
            dislikedHeadlines.add(headline);
            likedHeadlines.remove(headline); // Remove from liked if already liked

            Document article = findArticleByHeadline(headline);
            if (article != null) {
                String category = article.getString("category");
                if (category != null) {
                    dislikedCategories.add(category);
                    new RecEngine().updateUserPreferences(currentUser, category, "dislike");
                } else {
                    System.out.println("Unknown category for article.");
                }
            }

            showInfo("You disliked the article: " + headline);
        } else {
            showInfo("You already disliked this article.");
        }
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

    private void handleCloseArticle(String headline, Dialog<Void> dialog) {
        // Find the article's category and process skipping
        Document article = findArticleByHeadline(headline);
        if (article != null) {
            String category = article.getString("category");
            if (category != null) {
                // Update the skipped category points in the database
                new RecEngine().updateUserPreferences(currentUser, category, "skip");

                if (!skippedCategories.contains(category)) {
                    skippedCategories.add(category);
                }
                showInfo("You skipped an article in the category: " + category);
            } else {
                showInfo("You skipped an article in an unknown category.");
            }
        } else {
            showError("Article not found.");
        }
        dialog.close();
    }

    @FXML
    private void handleOpenArticle() {
        if (currentArticleLink != null && !currentArticleLink.isEmpty()) {
            try {
                // Open the link in the browser
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(currentArticleLink);
                desktop.browse(uri);

                // Find the article by headline
                Document article = findArticleByHeadline(currentHeadline);
                if (article != null) {
                    String category = article.getString("category");
                    if (category != null) {
                        if (!openedCategories.contains(category)) {
                            openedCategories.add(category);
                            showInfo("You opened an article in the category: " + category);
                        }
                    } else {
                        showInfo("Article opened, but no category available.");
                    }
                } else {
                    showInfo("Article not found for the headline: " + currentHeadline);
                }

            } catch (URISyntaxException | IOException e) {
                // Handle URI and IO exceptions
                showError("Failed to open the link: " + e.getMessage());
            } catch (Exception e) {
                // Handle any other exceptions
                showError("Unexpected error: " + e.getMessage());
            }
        } else {
            // If link is null or empty, show an error
            showError("No link available for this article.");
        }
    }

    // Categorize the article
    @FXML
    private void handleArticlesButtonAction() {
        categorizedArticles = articleProcess.processArticles();
        switchPane(articlesPane);
        articlesListView.getItems().clear();
        articlesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleArticleClick(newValue);});
    }

    // On click viewing the article
    @FXML
    private void handleArticleClick(String selectedHeadline) {
        if (selectedHeadline != null && !isDialogOpen) {
            Document selectedArticle = findArticleByHeadline(selectedHeadline);
            if (selectedArticle != null) {
                boolean isLiked = likedHeadlines.contains(selectedHeadline); // Check if article is liked
                showArticleDialog(selectedArticle, isLiked);
            } else {
                showError("Selected article not found.");
            }
        } else {
            showError("No article selected.");
        }
    }
    // Handling the recommendations
    @FXML
    private void handleRecommendationsButtonAction() {
        if (currentUser == null) {
            showError("No user is logged in.");
            return;
        }

        // Create an instance of RecEngine and get the recommended articles
        RecEngine recEngine = new RecEngine();
        List<Document> recommendedArticles = recEngine.recommendArticles(currentUser);

        if (recommendedArticles.isEmpty()) {
            showInfo("No recommendations available based on your preferences.");
        } else {
            // Create a list to store the Article objects
            List<ArticleType> articles = new ArrayList<>();

            // Loop through recommended articles and create Article objects with only headline and category
            for (Document articleDoc : recommendedArticles) {
                String headline = articleDoc.getString("headline");
                String category = articleDoc.getString("category");

                if (headline != null && !headline.trim().isEmpty() && category != null) {
                    articles.add(new ArticleType(headline, category));
                }
            }

            ObservableList<ArticleType> observableArticles = FXCollections.observableArrayList(articles);

            headlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeadline()));
            categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

            // Update the TableView with the articles
            articlesTableView.setItems(observableArticles);

            // Switch to the recommendations pane
            switchPane(recommendationsPane);
        }
    }

    // If the user doesn't rate and go to like
    @FXML
    private void handleLikeArticleButtonAction() {
        switchPane(likedArticlesPane);
        likedArticlesListView.setItems(FXCollections.observableArrayList(likedHeadlines));
        if (likedHeadlines.isEmpty()) {
            showInfo("You haven't liked any articles yet.");
        }

        // Add listener for item selection in likedArticlesListView
        likedArticlesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleLikedArticleClick(newValue);
        });
    }
    // On click open the liked article
    private void handleLikedArticleClick(String selectedHeadline) {
        if (selectedHeadline != null && !isDialogOpen) {
            Document selectedArticle = findArticleByHeadline(selectedHeadline);
            if (selectedArticle != null) {
                boolean isLiked = true; // Articles in liked list are always liked
                showArticleDialog(selectedArticle, isLiked);
            } else {
                showError("Selected article not found.");
            }
        } else {
            showError("No article selected.");
        }
    }
    // Handling the user profile
    @FXML
    private void handleProfileButtonAction() {
        switchPane(profilePane);
        if (currentUser != null) {
            loadUserDetails();
        } else {
            showError("No user is currently logged in.");
        }
    }
    // Loading the user data
    private void loadUserDetails() {
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
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
                    displayLoginTimes(loginTimes);
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
    // Updating the profile fields
    @FXML
    private void handleUpdateAllFieldsAction() {

        String username = usernameField.getText();
        String email = emailField.getText();
        String preferences = preferencesField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                preferences == null || preferences.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Invalid email format. Please provide a valid email address.");
            return;
        }

        List<String> validCategories = Arrays.asList("Technology", "Sports", "Health", "Politics", "Weather", "Entertainment");
        String[] enteredPreferences = preferences.split(",");
        for (String category : enteredPreferences) {
            if (!validCategories.contains(category.trim())) {
                showError("Invalid category: " + category + ". Allowed categories are: " + String.join(", ", validCategories));
                return;
            }
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> collection = database.getCollection("User_Details");

            Document userDocument = collection.find(new Document("username", currentUser)).first();

            if (userDocument != null) {
                userDocument.put("username", username);
                userDocument.put("email", email);
                userDocument.put("preferences", preferences);
                userDocument.put("password", newPassword);

                collection.replaceOne(new Document("username", currentUser), userDocument);

                showInfo("Profile updated successfully.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/main.fxml"));
                AnchorPane mainPage = loader.load();
                Scene mainScene = new Scene(mainPage);
                Stage stage = (Stage) profilePane.getScene().getWindow();
                stage.setScene(mainScene);
                stage.show();
            } else {
                showError("User not found.");
            }
        } catch (Exception e) {
            showError("Failed to update profile: " + e.getMessage());
        }
    }

    private void displayLoginTimes(List<String> loginTimes) {
        ObservableList<String> loginTimesObservable = FXCollections.observableArrayList(loginTimes);
        loginTimesTableView.setItems(loginTimesObservable);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void switchPane(Pane paneToShow) {
        profilePane.setVisible(false);
        articlesPane.setVisible(false);
        articleDetailsPane.setVisible(false);
        likedArticlesPane.setVisible(false);
        recommendationsPane.setVisible(false);

        paneToShow.setVisible(true);
    }


    private void showArticleDialog(Document article, boolean isLiked) {
        if (isDialogOpen) return;  // Prevent opening a new dialog if one is already open
        isDialogOpen = true;

        currentHeadline = article.getString("headline");
        String description = article.getString("short description");
        String authors = article.getString("authors");
        String date = article.getString("date");
        currentArticleLink = article.getString("link");

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
        Button closeButton = new Button("Skip");

        // Disable buttons if the article is liked
        if (isLiked) {
            likeButton.setText("Liked");
            likeButton.setDisable(true);
            dislikeButton.setDisable(true);
            closeButton.setDisable(true);
            openLinkButton.setVisible(true);  // Only Open Full Article is visible if liked
        } else {
            openLinkButton.setVisible(true);  // Show Open Full Article regardless of like status
        }

        // Actions for the buttons
        likeButton.setOnAction(e -> {
            likeArticle(currentHeadline);
            likeButton.setText("Liked");
            likeButton.setDisable(true);  // Disable the like button after liking
        });

        dislikeButton.setOnAction(e -> dislikeArticle(currentHeadline));

        openLinkButton.setOnAction(e -> handleOpenArticle());

        closeButton.setOnAction(e -> {
            showInfo("You have skipped this article.");
            handleCloseArticle(currentHeadline, dialog);
        });

        TextArea descriptionArea = new TextArea(description);
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Text("Headline: " + currentHeadline),
                new Text("Authors: " + authors),
                new Text("Date: " + date),
                new Text("Link: " + currentArticleLink),
                new Label("Description:"),
                descriptionArea,
                likeButton,
                dislikeButton,
                openLinkButton,
                closeButton
        );

        dialogPane.setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Reset the dialog state when it's closed
        dialog.setOnHidden(e -> {
            isDialogOpen = false;
        });

        // Show the dialog
        dialog.showAndWait();
    }


    @FXML
    private void handleLogOutButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/coursework/new_recommendation/main.fxml"));
        AnchorPane loginPane = loader.load();
        Scene loginScene = new Scene(loginPane);
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(null);
        infoAlert.setContentText(message);
        infoAlert.showAndWait();
    }
}

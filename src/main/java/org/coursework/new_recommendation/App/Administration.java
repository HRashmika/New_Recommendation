package org.coursework.new_recommendation.App;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.bson.Document;
import org.coursework.new_recommendation.Services.ArticleProcess;
import org.coursework.new_recommendation.Model.ArticleType;
import org.coursework.new_recommendation.Model.User;
import org.coursework.new_recommendation.Database.MongoDBConnection;

import java.io.IOException;
import java.util.List;

public class Administration {

    @FXML
    private Button articleManagementButton;
    @FXML
    private Button logoutbutton;

    @FXML
    private TableView<ArticleType> articleTable;

    @FXML
    private Pane articleManagementPane;

    private ObservableList<ArticleType> articleList;

    @FXML
    private Button userManagementButton;

    @FXML
    private TableView<User> userTable;

    @FXML
    private Pane userManagementPane;

    private ObservableList<User> userList;

    @FXML
    private Pane profilePane;

    @FXML
    private TextField adminUsernameField;

    @FXML
    private TableView<String> adminLoginTable;

    @FXML
    private TableColumn<String, String> loginTimesCol;
    @FXML
    private Button deleteArticleButton;

    @FXML
    private Button profileButton;

    private String currentAdminUsername = "admin123";
    @FXML
    private Button addArticleButton;

    public void setCurrentAdminUsername(String adminUsername) {
        this.currentAdminUsername = adminUsername;
        loadAdminProfile();
    }

    // Initializing the methods
    @FXML
    public void initialize() {

        userManagementButton.setOnAction(event -> {
            loadUserDetails();
            userManagementPane.setVisible(true);
            articleManagementPane.setVisible(false);
            profilePane.setVisible(false);
        });

        articleManagementButton.setOnAction(event -> {
            loadCategorizedArticles();
            articleManagementPane.setVisible(true);
            userManagementPane.setVisible(false);
            profilePane.setVisible(false);
        });

        profileButton.setOnAction(event -> {
            loadAdminProfile();
            profilePane.setVisible(true);
            userManagementPane.setVisible(false);
            articleManagementPane.setVisible(false);
        });

        loginTimesCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        addArticleButton.setOnAction(event -> {
            handleAddArticle();
        });
        deleteArticleButton.setOnAction(event -> handleDeleteArticle());
    }
    // Loading the user details
    private void loadUserDetails() {
        userList = FXCollections.observableArrayList();
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> userDetailsCollection = database.getCollection("User_Details");
        MongoCollection<Document> userLoginCollection = database.getCollection("User_Login");

        for (Document userDoc : userDetailsCollection.find()) {
            String username = userDoc.get("username", String.class);
            String email = userDoc.get("email", String.class);
            String preferences = userDoc.get("preferences", String.class);

            if (username == null || email == null || preferences == null) {
                System.err.println("Missing required fields for user: " + username);
                continue;
            }

            Document loginDoc = userLoginCollection.find(new Document("username", username)).first();
            List<String> loginTimes = null;

            if (loginDoc != null) {
                loginTimes = loginDoc.getList("loginTimes", String.class);
            }
            userList.add(new User(username, email, preferences, loginTimes));
        }
        setupUserTable();
        userTable.setItems(userList);
    }

    private void setupUserTable() {

        userTable.getColumns().clear();

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> preferencesCol = new TableColumn<>("Preferences");
        preferencesCol.setCellValueFactory(new PropertyValueFactory<>("preferences"));

        TableColumn<User, String> loginTimeCol = new TableColumn<>("Login Times");
        loginTimeCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            List<String> times = user.getLoginTimes();
            // Here if the loginTimes are null,"N/A" is stored
            return new SimpleStringProperty(times != null ? String.join(", ", times) : "N/A");
        });

        userTable.getColumns().addAll(usernameCol, emailCol, preferencesCol, loginTimeCol);
    }

    // Loading the categorized articles from the database
    private void loadCategorizedArticles() {
        articleList = FXCollections.observableArrayList();
        // Only these categories are considered
        List<String> allowedCategories = List.of("sports", "health", "technology", "politics", "weather", "entertainment");

        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> articleCollection = database.getCollection("News_Articles");

        for (Document article : articleCollection.find()) {
            String headline = article.getString("headline");
            String shortDescription = article.getString("short description");
            String authors = article.getString("authors");
            String date = article.getString("date");
            String link = article.getString("link");
            String category = article.getString("category");

            if (allowedCategories.contains(category)) {
                articleList.add(new ArticleType(headline, shortDescription, authors, date, category, link));
            }
        }
        setupArticleTable();
        articleTable.setItems(articleList);
    }

    private void setupArticleTable() {
        articleTable.getColumns().clear();

        TableColumn<ArticleType, String> headlineCol = new TableColumn<>("Headline");
        headlineCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeadline()));

        TableColumn<ArticleType, String> shortDescriptionCol = new TableColumn<>("Short Description");
        shortDescriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShortDescription()));

        TableColumn<ArticleType, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthors()));

        TableColumn<ArticleType, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));

        TableColumn<ArticleType, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<ArticleType, String> linkCol = new TableColumn<>("Link");
        linkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLink()));

        articleTable.getColumns().addAll(headlineCol, shortDescriptionCol, authorsCol, dateCol, categoryCol, linkCol);
    }

    // Method for adding the articles
    @FXML
    private void handleAddArticle() {
        Dialog<ArticleType> dialog = new Dialog<>();
        dialog.setTitle("Add New Article");
        dialog.setHeaderText("Please fill in the details for the new article");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextField headlineField = new TextField();
        headlineField.setPromptText("Headline");
        TextField shortDescriptionField = new TextField();
        shortDescriptionField.setPromptText("Short Description");
        TextField authorsField = new TextField();
        authorsField.setPromptText("Authors");
        TextField dateField = new TextField();
        dateField.setPromptText("Date");
        TextField linkField = new TextField();
        linkField.setPromptText("Link");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Headline:"), 0, 0);
        grid.add(headlineField, 1, 0);
        grid.add(new Label("Short Description:"), 0, 1);
        grid.add(shortDescriptionField, 1, 1);
        grid.add(new Label("Authors:"), 0, 2);
        grid.add(authorsField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(dateField, 1, 3);
        grid.add(new Label("Link:"), 0, 4);
        grid.add(linkField, 1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String headline = headlineField.getText();
                String shortDescription = shortDescriptionField.getText();
                String authors = authorsField.getText();
                String date = dateField.getText();
                String link = linkField.getText();

                ArticleType article = new ArticleType(headline, shortDescription, authors, date, "", link);

                ArticleProcess articleProcess = new ArticleProcess();
                // Categorizing the article using the key word extraction
                List<String> keywords = articleProcess.extractKeywords(headline);
                String category = articleProcess.categorizeKeywords(keywords);

                article = new ArticleType(headline, shortDescription, authors, date, category, link);
                return article;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(article -> {
            articleList.add(0, article);
            articleTable.refresh();
            // Calling the method to save the article to the database
            saveArticleToDatabase(article);
            loadCategorizedArticles();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Article added successfully!");
            alert.showAndWait();
        });
    }

    // Method to delete the articles
    @FXML
    private void handleDeleteArticle() {
        // Taking the selected article to delete
        ArticleType selectedArticle = articleTable.getSelectionModel().getSelectedItem();

        if (selectedArticle != null) {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> articleCollection = database.getCollection("News_Articles");

            // Deleting the selected article
            articleCollection.deleteOne(new Document("headline", selectedArticle.getHeadline()));
            loadCategorizedArticles();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Article deleted successfully!");
            alert.showAndWait();
        } else {
            showAlert(Alert.AlertType.WARNING, "No article selected", "Please select an article to delete.");
        }
    }

    // Saving the article to the database, used in  the add articles
    private void saveArticleToDatabase(ArticleType article) {
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> articleCollection = database.getCollection("News_Articles");

            Document articleDoc = new Document()
                    .append("headline", article.getHeadline())
                    .append("short description", article.getShortDescription())
                    .append("authors", article.getAuthors())
                    .append("date", article.getDate())
                    .append("category", article.getCategory())
                    .append("link", article.getLink());

            // Article inserted to the mongoDB
            articleCollection.insertOne(articleDoc);
            System.out.println("Article added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error adding article to database.");
        }
    }

    // Loading the profile of the administrator
    private void loadAdminProfile() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> adminCollection = database.getCollection("Admin_Login");
        Document adminDoc = adminCollection.find(new Document("Admin ID", currentAdminUsername)).first();

        if (adminDoc != null) {
            adminUsernameField.setText(currentAdminUsername);
            List<String> loginTimes = adminDoc.getList("loginTimes", String.class);
            // LoginTimes are saved in a list
            ObservableList<String> loginTimesList = FXCollections.observableArrayList(loginTimes);
            adminLoginTable.setItems(loginTimesList);
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Login out of the administration
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
}
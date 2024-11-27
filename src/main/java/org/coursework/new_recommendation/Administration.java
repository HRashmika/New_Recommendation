package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public class Administration {

    @FXML
    private Button articleManagementButton;

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
    private Button profileButton;

    private String currentAdminUsername = "admin123";

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
    }

    private void loadAdminProfile() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> adminCollection = database.getCollection("Admin_Login");

            Document adminDoc = adminCollection.find(new Document("Admin ID", currentAdminUsername)).first();

            if (adminDoc != null) {
                adminUsernameField.setText(currentAdminUsername);

                List<String> loginTimes = adminDoc.getList("loginTimes", String.class);
                ObservableList<String> loginTimesList = FXCollections.observableArrayList(loginTimes);
                adminLoginTable.setItems(loginTimesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setCurrentAdminUsername(String adminUsername) {
        this.currentAdminUsername = adminUsername;
        loadAdminProfile(); // Automatically load the profile when set
    }


    private void loadUserDetails() {
        userList = FXCollections.observableArrayList();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> userDetailsCollection = database.getCollection("User_Details");
            MongoCollection<Document> userLoginCollection = database.getCollection("User_Login");

            for (Document userDoc : userDetailsCollection.find()) {
                String username = userDoc.getString("username");
                String email = userDoc.getString("email");
                String preferences = userDoc.getString("preferences");

                Document loginDoc = userLoginCollection.find(new Document("username", username)).first();
                List<String> loginTimes = null;

                if (loginDoc != null && loginDoc.containsKey("loginTimes")) {
                    loginTimes = loginDoc.getList("loginTimes", String.class);
                }

                userList.add(new User(username, email, preferences, loginTimes));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            List<String> times = user.getLoginTime();
            return new SimpleStringProperty(times != null ? String.join(", ", times) : "N/A");
        });

        userTable.getColumns().addAll(usernameCol, emailCol, preferencesCol, loginTimeCol);
    }
    private void loadCategorizedArticles() {
        articleList = FXCollections.observableArrayList();

        ArticleProcess articleProcess = new ArticleProcess();
        Map<String, List<Document>> categorizedArticles = articleProcess.processArticles();

        categorizedArticles.forEach((category, articles) -> {
            for (Document article : articles) {
                String heading = article.getString("Heading");
                String shortDescription = article.getString("short description");
                String authors = article.getString("authors");
                String date = article.getString("date"); // Assuming "date" exists in MongoDB
                String link = article.getString("link");

                articleList.add(new ArticleType(heading, shortDescription, authors, date, category, link));
            }
        });

        setupArticleTable();
        articleTable.setItems(articleList);
    }

    private void setupArticleTable() {
        articleTable.getColumns().clear();

        TableColumn<ArticleType, String> headingCol = new TableColumn<>("Heading");
        headingCol.setCellValueFactory(new PropertyValueFactory<>("heading"));

        TableColumn<ArticleType, String> shortDescriptionCol = new TableColumn<>("Short Description");
        shortDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));

        TableColumn<ArticleType, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(new PropertyValueFactory<>("authors"));

        TableColumn<ArticleType, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ArticleType, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<ArticleType, String> linkCol = new TableColumn<>("Link");
        linkCol.setCellValueFactory(new PropertyValueFactory<>("link"));

        articleTable.getColumns().addAll(headingCol, shortDescriptionCol, authorsCol, dateCol, categoryCol, linkCol);
    }

}

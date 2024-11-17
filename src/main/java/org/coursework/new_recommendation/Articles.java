package org.coursework.new_recommendation;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.bson.Document;

import java.util.List;

public class Articles {

    @FXML
    private TextArea articlesTextArea;  // For displaying general info about articles (optional)
    @FXML
    private ListView<String> articlesListView;  // For displaying articles under each category

    private final ArticleAPI fetcher = new ArticleAPI();
    private final ArticleStore storage = new ArticleStore();
    private final ArticleProcess processor = new ArticleProcess();

    // Fetch and categorize articles at startup
    @FXML
    public void initialize() {
        fetchAndCategorizeArticles(); // Automatically fetch and categorize articles on startup
    }

    // Fetch articles, categorize them, and save to DB
    private void fetchAndCategorizeArticles() {
        try {
            // Example categories: Technology, Health, Sports, AI
            for (String category : new String[]{"technology", "health", "sports", "ai"}) {
                List<String> articles = fetcher.fetchArticles(category);  // Fetch articles for each category
                for (String article : articles) {
                    String categoryAssigned = processor.categorizeArticle(article);  // Categorize article
                    storage.saveArticles(List.of(article), categoryAssigned);  // Save articles with category in DB
                    // You can also append to TextArea if needed, but it isn't necessary for ListView
                    // articlesTextArea.appendText("Article: " + article + "\nCategory: " + categoryAssigned + "\n\n");
                }
            }
        } catch (Exception e) {
            articlesTextArea.appendText("Error: " + e.getMessage() + "\n");
        }
    }

    // Load articles by category and display them in ListView
    private void loadArticlesByCategory(String category) {
        articlesListView.getItems().clear();  // Clear previous list items

        // Fetch all articles from the database
        List<Document> allArticles = storage.fetchAllArticles();

        // Iterate over the articles and filter by category
        for (Document doc : allArticles) {
            String title = doc.getString("title");
            String articleCategory = doc.getString("category");

            // Add only articles from the selected category
            if (articleCategory.equalsIgnoreCase(category)) {
                articlesListView.getItems().add(title);  // Add article title to ListView
            }
        }
    }

    // Show Technology Articles
    @FXML
    public void showTechnologyArticles() {
        loadArticlesByCategory("technology");
    }

    // Show Health Articles
    @FXML
    public void showHealthArticles() {
        loadArticlesByCategory("health");
    }

    // Show Sports Articles
    @FXML
    public void showSportsArticles() {
        loadArticlesByCategory("sports");
    }

    // Show AI Articles
    @FXML
    public void showAIArticles() {
        loadArticlesByCategory("ai");
    }
}

package org.coursework.new_recommendation;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

public class Recommendation extends Score {
    private MongoDatabase database;

    public Recommendation(MongoDatabase database) {
        super();
        this.database = database;
    }

    public void assignInitialScores(String username) {
        MongoCollection<Document> userCollection = database.getCollection("User_Details");
        Document user = userCollection.find(new Document("username", username)).first();

        if (user != null) {
            String preferences = user.getString("preferences");
            System.out.println("User preferences: " + preferences); // Debugging line
            if (preferences != null) {
                String[] preferredCategories = preferences.split(", ");
                for (String category : preferredCategories) {
                    updateScore(category, 10); // Assign 10 points for each preference
                }
            }
        }
    }


    public List<Document> getRecommendedArticles(String username) {
        assignInitialScores(username);
        MongoCollection<Document> articleCollection = database.getCollection("News_Articles");
        List<Document> recommendedArticles = new ArrayList<>();

        FindIterable<Document> articles = articleCollection.find();
        System.out.println("Fetched articles count: " + articles.spliterator().estimateSize()); // Debugging line

        // Filter and prioritize articles based on the scores of their categories
        Map<String, Integer> categoryScores = getCategoryScores();
        for (Document article : articles) {
            String headline = article.getString("headline");
            String category = article.getString("category");

            if (headline != null && category != null && categoryScores.containsKey(category)) {
                int score = categoryScores.get(category);
                // Add the article with a score (used for sorting later)
                article.append("score", score);
                recommendedArticles.add(article);

                // Debugging line to print articles(Optional)
                System.out.println("Article: " + headline + ", Category: " + category + ", Score: " + score);
            }
        }

        recommendedArticles.sort((a, b) -> Integer.compare((Integer) b.get("score"), (Integer) a.get("score")));

        return recommendedArticles;
    }

}

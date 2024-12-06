package org.coursework.new_recommendation.Services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.coursework.new_recommendation.Database.MongoDBConnection;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RecEngine {

    private final MongoCollection<Document> preferencesCollection;
    private final MongoCollection<Document> articlesCollection;
    private final ExecutorService executorService;

    public RecEngine() {
        this.preferencesCollection = MongoDBConnection.getDatabase().getCollection("Preferences");
        this.articlesCollection = MongoDBConnection.getDatabase().getCollection("News_Articles");
        this.executorService = Executors.newFixedThreadPool(4);  // Using a fixed thread pool of 4 threads
    }

    public List<Document> recommendArticles(String username) {
        List<Document> recommendedArticles = new ArrayList<>();

        try {
            // Getting the user preferences
            Document preferencesDoc = preferencesCollection.find(new Document("username", username)).first();

            if (preferencesDoc == null) {
                throw new Exception("User preferences not found.");
            }

            // Getting scores from Preferences collection
            Map<String, Integer> categoryScores = new HashMap<>();
            for (String category : Arrays.asList("Technology", "Sports", "Health", "Politics", "Weather", "Entertainment")) {
                categoryScores.put(category, preferencesDoc.getInteger(category, 0));
            }

            // Using Future to collect results in parallel
            List<Future<List<Document>>> futures = new ArrayList<>();

            // Iterate through each category and recommend articles based on the score
            for (Map.Entry<String, Integer> entry : categoryScores.entrySet()) {
                String category = entry.getKey();
                int score = entry.getValue();

                // If the score is positive, recommend articles for this category
                if (score > 0) {
                    futures.add(executorService.submit(() -> getArticlesFromCategory(category, score)));
                }
            }

            // Collect all results once tasks are completed
            for (Future<List<Document>> future : futures) {
                recommendedArticles.addAll(future.get()); // Blocking until all tasks are complete
            }

        } catch (Exception e) {
            System.err.println("Error recommending articles: " + e.getMessage());
        }

        return recommendedArticles;
    }

    private List<Document> getArticlesFromCategory(String category, int limit) {
        List<Document> articles = new ArrayList<>();

        try {
            category = category.toLowerCase(); // Convert to lowercase to match MongoDB stored values

            // Find articles from the specified category and sort them by date
            articles = articlesCollection.find(Filters.eq("category", category))
                    .sort(Sorts.descending("date"))
                    .limit(limit)
                    .into(new ArrayList<>());

        } catch (Exception e) {
            System.err.println("Error retrieving articles from category " + category + ": " + e.getMessage());
        }

        // Ensure articles without an author are still included
        for (Document article : articles) {
            if (article.get("authors") == null || article.get("authors").toString().isEmpty()) {
                article.put("authors", "Unknown Author");
            }
        }

        return articles;
    }

    public void updateUserPreferences(String username, String category, String action) {
        try {
            int pointChange = switch (action.toLowerCase()) {
                case "like" -> 5;
                case "dislike" -> -5;
                case "skip" -> -3;
                default -> throw new IllegalArgumentException("Invalid action: " + action);
            };

            // Normalize category to match the case in the database
            category = category.substring(0, 1).toUpperCase() + category.substring(1).toLowerCase();

            // Create final copies of variables to use inside the lambda expression
            final String finalUsername = username;
            final String finalCategory = category;

            // Update the points in the database asynchronously
            executorService.submit(() -> {
                try {
                    preferencesCollection.updateOne(
                            new Document("username", finalUsername),
                            Updates.inc(finalCategory, pointChange)
                    );
                } catch (Exception e) {
                    System.err.println("Error updating preferences: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Error processing action: " + e.getMessage());
        }
    }


    // Optionally, you can implement a method to shut down the executor service
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

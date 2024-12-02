package org.coursework.new_recommendation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

public class Recommendation {
    private final MongoDatabase database;

    public Recommendation(MongoDatabase database) {
        this.database = database;
    }

    private String fetchUserPreferences(String username) {
        MongoCollection<Document> userDetailsCollection = database.getCollection("User_Details");
        Document user = userDetailsCollection.find(Filters.eq("username", username)).first();

        if (user != null) {
            return user.getString("preferences");
        } else {
            throw new IllegalArgumentException("Invalid username.");
        }
    }

    // Parse preferences into a list
    private List<String> parsePreferences(String preferences) {
        return Arrays.stream(preferences.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private List<ArticleType> fetchArticles() {
        List<ArticleType> articles = new ArrayList<>();
        MongoCollection<Document> articlesCollection = database.getCollection("News_Articles");

        for (Document doc : articlesCollection.find()) {
            articles.add(new ArticleType(
                    doc.getString("headline"),
                    doc.getString("shortDescription"),
                    doc.getString("authors"),
                    doc.getString("date"),
                    doc.getString("category"),
                    doc.getString("link")
            ));
        }
        return articles;
    }

    public List<ArticleType> recommendArticles(String username) {
        String preferences = fetchUserPreferences(username);
        System.out.println("User Preferences: " + preferences);

        List<String> parsedPreferences = parsePreferences(preferences);
        System.out.println("Parsed Preferences: " + parsedPreferences);

        List<ArticleType> articles = fetchArticles();
        System.out.println("Total Articles: " + articles.size());

        List<ArticleType> filteredArticles = articles.stream()
                .filter(article -> parsedPreferences.stream()
                        .anyMatch(pref -> article.getCategory().trim().equalsIgnoreCase(pref.trim())))
                .collect(Collectors.toList());

        System.out.println("Filtered Articles: " + filteredArticles.size());

        Map<String, List<ArticleType>> groupedByCategory = filteredArticles.stream()
                .collect(Collectors.groupingBy(ArticleType::getCategory));

        List<ArticleType> randomArticles = new ArrayList<>();
        for (String category : groupedByCategory.keySet()) {
            List<ArticleType> articlesInCategory = groupedByCategory.get(category);
            int size = articlesInCategory.size();
            int numberOfArticlesToPick = Math.min(5, size);

            Set<Integer> selectedIndexes = new HashSet<>();
            while (selectedIndexes.size() < numberOfArticlesToPick) {
                selectedIndexes.add(ThreadLocalRandom.current().nextInt(size));
            }

            selectedIndexes.forEach(index -> randomArticles.add(articlesInCategory.get(index)));
        }

        randomArticles.forEach(article -> {
            System.out.println("Selected Article: " + article.getHeadline() + " | Category: " + article.getCategory());
        });

        return randomArticles;
    }
}

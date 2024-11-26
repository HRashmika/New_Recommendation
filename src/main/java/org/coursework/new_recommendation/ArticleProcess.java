package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArticleProcess{

    private static final List<String> STOPWORDS = Arrays.asList(
            "a", "an", "and", "the", "is", "in", "on", "of", "to", "with", "for", "this", "that", "it"
    );

    public List<String> extractKeywords(String heading) {
        if (heading == null || heading.isEmpty()) return Collections.emptyList();

        // Tokenize the heading (Using NLP concepts)
        String[] words = heading.toLowerCase().split("\\s+");

        return Arrays.stream(words)
                .map(word -> word.replaceAll("[^a-zA-Z0-9]", "")) // Remove punctuation
                .filter(word -> !word.isEmpty() && !STOPWORDS.contains(word)) // Remove stopwords
                .collect(Collectors.toList());
    }

    public String categorizeKeywords(List<String> keywords) {
        Map<String, String> categories = new HashMap<>();
        categories.put("sports", "football basketball cricket tennis");
        categories.put("technology", "ai machine-learning programming computers software");
        categories.put("health", "medicine fitness wellness health disease");
        categories.put("politics", "election government policy politics");
        categories.put("weather", "rain cloudy heat humidity climate weather temperature");
        categories.put("finance", "money stock finance bank");

        for (String keyword : keywords) {
            for (Map.Entry<String, String> entry : categories.entrySet()) {
                if (entry.getValue().contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "uncategorized"; // Default if no match found
    }


    public Map<String, List<Document>> processArticles() {
        Map<String, List<Document>> categorizedArticles = new HashMap<>();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("News_Articles");

            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document article = cursor.next();
                    String heading = article.getString("Heading");

                    List<String> keywords = extractKeywords(heading);
                    String category = categorizeKeywords(keywords);


                    categorizedArticles
                            .computeIfAbsent(category, k -> new ArrayList<>())
                            .add(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorizedArticles;
    }
}

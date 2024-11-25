package org.coursework.new_recommendation;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.*;

public class ArticleProcess {


    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>() {{
        put("Technology", Arrays.asList("AI", "software", "technology", "innovation", "cloud", "power"));
        put("Sports", Arrays.asList("football", "cricket", "match", "record", "game", "team"));
        put("Health", Arrays.asList("medicine", "health", "fitness", "nutrition", "diet", "hospital"));
        put("Finance", Arrays.asList("stocks", "finance", "investment", "market", "economy", "budget"));
    }};

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
        MongoCollection<Document> collection = database.getCollection("News Articles");

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document article = cursor.next();
                String articleContent = article.getString("Article");
                String heading = article.getString("Heading");

                // Skip articles with missing content
                if ((articleContent == null || articleContent.trim().isEmpty()) &&
                        (heading == null || heading.trim().isEmpty())) {
                    System.out.println("Skipping article with ID: " + article.get("_id") + " due to missing content.");
                    continue;
                }


                String category = classifyArticle(articleContent, heading);
                if (category != null) {
                    article.put("Category", category);
                    collection.replaceOne(new Document("_id", article.get("_id")), article);
                    System.out.println("Classified article with ID: " + article.get("_id") + " as " + category);
                } else {
                    System.out.println("Could not classify article with ID: " + article.get("_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

    // Method to classify an article based on keyword matching
    private static String classifyArticle(String content, String heading) {
        String combinedContent = ((content != null ? content : "") + " " + (heading != null ? heading : "")).toLowerCase();
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            for (String keyword : entry.getValue()) {
                if (combinedContent.contains(keyword.toLowerCase())) {
                    return category;
                }
            }
        }
        return "Uncategorized";
    }
}

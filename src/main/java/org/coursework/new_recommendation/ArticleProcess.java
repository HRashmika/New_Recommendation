package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class ArticleProcess {

    // Stopwords list
    private static final List<String> STOPWORDS = Arrays.asList(
            "a", "an", "and", "the", "is", "in", "on", "of", "to", "with", "for", "this", "that", "it"
    );

    // Extract keywords from the heading by removing stopwords and punctuation
    public List<String> extractKeywords(String heading) {
        if (heading == null || heading.isEmpty()) return Collections.emptyList();

        // Tokenize the heading (Using basic string splitting)
        String[] words = heading.toLowerCase().split("\\s+");

        return Arrays.stream(words)
                .map(word -> word.replaceAll("[^a-zA-Z0-9]", "")) // Remove punctuation
                .filter(word -> !word.isEmpty() && !STOPWORDS.contains(word)) // Remove stopwords
                .collect(Collectors.toList());
    }

    // Categorize the keywords into categories
    public String categorizeKeywords(List<String> keywords) {
        Map<String, String> categories = new HashMap<>();
        categories.put("sports", "football basketball cricket tennis rugby baseball athletics swimming cycling volleyball Olympics boxing golf wrestling soccer motor-racing skateboarding gymnastics badminton extreme-sports sports-injuries");
        categories.put("technology", "ai machine-learning programming computers software robotics blockchain virtual-reality augmented-reality internet-of-things cybersecurity big-data cloud-computing machine-vision computer-science programming-languages artificial-intelligence data-science quantum-computing software-engineering mobile-apps smart-devices tech-startups automation");
        categories.put("health", "medicine fitness wellness health disease nutrition mental-health healthcare physical-therapy diseases vaccinations pandemics obesity heart-disease diabetes cancer mental-wellness mindfulness weight-loss sleep diet skincare immunity prescription");
        categories.put("politics", "election government policy politics democracy elections governance parliament political-parties political-leaders diplomacy international-relations public-policy civil-rights political-activism political-reform global-issues political-ideologies climate-change-policy tax-policy social-justice");
        categories.put("weather", "rain cloudy heat humidity climate weather temperature storms hurricanes typhoons tornadoes floods drought global-warming climate-change snowfall wind-speed heatwave meteorology atmospheric-pressure sea-level-rise monsoon");
        categories.put("entertainment", "movies music tv-shows video-games celebrities streaming-services pop-culture theater concerts film-reviews awards gaming comedy drama animation series documentaries music-industry fashion film-industry cultural-events e-sports");

        // Loop through keywords and check for matching category
        for (String keyword : keywords) {
            for (Map.Entry<String, String> entry : categories.entrySet()) {
                if (entry.getValue().contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "uncategorized"; // Default category
    }

    // Process articles from MongoDB and categorize them
    public Map<String, List<Document>> processArticles() {
        Map<String, List<Document>> categorizedArticles = new HashMap<>();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
            MongoCollection<Document> collection = database.getCollection("News_Articles");

            try (MongoCursor<Document> cursor = collection.find().iterator()) {

                while (cursor.hasNext()) {
                    Document article = cursor.next();
                    String heading = article.getString("headline"); // Use "headline" instead of "Heading"

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

    public static void main(String[] args) {
        ArticleProcess process = new ArticleProcess();
        Map<String, List<Document>> categorizedArticles = process.processArticles();

        categorizedArticles.forEach((category, articles) -> {
            System.out.println("Category: " + category);
            articles.forEach(article -> System.out.println(article.toJson()));
        });
    }
}

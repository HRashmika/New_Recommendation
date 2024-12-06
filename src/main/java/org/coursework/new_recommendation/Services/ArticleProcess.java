package org.coursework.new_recommendation.Services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.coursework.new_recommendation.Database.MongoDBConnection;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ArticleProcess {

    // Stopwords list
    private static final List<String> STOPWORDS = Arrays.asList(
            "a", "an", "and", "the", "is", "in", "on", "of", "to", "with", "for", "this", "that", "it"
    );

    private final ExecutorService executorService;

    public ArticleProcess() {
        this.executorService = Executors.newFixedThreadPool(4);  // Use a fixed thread pool with 4 threads
    }

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

    public String categorizeKeywords(List<String> keywords) {
        Map<String, String> categories = new HashMap<>();
        categories.put("technology", "Payment-Processor Visa Sales Categorization Processor Technology Data Processing Payment System Technology-Innovation Payment-Tech Cybersecurity Digital-Transactions Online-Payment E-Commerce Tech-News Meta Instagram Fined Regulators Privacy Social-Media Data-Breach Digital-Rights Cyberattack Los-Angeles School District Security System NASA Moon Rocket Fuel Leak Launch Delay Amazon Blade-Runner Ridley-Scott AI TikTok Machine-Learning Big-Data Cloud-Computing Blockchain 5G Automation IoT AR VR Artificial-Intelligence Robotics Tech-Startups Venture-Capital AI-Ethics Cybersecurity-Threats Data-Privacy");
        categories.put("sports", "Sports Football Basketball Tennis Rugby Baseball Athletics Swimming Cycling Volleyball Olympics Boxing Golf Wrestling Soccer Motor-Racing Skateboarding Gymnastics Badminton Extreme-Sports Sports-Injuries Games Match Team Players Competition Rally Fan Sports-News Baseball-Union AFL-CIO Solidarity MVP Shohei-Ohtani Golf LIV Tour PGA Kobe-Bryant Crash Photos Lawsuit Carlos-Alcaraz U.S. Open NFL Fan Fight Doping World-Records Esports Olympic-Gold Winter-Olympics World-Cup Super-Bowl Football-Players Athlete-Performance Injuries Training Sports-Scandals Fantasy-Sports");
        categories.put("health", "Health Medical Health-Care Mental-Health Wellness Disease Treatment Surgery Emergency Healthcare-Medical Healthcare-News Racism Water-Crisis Mississippi Health-Safety Flooding Heatwave Swimming-Pool Bear Wildfires Heat-Stroke Fertility Treatment Debt Compassion-Fatigue Mental-Health CDC Director Recovery Anxiety COVID Omicron Booster Vaccine LGBTQ Monkeypox Chest-Pain Health-Safety Hospital ICU Drug-Addiction Heart-Disease Cancer Obesity Diabetes Mental-Illness Wellness-Journey Health-Tips Diet-Exercise Fitness-Motivation Public-Health Stress-Management");
        categories.put("politics", "Politics Government Senator Supreme-Court Election Democracy Laws Policy Congress Public-Policy Gerrymandering Rights Political-News Political-Commentary Biden Administration Violence-Morality Midterms Voting-Rights Democracy-In-Crisis Brittney-Griner Paul-Whelan 9/11 Terrorism Russia Ukraine War NATO Biden-Politics Veterans Health Bill Abortion-Restrictions Tokyo-Court Voting-Rights Campaign Finance Health-Bill Taiwan Russia UN January-6 Secret-Service Trump Wisconsin Gubernatorial Pitchforks Political-Reform Presidential-Race Foreign-Policy Congress-Bills Tax-Reform Civil-Rights Campaigns Voter-Suppression Social-Justice Political-Activism");
        categories.put("weather", "Weather FEMA Jackson Clean-Water Storm Flood Hurricane Disaster Preparedness Environment Climate Global-Warming Weather-News Natural-Disasters Hurricane Fiona Tropical-Storm Landslides Flash-Flooding California Heatwave Severe-Winds Flooding Recovery Flooding-Pakistan Disaster Relief Monsoon Drought Extreme-Weather Weather-Warnings Temperature-Rise Weather-Impact Environment-Impact Climate-Change Heatwaves Wildfires Forest-Fires Severe-Storms Polar-Vortex Hurricane-Season");
        categories.put("entertainment", "Entertainment Queen Movie Celebrity Movies Music TV Shows Awards Gossip Actor Actress Performance Drama Comedy News Stars Reality-Shows Pop-Culture Celebrity-News Yung-Gravy Addison-Rae VMA Johnny-Depp Moon-Person MTV-VMA Brad-Pitt Bullet-Train Chorizo-Star Dragon-Ball Super Beast Movie Box-Office Celebrity-Appearance Johnny-Depp Viola-Davis Kim-Kardashian Steve-Martin SNL Cardi-B Fundraising Beyonce JoJo Siwa GLSEN Honor LGBTQ Oscars Film-Festival Indie-Films Red-Carpet Celebrity-Scandals Streaming-Movies Music-Awards Chart-Toppers TV-Episodes Music-Genre Broadway");

        // Convert keywords to lowercase for case-insensitive matching
        Set<String> normalizedKeywords = keywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Loop through keywords and check for matching category
        for (String keyword : normalizedKeywords) {
            for (Map.Entry<String, String> entry : categories.entrySet()) {
                // Check for exact match or partial match within category terms
                if (entry.getValue().toLowerCase().contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "uncategorized";
    }

    public Map<String, List<Document>> processArticles() {
        Map<String, List<Document>> categorizedArticles = new ConcurrentHashMap<>(); // Thread-safe map

        try {
            MongoDatabase database = MongoDBConnection.getDatabase(); // Use the MongoDBConnection class
            MongoCollection<Document> collection = database.getCollection("News_Articles");

            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                List<Future<Void>> futures = new ArrayList<>();

                // Process each article concurrently
                while (cursor.hasNext()) {
                    Document article = cursor.next();
                    futures.add(executorService.submit(() -> {
                        String heading = article.getString("headline");
                        List<String> keywords = extractKeywords(heading);
                        String category = categorizeKeywords(keywords);

                        // Add article to appropriate category in a thread-safe manner
                        categorizedArticles
                                .computeIfAbsent(category, k -> new ArrayList<>())
                                .add(article);
                        return null; // Return null as we are not using the result
                    }));
                }

                // Wait for all tasks to complete
                for (Future<Void> future : futures) {
                    future.get();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorizedArticles;
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

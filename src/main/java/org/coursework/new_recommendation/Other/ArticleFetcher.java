package org.coursework.new_recommendation.Other;

import com.mongodb.client.*;
import org.bson.Document;
import org.coursework.new_recommendation.Database.MongoDBConnection;
import org.coursework.new_recommendation.Services.ArticleProcess;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArticleFetcher extends ArticleProcess {

    public static void main(String[] args) {
        String jsonFile = "src/main/resources/DataSet/News_Articles.json";

        // Get the database connection using MongoDBConnection Singleton
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("News_Articles");

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(jsonFile))) {
            String line;
            List<Document> documents = new ArrayList<>();
            int articleCount = 0;  // Counter to track the number of articles processed

            ArticleFetcher jsonProcessor = new ArticleFetcher(); // Create an instance
            // Here only 500 articles are considered
            while ((line = reader.readLine()) != null && articleCount < 500) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                JSONObject jsonObject = new JSONObject(line);

                String link = jsonObject.optString("link");
                String headline = jsonObject.optString("headline");
                String shortDescription = jsonObject.optString("short_description");
                String authors = jsonObject.optString("authors");
                String date = jsonObject.optString("date");

                // Extract the keywords
                List<String> keywords = jsonProcessor.extractKeywords(headline);
                String category = jsonProcessor.categorizeKeywords(keywords);

                Document doc = new Document("link", link)
                        .append("headline", headline)
                        .append("short description", shortDescription)
                        .append("authors", authors)
                        .append("date", date)
                        .append("category", category);

                documents.add(doc);
                articleCount++;

                if (articleCount >= 500) {
                    break;
                }
            }

            if (!documents.isEmpty()) {
                collection.insertMany(documents);
                System.out.println("Data inserted successfully with categories. Total articles: " + articleCount);
            } else {
                System.out.println("No data to insert.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        } finally {
            // Close MongoDB connection through the singleton class
            MongoDBConnection.closeConnection();
        }
    }
}

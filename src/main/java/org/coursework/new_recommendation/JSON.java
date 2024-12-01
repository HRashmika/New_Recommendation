package org.coursework.new_recommendation;

import com.mongodb.client.*;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSON extends ArticleProcess {

    public static void main(String[] args) {
        String jsonFile = "src/main/resources/Data_CSV/News_Articles.json";

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
        MongoCollection<Document> collection = database.getCollection("News_Articles");

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(jsonFile))) {
            String line;
            List<Document> documents = new ArrayList<>();
            int articleCount = 0;  // Counter to track the number of articles processed

            JSON jsonProcessor = new JSON(); // Create an instance of the derived class

            while ((line = reader.readLine()) != null && articleCount < 500) {  // Limit to 500 articles
                if (line.trim().isEmpty()) {
                    continue;
                }

                JSONObject jsonObject = new JSONObject(line);

                String link = jsonObject.optString("link");
                String headline = jsonObject.optString("headline");
                String shortDescription = jsonObject.optString("short_description");
                String authors = jsonObject.optString("authors");
                String date = jsonObject.optString("date");

                List<String> keywords = jsonProcessor.extractKeywords(headline);
                String category = jsonProcessor.categorizeKeywords(keywords);

                Document doc = new Document("link", link)
                        .append("headline", headline)
                        .append("short description", shortDescription)
                        .append("authors", authors)
                        .append("date", date)
                        .append("category", category); // Add category field

                documents.add(doc);
                articleCount++;  // Increment the counter

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
            mongoClient.close();
        }
    }
}

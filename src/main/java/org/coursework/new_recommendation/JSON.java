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

public class JSON {
    public static void main(String[] args) {
        String jsonFile = "src/main/resources/Data_CSV/News_Articles.json";

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
        MongoCollection<Document> collection = database.getCollection("News_Articles");

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(jsonFile))) {
            String line;
            List<Document> documents = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                JSONObject jsonObject = new JSONObject(line);

                String link = jsonObject.optString("link");
                String headline = jsonObject.optString("headline");
                String shortDescription = jsonObject.optString("short_description");
                String authors = jsonObject.optString("authors");
                String date = jsonObject.optString("date");

                Document doc = new Document("link", link)
                        .append("headline", headline)
                        .append("short description", shortDescription)
                        .append("authors", authors)
                        .append("date", date);

                documents.add(doc);
            }

            if (!documents.isEmpty()) {
                collection.insertMany(documents);
                System.out.println("Data inserted successfully.");
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

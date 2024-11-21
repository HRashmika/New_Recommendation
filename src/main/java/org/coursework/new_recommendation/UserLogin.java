package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class UserLogin {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public UserLogin(String databaseName, String collectionName) {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase(databaseName);
            userCollection = database.getCollection(collectionName);
        } catch (Exception e) {
            throw new RuntimeException("Database connection error: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            // Query MongoDB for the user with the provided username and password
            Document user = userCollection.find(new Document("username", username)
                    .append("password", password)).first();
            return user != null; // Return true if a matching user is found
        } catch (Exception e) {
            throw new RuntimeException("Error during authentication: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

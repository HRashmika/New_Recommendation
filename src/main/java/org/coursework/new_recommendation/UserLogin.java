package org.coursework.new_recommendation;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class UserLogin extends User {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public UserLogin(String databaseName, String collectionName) {
        // Call the default constructor of the User class
        super();
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase(databaseName);
            userCollection = database.getCollection(collectionName);
        } catch (Exception e) {
            throw new RuntimeException("Database connection error: " + e.getMessage());
        }
    }

    // Modify authenticate method to use inherited username and password
    public boolean authenticate(String username, String password) {
        try {
            // Query MongoDB for the user with the provided username and password
            Document user = userCollection.find(new Document("username", username)
                    .append("password", password)).first();

            if (user != null) {

                this.setUsername(user.getString("username"));
                this.setEmail(user.getString("email"));
                this.setPreferences(user.getString("preferences"));
                return true;
            } else {
                return false; // No matching user found
            }
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

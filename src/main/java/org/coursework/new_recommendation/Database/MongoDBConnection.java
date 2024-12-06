package org.coursework.new_recommendation.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    // Connection String is for the cloud based mongoDB (Atlas)
    // It is configured to 0.0.0.0/0
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static final String CONNECTION_STRING = "mongodb+srv://hirushi20230208:mongo1114@cluster0.gldiu.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "News_Recommendation_System";

    // Singleton pattern for MongoClient
    public static synchronized MongoDatabase getDatabase() {
        if (mongoDatabase == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
            } catch (Exception e) {
                System.err.println("Error initializing MongoDB connection: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return mongoDatabase;
    }

    // Cleanup resources
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

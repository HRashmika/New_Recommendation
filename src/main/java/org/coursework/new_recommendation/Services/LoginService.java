package org.coursework.new_recommendation.Services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.coursework.new_recommendation.Model.User;
import org.coursework.new_recommendation.Database.MongoDBConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginService extends User {
    private MongoCollection<Document> userDetailsCollection;
    private MongoCollection<Document> userLoginCollection;

    // Inheriting from the user class
    public LoginService() {
        super();
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            userDetailsCollection = database.getCollection("User_Details");
            userLoginCollection = database.getCollection("User_Login");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage());
        }
    }
    // Checking the credentials
    public boolean authenticate(String username, String password) {
        try {
            Document user = userDetailsCollection.find(new Document("username", username)
                    .append("password", password)).first();

            if (user != null) {
                this.setUsername(user.getString("username"));
                this.setEmail(user.getString("email"));
                this.setPreferences(user.getString("preferences"));

                String currentTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Update login time in User_Details, for the current time
                userDetailsCollection.updateOne(
                        new Document("username", username),
                        new Document("$set", new Document("loginTime", currentTime))
                );

                // Record login time in User_Login
                userLoginCollection.updateOne(
                        Filters.and(
                                Filters.eq("username", username),
                                Filters.eq("password", password)
                        ),
                        Updates.push("loginTimes", currentTime),
                        new com.mongodb.client.model.UpdateOptions().upsert(true)
                );
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during authentication: " + e.getMessage());
        }
    }
}

package org.coursework.new_recommendation.Services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.coursework.new_recommendation.Database.MongoDBConnection;
import org.coursework.new_recommendation.Model.Admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminService extends Admin {

    private MongoDatabase database;
    private MongoCollection<Document> adminCollection;

    public AdminService(String adminId, String password, List<String> loginTimes) {
        // Call the parent constructor to initialize inherited fields
        super(adminId, password, loginTimes);

        try {
            database = MongoDBConnection.getDatabase();
            adminCollection = database.getCollection("Admin_Login");
        } catch (Exception e) {
            throw new RuntimeException("Database Connection Error: Could not connect to MongoDB.", e);
        }
    }

    // Method to check admin credentials
    public boolean checkCredentials() {
        try {
            Document adminDoc = adminCollection.find(new Document("Admin ID", getAdminId())
                    .append("Password", getPassword())).first();

            if (adminDoc != null) {
                // Set the loginTimes if admin is found
                setLoginTimes((List<String>) adminDoc.get("loginTimes"));
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking credentials.", e);
        }
        return false;
    }

    // Method to record login time
    public void recordLoginTime() {
        try {
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Update the admin document to add the current time to the "loginTimes" array
            adminCollection.updateOne(
                    new Document("Admin ID", getAdminId()),
                    new Document("$push", new Document("loginTimes", currentTime))
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while recording login time.", e);
        }
    }
}

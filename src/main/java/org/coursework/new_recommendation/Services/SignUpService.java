package org.coursework.new_recommendation.Services;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.coursework.new_recommendation.Model.User;
import java.util.regex.Pattern;

// Inherited from the User
public class SignUpService extends User {
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> preferencesCollection;

    public SignUpService(MongoCollection<Document> userCollection, MongoCollection<Document> preferencesCollection) {
        super();
        this.userCollection = userCollection;
        this.preferencesCollection = preferencesCollection;
    }

    // Both validation and storing in the database
    public String validateAndRegister(String email, String username, String password, String confirmPassword, boolean[] preferences) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Please fill in all fields.";
        }

        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }

        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters long and contain both letters and numbers.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        this.setEmail(email);
        this.setUsername(username);

        try {
            // Check if email already exists
            Document existingUser = userCollection.find(new Document("email", email)).first();
            if (existingUser != null) {
                return "An account with this email already exists.";
            }

            String[] preferenceNames = {"Technology", "Sports", "Health", "Politics", "Weather", "Entertainment"};
            StringBuilder selectedPreferences = new StringBuilder();

            for (int i = 0; i < preferences.length; i++) {
                if (preferences[i]) {
                    if (selectedPreferences.length() > 0) {
                        selectedPreferences.append(", ");
                    }
                    selectedPreferences.append(preferenceNames[i]);
                }
            }

            // Store user details with preferences as a string
            Document userDocument = new Document("email", this.getEmail())
                    .append("username", this.getUsername())
                    .append("password", password)
                    .append("preferences", selectedPreferences.toString());
            userCollection.insertOne(userDocument);

            // Store preferences in the Preferences collection with detailed scoring
            Document preferencesDocument = new Document("username", username)
                    .append("Technology", preferences[0] ? 10 : 0)
                    .append("Sports", preferences[1] ? 10 : 0)
                    .append("Health", preferences[2] ? 10 : 0)
                    .append("Politics", preferences[3] ? 10 : 0)
                    .append("Weather", preferences[4] ? 10 : 0)
                    .append("Entertainment", preferences[5] ? 10 : 0);
            preferencesCollection.insertOne(preferencesDocument);

            return "SUCCESS";
        } catch (Exception e) {
            return "Failed to create account: " + e.getMessage();
        }
    }
    // Validation of email using regex patterns
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    // Confirming passwords, to avoid the mismatch
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*");
    }
}

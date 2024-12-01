package org.coursework.new_recommendation;

import java.util.HashMap;
import java.util.Map;

public class Score {

    private Map<String, Integer> categoryScores;

    public Score() {
        this.categoryScores = new HashMap<>();
        initializeScores();
    }

    private void initializeScores() {
        categoryScores.put("Technology", 0);
        categoryScores.put("AI", 0);
        categoryScores.put("Weather", 0);
        categoryScores.put("Healthcare", 0);
        categoryScores.put("Sports", 0);
        categoryScores.put("Entertainment", 0);
    }

    public void updateScore(String category, int points) {
        if (categoryScores.containsKey(category)) {

            categoryScores.put(category, categoryScores.get(category) + points);
        } else {

            categoryScores.put(category, points);
        }
    }

    public Map<String, Integer> getCategoryScores() {
        return new HashMap<>(categoryScores);  // Return a copy to prevent direct modification
    }
}

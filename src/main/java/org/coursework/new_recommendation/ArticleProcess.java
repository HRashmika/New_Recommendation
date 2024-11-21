//package org.coursework.new_recommendation;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ArticleProcess {
//    private final Map<String, List<String>> keywordMap;
//
//    public ArticleProcess() {
//        this.keywordMap = new HashMap<>();
//        keywordMap.put("Technology", List.of("technology", "innovation", "software", "digital", "computing"));
//        keywordMap.put("Health", List.of("medicine", "health", "wellness", "fitness", "nutrition","cancer"));
//        keywordMap.put("Sports", List.of("soccer", "cricket", "tennis", "olympics", "athletes","sports","game"));
//        keywordMap.put("AI", List.of("ai", "data", "analytics", "machine learning", "artificial intelligence"));
//    }
//
//    public String categorizeArticle(String article) {
//        // Map to count keyword occurrences for each category
//        Map<String, Integer> categoryCounts = new HashMap<>();
//
//        // Normalize the article content to lower case for consistent matching
//        String articleContent = article.toLowerCase();
//
//        // Iterate over each category and count matches for its keywords
//        for (Map.Entry<String, List<String>> entry : keywordMap.entrySet()) {
//            int matchCount = 0;
//            for (String keyword : entry.getValue()) {
//                if (articleContent.contains(keyword.toLowerCase())) {
//                    matchCount++;
//                }
//            }
//            categoryCounts.put(entry.getKey(), matchCount);
//        }
//
//        // Determine the category with the highest match count
//        String bestCategory = "Uncategorized";  // Default category if no match is found
//        int maxMatches = 0;
//
//        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
//            if (entry.getValue() > maxMatches) {
//                maxMatches = entry.getValue();
//                bestCategory = entry.getKey();
//            }
//        }
//
//        return bestCategory;
//    }
//}

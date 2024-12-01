//package org.coursework.new_recommendation;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//// Concrete implementation of RecommendationSystem
//public class Recommendation extends ArticleRecommendation {
//    private User user;
//    private List<ArticleType> articles;
//
//    public Recommendation(User user, List<ArticleType> articles) {
//        this.user = user;
//        this.articles = articles;
//    }
//
//    @Override
//    public void updatePreferences(String category, int points) {
//        user.updateCategoryPoints(category, points);
//    }
//    @Override
//    public List<ArticleType> recommendArticles() {
//        // Get top 2 categories
//        Map<String, Integer> preferences = user.getCategoryPreferences();
//        List<String> topCategories = preferences.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // Sort by points descending
//                .limit(2) // Top 2 categories
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        // Recommend 5 random articles from each top category
//        List<ArticleType> recommendations = new ArrayList<>();
//        for (String category : topCategories) {
//            List<ArticleType> categoryArticles = articles.stream()
//                    .filter(article -> article.getCategory().equals(category))
//                    .collect(Collectors.toList());
//            Collections.shuffle(categoryArticles);
//            recommendations.addAll(categoryArticles.stream().limit(5).collect(Collectors.toList()));
//        }
//        return recommendations;
//    }
//
//}

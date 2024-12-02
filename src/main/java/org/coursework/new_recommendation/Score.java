//package org.coursework.new_recommendation;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//// Represents a scored article (Article + Score)
//class Score {
//    private ArticleType article;  // Assuming Score contains an ArticleType
//    private int score;  // Score or relevance of the article
//
//    // Constructor
//    public Score(ArticleType article, int score) {
//        this.article = article;
//        this.score = score;
//    }
//
//    // Getter for ArticleType
//    public ArticleType getArticle() {
//        return article;
//    }
//
//    // Getter for score
//    public int getScore() {
//        return score;
//    }
//    @Override
//    public String toString() {
//        return String.format("%-40s %-15s %-5d %s",
//                article.getHeadline(), article.getCategory(), score, article.getLink());
//    }
//}
//

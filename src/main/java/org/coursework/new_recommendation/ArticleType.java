package org.coursework.new_recommendation;

public class ArticleType {
    private final String headline;
    private final String shortDescription;
    private final String authors;
    private final String date;
    private final String category;
    private final String link;

    public ArticleType(String headline, String shortDescription, String authors, String date, String category, String link) {
        this.headline = headline;
        this.shortDescription = shortDescription;
        this.authors = authors;
        this.date = date;
        this.category = category;
        this.link = link;
    }

    public String getHeadline() {
        return headline;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getLink() {
        return link;
    }
    @Override
    public String toString() {
        return "Headline: " + headline + " | Category: " + category;
    }
}

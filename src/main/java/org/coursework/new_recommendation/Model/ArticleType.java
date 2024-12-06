package org.coursework.new_recommendation.Model;

// The class for articles
public class ArticleType {
    private String headline;
    private String shortDescription;
    private String authors;
    private String date;
    private String category;
    private String link;

    public ArticleType(String headline, String shortDescription, String authors, String date, String category, String link) {
        this.headline = headline;
        this.shortDescription = shortDescription;
        this.authors = authors;
        this.date = date;
        this.category = category;
        this.link = link;
    }

    // New constructor with only headline and category (Specifically for articles with missing values and recommendation setup)
    public ArticleType(String headline, String category) {
        this.headline = headline;
        this.category = category;
        this.shortDescription = "";
        this.authors = "";
        this.date = "";
        this.link = "";
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

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "ArticleType{" +
                "headline='" + headline + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", authors='" + authors + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}

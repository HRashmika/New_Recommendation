package org.coursework.new_recommendation;

public class ArticleType {
    private String heading;
    private String shortDescription;
    private String authors;
    private String date;
    private String category;
    private String link;

    public ArticleType(String heading, String shortDescription, String authors, String date, String category, String link) {
        this.heading = heading;
        this.shortDescription = shortDescription;
        this.authors = authors;
        this.date = date;
        this.category = category;
        this.link = link;
    }

    public String getHeading() {
        return heading;
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
}

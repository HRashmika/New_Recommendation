package org.coursework.new_recommendation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticleAPI {
    private static final String API_KEY = "45a52184813d4450aa5e692d63cf7440";
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines";

    public List<String> fetchArticles(String category) throws Exception {
        String urlString = BASE_URL + "?category=" + category + "&apiKey=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return parseArticles(response.toString());
    }

    private List<String> parseArticles(String jsonResponse) {
        List<String> articles = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonResponse);
        JSONArray articlesArray = obj.getJSONArray("articles");

        for (int i = 0; i < articlesArray.length(); i++) {
            JSONObject article = articlesArray.getJSONObject(i);
            String title = article.getString("title");
            articles.add(title); // Add only the title for simplicity
        }

        return articles;
    }
}

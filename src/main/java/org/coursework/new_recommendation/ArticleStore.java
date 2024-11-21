//package org.coursework.new_recommendation;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArticleStore {
//    private static final String DB_NAME = "News_Recommendation_System";
//    private static final String COLLECTION_NAME = "Articles";
//    private final MongoCollection<Document> collection;
//
//    public ArticleStore() {
//        // Use MongoClients.create() for the modern MongoDB driver
//        MongoClient client = MongoClients.create("mongodb://localhost:27017");
//        MongoDatabase database = client.getDatabase(DB_NAME);
//        this.collection = database.getCollection(COLLECTION_NAME);
//    }
//
//    public void saveArticles(List<String> articles, String category) {
//        for (String article : articles) {
//            Document doc = new Document("title", article)
//                    .append("category", category);
//            collection.insertOne(doc);
//        }
//    }
//
//    public List<Document> fetchAllArticles() {
//        // Use ArrayList from java.util
//        return collection.find().into(new ArrayList<>());
//    }
//}

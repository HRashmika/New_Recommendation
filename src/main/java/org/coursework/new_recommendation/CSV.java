package org.coursework.new_recommendation;

import com.opencsv.CSVReader;
import com.mongodb.client.*;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.Document;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSV {
    public static void main(String[] args) {
        String csvFile = "src/main/resources/Data_CSV/DataSet.csv";

        // Connecting to the MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("News_Recommendation_System");
        MongoCollection<Document> collection = database.getCollection("News Articles");

        // Reading the CSV file
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {

            String[] header = csvReader.readNext();  // Read the header row
            if (header == null) {
                System.err.println("The CSV file does not have a header row.");
                return;
            }
            List<Document> documents = new ArrayList<>();

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                if (row.length == header.length) {  // Ensure row matches header length
                    Document doc = new Document();
                    for (int i = 0; i < header.length; i++) {
                        doc.append(header[i], row[i]); // Map each row to the header
                    }
                    documents.add(doc);
                } else {
                    System.err.println("Skipping invalid row: " + String.join(",", row));
                }
            }

            // Inserting the data into MongoDB only once ****
            if (!documents.isEmpty()) {
                collection.insertMany(documents); // Only insert once
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("No data to insert.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }
}

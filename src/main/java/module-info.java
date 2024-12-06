module org.coursework.new_recommendation {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.json;
    requires com.opencsv;
    requires java.desktop;


    opens org.coursework.new_recommendation to javafx.fxml;

    exports org.coursework.new_recommendation.App;
    opens org.coursework.new_recommendation.App to javafx.fxml;
    exports org.coursework.new_recommendation.Model;
    opens org.coursework.new_recommendation.Model to javafx.fxml;
    exports org.coursework.new_recommendation.Services;
    opens org.coursework.new_recommendation.Services to javafx.fxml;
    exports org.coursework.new_recommendation.Other;
    opens org.coursework.new_recommendation.Other to javafx.fxml;
    exports org.coursework.new_recommendation.Database;
    opens org.coursework.new_recommendation.Database to javafx.fxml;
}
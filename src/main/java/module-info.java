module org.coursework.new_recommendation {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.coursework.new_recommendation to javafx.fxml;
    exports org.coursework.new_recommendation;
}
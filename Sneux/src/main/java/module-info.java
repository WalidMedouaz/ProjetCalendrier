module com.example.sneux {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires ch.qos.logback.classic;
    requires org.slf4j;
    requires java.desktop;


    opens com.example.sneux to javafx.fxml;
    exports com.example.sneux;
}
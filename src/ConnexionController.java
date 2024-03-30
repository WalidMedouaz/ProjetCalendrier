import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;

public class ConnexionController {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;

    public ConnexionController() {
        mongoClient = MongoClients.create("mongodb://pedago.univ-avignon.fr:27017");
        database = mongoClient.getDatabase("cal_aw");
        userCollection = database.getCollection("user");
    }

    @FXML
    private TextField tfpassword;

    @FXML
    private TextField tfusername;

    @FXML
    void Connexion(ActionEvent event) {
        String username = tfusername.getText();
        String password = tfpassword.getText();

        Document query = new Document("Id", username).append("Mdp", password);
        Document userDocument = userCollection.find(query).first();

        if (userDocument != null) {
            System.out.println("Connexion réussie ! " + userDocument.toJson());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) tfpassword.getScene().getWindow();
                stage.setScene(new Scene(root, 1920, 1000));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur ...");
            alert.setHeaderText("Id ou Mdp incorrect");
            alert.setContentText("Vérifiez vos informations.");
            alert.showAndWait();
        }
    }
    public void closeMongoClient() {
        mongoClient.close();
    }
}

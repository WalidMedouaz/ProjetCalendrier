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

    @FXML
    private TextField tfpassword;

    @FXML
    private TextField tfusername;

    MongoService mongoService = new MongoService();

    public static Utilisateur currentUser;

    @FXML
    void Connexion(ActionEvent event) {
        String username = tfusername.getText();
        String password = tfpassword.getText();

        Document userDocument = mongoService.connect(username, password);
        if (userDocument != null) {
            currentUser = new Utilisateur(userDocument.get("id").toString(), userDocument.get("nom").toString(), (String) userDocument.get("prenom"), userDocument.get("isEnseignant").toString(), userDocument.get("modeFavori").toString(), userDocument.get("eventPerso").toString());

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

}

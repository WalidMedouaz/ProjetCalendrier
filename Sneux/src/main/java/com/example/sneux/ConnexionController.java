package com.example.sneux;

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
import java.util.Objects;

public class ConnexionController {

    @FXML
    private TextField tfpassword;

    @FXML
    private TextField tfusername;

    public static MongoService mongoService = new MongoService();

    public static Utilisateur currentUser;

    @FXML
    void Connexion(ActionEvent event) {
        String username = tfusername.getText();
        String password = tfpassword.getText();

        Document userDocument = mongoService.getUser(username, password);
        if (userDocument != null) {
            if(Objects.equals(userDocument.get("isEnseignant").toString(), "false")) {
                currentUser = new Utilisateur(userDocument.getString("id"), userDocument.getString("nom"), userDocument.getString("prenom"), userDocument.getString("filiere"), userDocument.getString("groupe"), userDocument.getBoolean("isEnseignant"), userDocument.getString("modeFavori"), userDocument.getList("eventPerso", Document.class));
            }
            else {
                currentUser = new Utilisateur(userDocument.getString("id"), userDocument.getString("nom"), userDocument.getString("prenom"), userDocument.getBoolean("isEnseignant"), userDocument.getString("modeFavori"), userDocument.getList("eventPerso", Document.class), userDocument.getList("reservations", Document.class));
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) tfpassword.getScene().getWindow();
                stage.setScene(new Scene(root, 1920, 1000));
                stage.setMaximized(true);
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

    public static void updateMode(String mode) {
        mongoService.updateMode(currentUser.id, mode);
    }

}

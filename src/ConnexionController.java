import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnexionController {
    private Donnees donnees;

    public ConnexionController(Donnees donnees) {
        this.donnees = donnees;
    }

    @FXML
    private TextField tfpassword;

    @FXML
    private TextField tfusername;

    @FXML
    void Connexion(ActionEvent event) {
        String username = tfusername.getText();
        String password = tfpassword.getText();

        // Stocker les valeurs dans l'instance de Donnees
        donnees.setUsername(username);
        donnees.setPassword(password);
        System.out.println("Les données sont "+donnees.getUsername() +" "+ donnees.getPassword());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();
           
            Stage stage = (Stage) tfpassword.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

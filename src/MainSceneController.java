import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import java.time.LocalTime;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class MainSceneController implements Initializable {

  @FXML
  private TableView<Event> tableView;

  @SuppressWarnings("unchecked")
@Override
  public void initialize(URL location, ResourceBundle resources)  {
      // Créer des données de test
      ObservableList<Event> Modeles = FXCollections.observableArrayList();

      // Ajouter les données au TableView
      tableView.setItems(Modeles);

      TableColumn<Event, LocalTime> heureColumn = null;

// Parcourir la liste des colonnes de votre TableView
for (TableColumn<Event, ?> column : tableView.getColumns()) {
    // Vérifier si le texte de la colonne correspond à "Heure"
    if (column.getText().equals("Heure")) {
        // Si c'est le cas, assigner la colonne à la variable heureColumn
        heureColumn = (TableColumn<Event, LocalTime>) column;
        break;
    }
}

if (heureColumn != null) {
  // Maintenant, vous pouvez modifier la colonne heureColumn comme vous le souhaitez
  heureColumn.setCellValueFactory(cellData -> {
      int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
      return new SimpleObjectProperty<>(LocalTime.of(8 + index, 0));
  });

  // Utiliser un convertisseur pour formater les valeurs d'heure dans les cellules
  StringConverter<LocalTime> timeConverter = new LocalTimeStringConverter();
  heureColumn.setCellFactory(tc -> new TableCell<>() {
      @Override
      protected void updateItem(LocalTime item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
              setText(null);
          } else {
              setText(timeConverter.toString(item)); // Utiliser le convertisseur pour formater l'heure
          }
      }
  });
} else {
  System.out.println("La colonne Heure n'a pas été trouvée dans le TableView.");
}

  }
private Donnees donnees;
    @FXML
    private TextField tfsearch=new TextField("");

      @FXML
    private TextArea textArea1=new TextArea("");

    public void setDonnees(Donnees donnees) {
        this.donnees = donnees;
        setWelcomeText(this.donnees.getUsername());
       
    }
  public void setWelcomeText(String username) {
    textArea1.setText("Bienvenue, cher " + username + "! Qu'avez-vous prévu aujourd'hui?");
    textArea1.setEditable(false); 
}

    @FXML
    void LancerRecherche(ActionEvent event) {
        Stage Mainwindow=(Stage) tfsearch.getScene().getWindow();
        String t=tfsearch.getText();
        Mainwindow.setTitle(t);
System.out.println("Vous avez recherché "+this.tfsearch.getText()+ " !Ceci est affiché dans le terminal");
    }

}

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.io.IOException;
import java.text.ParseException;

public class MainSceneController {
    @FXML
    private Button previousWeekButton;
    @FXML
    private Button nextWeekButton;
    @FXML
    private Button currentWeekButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private RadioButton radioButtonDay;
    @FXML
    private RadioButton radioButtonWeek;
    @FXML
    private RadioButton radioButtonMonth;
    @FXML
    private ToggleGroup viewToggleGroup = new ToggleGroup();
    @FXML
    private ComboBox searchBox;
    @FXML
    private TextField searchField;

    @FXML
    private VBox newEventFieldsContainer;
    @FXML
    private GridPane scheduleGridPane;
    @FXML
    private ComboBox filterType;
    @FXML
    private ComboBox filterChoice;
    private static final double MIN_HEIGHT_PER_HALF_HOUR = 60.0;
    private ArrayList<Event> events = new ArrayList<Event>(); 
    private LocalDate currentMonday;
    private CalendarCERI calendarCERI;
    private ParserTest parser;
    @FXML
private Button previousDayButton;
@FXML
private Button nextDayButton; 
@FXML
private Label dayHeader=new Label();

private LocalDate currentDay;


    @FXML
    private void initialize() {
        try {
           
            parser = new ParserTest();
            loadEvents();
            createDefaultTimeSlots();
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            setupWeekdaysHeader();
            displayEvents();
            setEqualColumnWidths();
            List<String> stringList = Arrays.asList("Matière", "Groupe", "Salle", "Type de cours");
            filterType.getItems().addAll(stringList);
            if (!stringList.isEmpty()) {
                filterType.setValue(stringList.get(0));
            }

            ArrayList<String> distinctSubjects = parser.getDistinctSubjects();
            filterChoice.getItems().addAll(distinctSubjects);
            if(!distinctSubjects.isEmpty()) {
                filterChoice.setValue(distinctSubjects.get(0));
            }

            filterType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleFilterTypeSelection((String) newValue);
                }
                
        
            });
            radioButtonDay.setToggleGroup(viewToggleGroup);
                radioButtonWeek.setToggleGroup(viewToggleGroup);
                radioButtonMonth.setToggleGroup(viewToggleGroup);
                viewToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                    if (newToggle != null) {
                        RadioButton selectedRadioButton = (RadioButton) newToggle;
                        switch (selectedRadioButton.getText()) {
                            case "Jour":
                                updateButtonLabels("Jour précédent", "Jour suivant", "Jour d'aujourd'hui");
                                break;
                            case "Semaine":
                                updateButtonLabels("Semaine précédente", "Semaine suivante", "Aujourd'hui");
                                break;
                            case "Mois":
                                updateButtonLabels("Mois précédent", "Mois suivant", "Ce mois-ci");
                                break;
                        }
                    }
                });
                viewToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                    if (newToggle != null) {
                        updateViewBasedOnRadioButton((RadioButton) newToggle);
                    }
                });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateViewBasedOnRadioButton(RadioButton selectedRadioButton) {
        if (selectedRadioButton == radioButtonDay) {
            // Changez le texte des boutons pour l'affichage "Jour"
            previousWeekButton.setText("Jour précédent");
            nextWeekButton.setText("Jour suivant");
            currentWeekButton.setText("Jour actuel");
            // Mettez ici le code pour mettre à jour la vue d'affichage en mode "Jour"
        } else if (selectedRadioButton == radioButtonWeek) {
            // Changez le texte des boutons pour l'affichage "Semaine"
            previousWeekButton.setText("Semaine précédente");
            nextWeekButton.setText("Semaine suivante");
            currentWeekButton.setText("Semaine actuelle");
            // Mettez ici le code pour mettre à jour la vue d'affichage en mode "Semaine"
        } else if (selectedRadioButton == radioButtonMonth) {
            // Changez le texte des boutons pour l'affichage "Mois"
            previousWeekButton.setText("Mois précédent");
            nextWeekButton.setText("Mois suivant");
            currentWeekButton.setText("Mois actuel");
            // Mettez ici le code pour mettre à jour la vue d'affichage en mode "Mois"
        }
    }
    private void updateButtonLabels(String prevLabel, String nextLabel, String currentLabel) {
        previousWeekButton.setText(prevLabel); // Changez l'id en fonction de votre FXML
        nextWeekButton.setText(nextLabel); // Changez l'id en fonction de votre FXML
        currentWeekButton.setText(currentLabel); // Changez l'id en fonction de votre FXML
    }
    @FXML
    private void handleAddEvent() {
        System.out.println("Ajouter un nouvel événement cliqué");
        // Créer les zones de texte
        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Nom de l'événement");
        TextField dateField = new TextField();
        dateField.setPromptText("Date (format: JJ/MM/AAAA)");
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Heure de début (format: HH:MM)");
        TextField endTimeField = new TextField();
        endTimeField.setPromptText("Heure de fin (format: HH:MM)");
        TextField location = new TextField();
        location.setPromptText("Lieu de l'évènement");
        TextField type = new TextField();
        type.setPromptText("Type d'évènement");
        TextField group= new TextField();
        group.setPromptText("Groupe concerné");
        newEventFieldsContainer.getChildren().addAll(eventNameField, dateField, startTimeField, endTimeField,location);
    
        // Bouton pour soumettre le nouvel événement
        Button submitButton = new Button("Ajouter l'événement");
        submitButton.setOnAction(e -> {
            handleSubmitEvent(eventNameField.getText(), dateField.getText(), startTimeField.getText(), endTimeField.getText(), location.getText(), type.getText(), group.getText());
            newEventFieldsContainer.getChildren().clear();
        });
         newEventFieldsContainer.getChildren().add(submitButton);
    }

    private void handleSubmitEvent(String eventName, String date, String startTime, String endTime,String l,String t,String g) {
        // Ici,je vais devoir rajouter cela à la base de données
        System.out.println("Événement ajouté: " + eventName + ", Date: " + date + ", De: " + startTime + " à " + endTime);
 }
    private void handleFilterTypeSelection(String selectedItem) {
        filterChoice.getItems().clear();
        switch (selectedItem) {
            case "Matière":
                ArrayList<String> distinctSubjects = parser.getDistinctSubjects();
                filterChoice.getItems().addAll(distinctSubjects);
                if(!distinctSubjects.isEmpty()) {
                    filterChoice.setValue(distinctSubjects.get(0));
                }                break;
            case "Groupe":
                ArrayList<String> distinctGroups = parser.getDistinctGroups();
                filterChoice.getItems().addAll(distinctGroups);
                if(!distinctGroups.isEmpty()) {
                    filterChoice.setValue(distinctGroups.get(0));
                }
                break;
            case "Salle":
                ArrayList<String> distinctLocation = parser.getDistinctLocation();
                filterChoice.getItems().addAll(distinctLocation);
                if(!distinctLocation.isEmpty()) {
                    filterChoice.setValue(distinctLocation.get(0));
                }
                break;
            case "Type de cours":
                ArrayList<String> distinctTypes = parser.getDistinctTypes();
                filterChoice.getItems().addAll(distinctTypes);
                if(!distinctTypes.isEmpty()) {
                    filterChoice.setValue(distinctTypes.get(0));
                }
                break;
            default:
                System.out.println("Not found...");
                break;
        }
    }

    @FXML
    private void handleFilterButton() throws IOException, ParseException {
        events.clear();
        events.addAll(calendarCERI.getEvents());
        parser.filter(events, filterType.getValue().toString(), filterChoice.getValue().toString());
        updateWeekView();
        System.out.println(events.size());
    }

    private void loadEvents() throws IOException, ParseException {
        calendarCERI = parser.getCalendarHeader();
        parser.getCalendarEvents(calendarCERI.getEvents());
        events.addAll(calendarCERI.getEvents());
    }

    private void setupWeekdaysHeader() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
    for (int i = 0; i < 7; i++) { // commencez à 0 pour l'alignement avec les colonnes
        LocalDate date = currentMonday.plusDays(i);
        String headerText = date.format(formatter);
        Label dayLabel = new Label(headerText);
        dayLabel.setOnMouseClicked(e -> displayDayView(date)); // Ajoutez le gestionnaire de clic ici
        scheduleGridPane.add(dayLabel, i + 1, 0); // i+1 car la première colonne est pour les heures
        GridPane.setMargin(dayLabel, new Insets(0, 0, 0, 125));
    }
}

private void displayDayView(LocalDate l) {
    clearGridPane();
  
}

private void displayEventsForDay(LocalDate l) {
    for (Event event : events) {
        if (event.getStartDate() == null) {
            continue;
        }
        LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (eventDate.equals(l)) {
            addEventToGridday(event, l);
        }
    }
}

private void setupDayHeader(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
    String headerText = date.format(formatter);
    Label dayLabel = new Label(headerText);
    scheduleGridPane.add(dayLabel, 1, 0); // i+1 car la première colonne est pour les heures
    GridPane.setMargin(dayLabel, new Insets(0, 0, 0, 100));

}
private void updateDayView() {
    displayDayView(currentDay);
}

@FXML
private void loadPreviousDay() {
    currentDay = currentDay.minusDays(1);
    updateDayView();
}

@FXML
private void loadNextDay() {
    currentDay = currentDay.plusDays(1);
    updateDayView();
}
private void onDayHeaderClicked(LocalDate date) {
    radioButtonDay.setSelected(true);
    currentDay = date;
    updateDayView();
}

    private void displayEvents() {
        for (Event event : events) {
            if (event.getStartDate() == null) {
                //System.out.println("Date nulle détectée...");
                continue;
            }

            LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!eventDate.isBefore(currentMonday) && !eventDate.isAfter(currentMonday.plusDays(6))) {
                addEventToGrid(event);
            }
        }

    }
    @FXML
    private void loadCurrentWeek() {
        currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateWeekView();  
    }
     public void applyLightMode() {
    // Récupère la scène à partir de n'importe quel composant ajouté à celle-ci, ici le GridPane.
    Scene scene = scheduleGridPane.getScene();
    if (scene != null) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/lightmode.css").toExternalForm());
    }
}

public void applyDarkMode() {
    // Même chose pour le mode sombre.
    Scene scene = scheduleGridPane.getScene();
    if (scene != null) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/darkmode.css").toExternalForm());
    }
}

private void addEventToGridday(Event event, LocalDate displayDate) {
    // Ensure we are only adding events for the given display date
    LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    if (!eventDate.equals(displayDate)) {
        return; // Skip events that are not for the display date
    }

    LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

    // For a day view, we only use one column
    int dayColumn = 1;
    int startRow = timeToRow(startTime);
    int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;

    String[] teachers = Arrays.asList("").toArray(new String[0]);
    if(event.getTeacher() != null) {
        teachers = event.getTeacher().split(","); 
    }
    StringBuilder teachersWithNewLines = new StringBuilder();
    for (String teacher : teachers) {
        teachersWithNewLines.append(teacher.trim()).append("\n"); 
    }
    String message1 = event.getType() != null && !event.getType().isEmpty() ? "pour un(e) " + event.getType() + "\n" : "";
String message2 = event.getLocation() != null && !event.getLocation().isEmpty() ? "dans la salle " + event.getLocation() : "";
String message3 = teachersWithNewLines.length() > 0 ? " avec \n" + teachersWithNewLines.toString() : "";


    VBox eventBox = new VBox(new Text(event.getSubject() + message3+ message1 +"\n"+ message2));
   String backgroundColor = "lightblue";
    String textColor = "black";
    if(event.getType() != null){
        if (event.getType().equals("Evaluation")) {
            backgroundColor = "red";
            textColor = "white";
        }
    }
    else{
        backgroundColor = "green";
        textColor = "white";
    }
        eventBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black; -fx-text-fill: " + textColor + ";");

    double eventHeight = durationInHalfHours * MIN_HEIGHT_PER_HALF_HOUR;
    eventBox.setMinHeight(eventHeight);
// Définissez la largeur préférée pour le GridPane pour qu'elle corresponde à celle du ScrollPane.
scheduleGridPane.setPrefWidth(scrollPane.getViewportBounds().getWidth());

// Assurez-vous que le ScrollPane ne montre pas de barres de défilement lorsque son contenu est plus petit que la zone d'affichage.
scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    StackPane container = new StackPane(eventBox);
    container.setAlignment(Pos.CENTER); // Centre le contenu dans le StackPane
    scheduleGridPane.add(container, dayColumn, startRow, 1, durationInHalfHours);
    }

    private void addEventToGrid(Event event) {
        LocalDate date = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        int dayColumn = dayOfWeekToColumn(date.getDayOfWeek());
        int startRow = timeToRow(startTime);
        int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;

        String[] teachers = Arrays.asList("").toArray(new String[0]);
        if(event.getTeacher() != null) {
            teachers = event.getTeacher().split(","); 
        }
        StringBuilder teachersWithNewLines = new StringBuilder();
        for (String teacher : teachers) {
            teachersWithNewLines.append(teacher.trim()).append("\n"); 
        }
        String message1 = event.getType() != null && !event.getType().isEmpty() ? "pour un(e) " + event.getType() + "\n" : "";
String message2 = event.getLocation() != null && !event.getLocation().isEmpty() ? "dans la salle " + event.getLocation() : "";
String message3 = teachersWithNewLines.length() > 0 ? " avec \n" + teachersWithNewLines.toString() : "";


        VBox eventBox = new VBox(new Text(event.getSubject() + message3+ message1 +"\n"+ message2));
       String backgroundColor = "lightblue";
        String textColor = "black";
        if(event.getType() != null){
            if (event.getType().equals("Evaluation")) {
                backgroundColor = "red";
                textColor = "white";
            }
        }
        else{
            backgroundColor = "green";
            textColor = "white";
        }
            eventBox.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black; -fx-text-fill: " + textColor + ";");

        double eventHeight = durationInHalfHours * MIN_HEIGHT_PER_HALF_HOUR;
        eventBox.setMinHeight(eventHeight);
        scheduleGridPane.add(eventBox, dayColumn, startRow, 1, durationInHalfHours);
        GridPane.setValignment(eventBox, VPos.TOP);
        GridPane.setMargin(eventBox, new Insets(MIN_HEIGHT_PER_HALF_HOUR / 2, 0, 0, 100));
    }



    private void createDefaultTimeSlots() {
        LocalTime startTime = LocalTime.of(8, 0);
        int row = 1;
        scheduleGridPane.getRowConstraints().clear();

        while (!startTime.isAfter(LocalTime.of(20, 0))) {
            Text timeText = new Text(startTime.toString());
            scheduleGridPane.add(timeText, 0, row);
            GridPane.setMargin(timeText, new Insets(0,0,0,75));

            RowConstraints rowConstraints = new RowConstraints(MIN_HEIGHT_PER_HALF_HOUR);
            scheduleGridPane.getRowConstraints().add(rowConstraints);

            startTime = startTime.plusMinutes(30);
            row++;
        }
    }

    private int timeToRow(LocalTime time) {
        LocalTime baseTime = LocalTime.of(8, 0);
        int halfHoursSinceBaseTime = (int) Duration.between(baseTime, time).toMinutes() / 30;
        return 1 + halfHoursSinceBaseTime;
    }

    private int dayOfWeekToColumn(DayOfWeek day) {
        return day.getValue();
    }

    @FXML
    private void loadPreviousWeek() {
        currentMonday = currentMonday.minusWeeks(1);
        updateWeekView();
    }

    @FXML
    private void loadNextWeek() {
        currentMonday = currentMonday.plusWeeks(1);
        updateWeekView();
    }

    private void updateWeekView() {
        clearGridPane();
        createDefaultTimeSlots();
        setupWeekdaysHeader();
        displayEvents();
    }

    private void clearGridPane() {
        scheduleGridPane.getChildren().clear();
    }

    private void setEqualColumnWidths() {
        double columnWidthPercentage = 100.0 / 7;
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(columnWidthPercentage);
        scheduleGridPane.getColumnConstraints().clear();
        scheduleGridPane.getColumnConstraints().add(new ColumnConstraints()); // Default constraints for the first column
        for (int i = 0; i < 7; i++) {
            scheduleGridPane.getColumnConstraints().add(columnConstraints);
        }
    }

}
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import javafx.scene.text.Text;
import java.time.*;
import java.util.*;
import java.io.IOException;
import java.text.ParseException;

import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

enum ViewMode {
    DAY, WEEK, MONTH
}

public class MainSceneController {
    private ViewMode currentViewMode = ViewMode.WEEK;
    @FXML
private Button previousButton; // Renommé pour la généralisation
@FXML
private Button nextButton; // Renommé pour la généralisation

    @FXML
    private GridPane scheduleGridPane;
    @FXML
    private RadioButton radioDay, radioWeek, radioMonth;
private final ToggleGroup viewToggleGroup = new ToggleGroup();


    private List<Event> events; // Your events list
    private LocalDate currentMonday;
    private CalendarCERI calendarCERI;
private ComboBox<String> viewOptionsComboBox;

    @FXML
    private void initialize() {
        try {
            loadEvents();
            createDefaultTimeSlots();
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            setupWeekdaysHeader();
            displayEvents();
            setEqualColumnWidths();
radioDay.setToggleGroup(viewToggleGroup);
radioWeek.setToggleGroup(viewToggleGroup);
radioMonth.setToggleGroup(viewToggleGroup);

// Ajouter un écouteur sur le ToggleGroup pour réagir aux changements de sélection
viewToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
    if (radioDay.isSelected()) {
        displayDayView();
    } else if (radioWeek.isSelected()) {
      updateWeekView();
    } else if (radioMonth.isSelected()) {
        displayMonthView();
    }
});
radioWeek.setSelected(true);
}catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    @FXML
private void loadCurrentWeek() {
    currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    updateWeekView();  
}

private void displayDayView() {
   
    clearGridPane();
    createDefaultTimeSlots();
    setupDayHeader();
    displayEvents();
    LocalDate selectedDay = currentMonday; // Exemple: afficher les événements du "currentMonday"
    for (Event event : events) {
        if(event.getStartDate()==null){
            return;
        }
        LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // Check if event falls within the current week being displayed
        if (!eventDate.isBefore(currentMonday) ) {
            addEventToGrid(event);
        }
    }
}

private void setupDayHeader() {
    // Méthode pour configurer l'en-tête pour l'affichage par jour
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
    String headerText = currentMonday.format(formatter);
    scheduleGridPane.add(new Label(headerText), 1, 0); // Utiliser une seule colonne pour l'affichage par jour
}


private void displayMonthView() {
    clearGridPane();
    setupMonthHeader(); // Méthode pour configurer les en-têtes pour le mois
    // TODO: Logique pour afficher les événements du mois, possiblement en regroupant par jour
}

private void setupMonthHeader() {
    // Méthode pour configurer les en-têtes pour les jours du mois
    LocalDate startOfMonth = currentMonday.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endOfMonth = currentMonday.with(TemporalAdjusters.lastDayOfMonth());
    int dayOfMonth = startOfMonth.getDayOfMonth();
    int dayOfWeek = startOfMonth.getDayOfWeek().getValue(); // 1 = Monday, ..., 7 = Sunday
    
    // Exemple: créer des en-têtes pour chaque jour du mois (ajuster selon votre layout et logique d'affichage)
    for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
        // Ajouter des en-têtes pour chaque jour du mois dans le GridPane
    }
}


    private void loadEvents() throws IOException, ParseException {
        ParserTest parser = new ParserTest();
        calendarCERI = parser.getCalendarHeader();
        parser.getCalendarEvents(calendarCERI);
        events = calendarCERI.getEvents();
        parser.filterBySubject(calendarCERI, "Group", "ILSEN");
    }

    private void setupWeekdaysHeader() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
        for (int i = 1; i <= 7; i++) {
            LocalDate date = currentMonday.plusDays(i - 1);
            String headerText = date.format(formatter);
            scheduleGridPane.add(new Label(headerText), i, 0);
        }
    }

    private void displayEvents() {
        for (Event event : events) {
            if(event.getStartDate()==null){
                return;
            }
            LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!eventDate.isBefore(currentMonday) && !eventDate.isAfter(currentMonday.plusDays(6))) {
                addEventToGrid(event);
            }
        }
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

    
    
    

    private void addEventToGrid(Event event) {
        LocalDate date = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    
        int dayColumn = dayOfWeekToColumn(date.getDayOfWeek());
        int startRow = timeToRow(startTime);
        int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;
    
        String[] teachers = event.getTeacher().split(","); // Séparez les noms des enseignants en utilisant la virgule comme délimiteur
        StringBuilder teachersWithNewLines = new StringBuilder();
        for (String teacher : teachers) {
            teachersWithNewLines.append(teacher.trim()).append("\n"); // Ajoutez chaque nom suivi d'un retour à la ligne
        }
        VBox eventBox = new VBox(new Text(event.getSubject() + " avec \n" + teachersWithNewLines.toString()+ "pour un(e) "+ event.getType() + " dans la salle "+ event.getLocation()));
        String backgroundColor = "lightblue";
    String textColor = "black";
    
    // Si l'événement est de type évaluation, changez la couleur de fond et le texte
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
        // Set the height of the event VBox according to its duration in half-hours
        eventBox.setMinHeight(eventHeight);
    
        scheduleGridPane.add(eventBox, dayColumn, startRow, 1, durationInHalfHours); // Use duration for rowSpan
        GridPane.setValignment(eventBox, VPos.TOP);
         GridPane.setMargin(eventBox, new Insets(MIN_HEIGHT_PER_HALF_HOUR / 2, 0, 0, 10));
    }
    
private static final double MIN_HEIGHT_PER_HOUR = 60.0; // Assuming 60 pixels height for an hour
private static final double MIN_HEIGHT_PER_HALF_HOUR = 30.0; // Height for 30 minutes slots

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


// This method sets the height for each row in the schedule
private void setRowHeights(GridPane gridPane, double minHeight) {
    // Parcourez chaque enfant (événement) dans le GridPane
    for (Node node : gridPane.getChildren()) {
        if (node instanceof VBox && GridPane.getRowIndex(node) != null) {
            VBox eventBox = (VBox) node;
            int rowIndex = GridPane.getRowIndex(eventBox);

            // Obtenir la hauteur de l'événement et ajuster la hauteur de la rangée correspondante
            double eventHeight = eventBox.getMinHeight();
            while (rowIndex < gridPane.getRowConstraints().size()) {
                RowConstraints rowConstraints = gridPane.getRowConstraints().get(rowIndex);
                rowConstraints.setMinHeight(Math.max(minHeight, eventHeight)); // Utiliser la plus grande hauteur
                rowIndex++;
            }
        }
    }
}


    private int dayOfWeekToColumn(DayOfWeek day) {
        return day.getValue(); // Assuming your grid columns start with Monday at column 1
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
        scheduleGridPane.getChildren().clear(); // Cela retirera tous les nœuds enfants du GridPane
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

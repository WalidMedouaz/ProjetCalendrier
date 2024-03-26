import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

public class MainSceneController {

    @FXML
    private GridPane scheduleGridPane;

    private List<Event> events; // Your events list
    private LocalDate currentMonday;
    private CalendarCERI calendarCERI;

    @FXML
    private void initialize() {
        try {
            loadEvents();
            createDefaultTimeSlots();
            setRowHeights(scheduleGridPane, 10);
            currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            setupWeekdaysHeader();
            displayEvents();
          // Supposons que vous souhaitez que chaque colonne ait une largeur égale (à part la première)
double columnWidthPercentage = 100.0 / 7; // Si vous avez 8 colonnes, chaque colonne aura 12.5% de la largeur du GridPane

ColumnConstraints columnConstraints = new ColumnConstraints();
columnConstraints.setPercentWidth(columnWidthPercentage);

// Répétez ceci pour chaque colonne dont vous souhaitez définir la largeur, à l'exception de la première
scheduleGridPane.getColumnConstraints().clear(); // Effacez d'abord toutes les contraintes existantes
scheduleGridPane.getColumnConstraints().add(new ColumnConstraints()); // Ajoutez des contraintes par défaut pour la première colonne
for (int i = 0; i < 7; i++) { // Répétez pour les 7 colonnes restantes
    scheduleGridPane.getColumnConstraints().add(columnConstraints);
}

        } catch (IOException | ParseException e) {
            e.printStackTrace();
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
        // Only display events that fall within the week starting with currentMonday.
        for (Event event : events) {
            if(event.getStartDate()==null){
                System.out.println("Date nulle détectée...");
                return;
            }
            LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            // Check if event falls within the current week being displayed
            if (!eventDate.isBefore(currentMonday) && !eventDate.isAfter(currentMonday.plusDays(6))) {
                addEventToGrid(event);
            }
        }
    }
    

    private void addEventToGrid(Event event) {
        LocalDate date = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    
        int dayColumn = dayOfWeekToColumn(date.getDayOfWeek());
        int startRow = timeToRow(startTime);
        int endRow = timeToRow(endTime);
        int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;
    
        String[] teachers = event.getTeacher().split(","); // Séparez les noms des enseignants en utilisant la virgule comme délimiteur
        StringBuilder teachersWithNewLines = new StringBuilder();
        for (String teacher : teachers) {
            teachersWithNewLines.append(teacher.trim()).append("\n"); // Ajoutez chaque nom suivi d'un retour à la ligne
        }
        VBox eventBox = new VBox(new Text(event.getSubject() + "\n" + teachersWithNewLines.toString()));
        eventBox.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        int MIN_HEIGHT_PER_HALF_HOUR=10;
        // Set the height of the event VBox according to its duration in half-hours
        eventBox.setMinHeight(durationInHalfHours * MIN_HEIGHT_PER_HALF_HOUR);
    
        scheduleGridPane.add(eventBox, dayColumn, startRow, 1, durationInHalfHours); // Use duration for rowSpan
        GridPane.setValignment(eventBox, VPos.TOP);
    }
    
// Cette méthode est appelée après avoir placé tous les événements sur le GridPane.
private void adjustEventHeights() {
    final double rowHeight = 2; // La hauteur pour 30 minutes, ajustez selon votre mise en page
    for (Node child : scheduleGridPane.getChildren()) {
        if (child instanceof VBox) {
            VBox eventBox = (VBox) child;
            Integer startRow = GridPane.getRowIndex(eventBox);
            Integer rowSpan = GridPane.getRowSpan(eventBox);

            // La hauteur de l'événement est la hauteur d'une rangée multipliée par le nombre de rangées à couvrir.
            double eventHeight = rowHeight * rowSpan;
            eventBox.setMinHeight(eventHeight);
        }
    }
}

private static final double MIN_HEIGHT_PER_HOUR = 60.0; // Assuming 60 pixels height for an hour
private static final double MIN_HEIGHT_PER_HALF_HOUR = 30.0; // Height for 30 minutes slots

private void createDefaultTimeSlots() {
    LocalTime startTime = LocalTime.of(8, 0); // Start time
    int row = 1; // Start from the second row
    scheduleGridPane.getRowConstraints().clear(); // Clear any existing row constraints

    while (!startTime.isAfter(LocalTime.of(20, 0))) { // Until end time
        Text timeText = new Text(startTime.toString());
        scheduleGridPane.add(timeText, 0, row); // 0 is the first column
        
        // Create a new RowConstraints object with the specified height for the row
        RowConstraints rowConstraints = new RowConstraints(MIN_HEIGHT_PER_HALF_HOUR);
        
        // If you want to add more space between hours, you can adjust the min height here
        if (startTime.getMinute() == 0) {
            rowConstraints.setMinHeight(MIN_HEIGHT_PER_HOUR);
        }

        scheduleGridPane.getRowConstraints().add(rowConstraints); // Add row constraints to the grid pane
        
        // Add two slots for each hour: one for the hour, and one for the half-hour
        if(startTime.getMinute() == 0) {
            startTime = startTime.plusMinutes(30); // Increment by 30 minutes for the half-hour slot
            row++;
            // Add constraints for the half-hour slot
            RowConstraints halfHourRowConstraints = new RowConstraints(MIN_HEIGHT_PER_HALF_HOUR);
            scheduleGridPane.getRowConstraints().add(halfHourRowConstraints);
        } else {
            startTime = startTime.plusMinutes(30); // Increment by 30 minutes for the next hour slot
        }

        row++; // Move to the next row for the next time slot
    }
}

   // Assurez-vous que cette méthode renvoie la rangée correcte pour une heure donnée.
private int timeToRow(LocalTime time) {
    LocalTime baseTime = LocalTime.of(8, 0); // L'heure de début de votre grille horaire
    int halfHoursSinceBaseTime = (int) Duration.between(baseTime, time).toMinutes() / 30;
    return 1 + halfHoursSinceBaseTime; // Commencez à compter à partir de 1 si la première ligne est réservée pour les en-têtes
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
    
    
    
}

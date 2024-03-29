import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.RowConstraints;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.io.IOException;
import java.text.ParseException;

public class MainSceneController {

    @FXML
    private GridPane scheduleGridPane;
    @FXML
    private ComboBox filterType;
    @FXML
    private ComboBox filterChoice;
    private static final double MIN_HEIGHT_PER_HALF_HOUR = 60.0;
    private ArrayList<Event> events = new ArrayList<Event>(); // Your events list
    private LocalDate currentMonday;
    private CalendarCERI calendarCERI;
    private ParserTest parser;


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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
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
        for (int i = 1; i <= 7; i++) {
            LocalDate date = currentMonday.plusDays(i - 1);
            String headerText = date.format(formatter);
            Label dayLabel = new Label(headerText);
            scheduleGridPane.add(dayLabel, i, 0);
            GridPane.setMargin(dayLabel, new Insets(0, 0, 0, 125));
        }
    }

    private void displayEvents() {
        for (Event event : events) {
            if (event.getStartDate() == null) {
                //System.out.println("Date nulle détectée...");
                return;
            }

            LocalDate eventDate = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
        int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;

        String[] teachers = Arrays.asList("").toArray(new String[0]);
        if(event.getTeacher() != null) {
            teachers = event.getTeacher().split(","); // Séparez les noms des enseignants en utilisant la virgule comme délimiteur
        }
        StringBuilder teachersWithNewLines = new StringBuilder();
        for (String teacher : teachers) {
            teachersWithNewLines.append(teacher.trim()).append("\n"); // Ajoutez chaque nom suivi d'un retour à la ligne
        }
        VBox eventBox = new VBox(new Text(event.getSubject() + "\n" + teachersWithNewLines.toString()));
        eventBox.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
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

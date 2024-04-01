import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class MainSceneController {
    private static final double MIN_HEIGHT_PER_HALF_HOUR = 60.0;

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
    private VBox newEventFieldsContainer;
    @FXML
    private GridPane scheduleGridPane;
    @FXML
    private ComboBox filterType;
    @FXML
    private ComboBox filterChoice;
    @FXML
    private ComboBox searchBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button reservationButton;

    private ArrayList<Event> events = new ArrayList<Event>(); // Your events list
    private LocalDate currentMonday;
    private CalendarCERI calendarCERI;
    private ParserTest parser;
    private BooleanProperty edtPerso = new SimpleBooleanProperty(true);
    private BooleanProperty edtFormation = new SimpleBooleanProperty(false);
    private BooleanProperty edtEnseignant = new SimpleBooleanProperty(false);
    private BooleanProperty edtSalle = new SimpleBooleanProperty(false);
    @FXML
    private Button previousDayButton;
    @FXML
    private Button nextDayButton; 
    @FXML
    private Label dayHeader=new Label();

    private LocalDate currentDay;
    private LocalDate currentMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());

    @FXML
    private void initialize() {
        parser = new ParserTest();
        currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        currentDay = LocalDate.now();

        if(ConnexionController.currentUser.isEnseignant) {
            edtEnseignant.setValue(true);
        }
        Thread parserThread = new Thread(() -> {
            try {
                parser = new ParserTest();
                loadEvents();

                Platform.runLater(() -> {
                    createDefaultTimeSlots();
                    setupWeekdaysHeader();
                    displayEvents();
                    setEqualColumnWidths();

                    List<String> stringList = Arrays.asList("Matière", "Groupe", "Salle", "Type de cours");
                    filterType.getItems().addAll(stringList);
                    if (!stringList.isEmpty()) {
                        filterType.setValue(stringList.get(0));
                    }

                    List<String> searchOptions = Arrays.asList("Formation", "Enseignant", "Salle");
                    searchBox.getItems().addAll(searchOptions);
                    if (!searchOptions.isEmpty()) {
                        searchBox.setValue(searchOptions.get(0));
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

                    if(Objects.equals(ConnexionController.currentUser.modeFavori, "dark")) {
                        applyDarkMode();
                    }
                    else {
                        applyLightMode();
                    }

                    ChangeListener<Boolean> salleListener = new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                            if(edtEnseignant.get() && newValue) {
                                reservationButton.setVisible(true);
                            }
                            else {
                                reservationButton.setVisible(false);
                            }
                        }
                    };
                    edtSalle.addListener(salleListener);

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

                });
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
        parserThread.start();
    }

    private void updateViewBasedOnRadioButton(RadioButton selectedRadioButton) {
        clearGridPane();
        createDefaultTimeSlots();
        if (selectedRadioButton == radioButtonDay) {
            // Changez le texte des boutons pour l'affichage "Jour"
            previousWeekButton.setText("Jour précédent");
            nextWeekButton.setText("Jour suivant");
            currentWeekButton.setText("Jour actuel");
            previousWeekButton.setOnAction(e -> loadPreviousDay());
            nextWeekButton.setOnAction(e -> loadNextDay());
            currentWeekButton.setOnAction(e -> loadCurrentDay());
    
            setupDayHeader(currentDay);
            displayEventsForDay(currentDay);
        } else if (selectedRadioButton == radioButtonWeek) {
            // Changez le texte des boutons pour l'affichage "Semaine"
            previousWeekButton.setText("Semaine précédente");
            nextWeekButton.setText("Semaine suivante");
            currentWeekButton.setText("Semaine actuelle");
            previousWeekButton.setOnAction(e -> loadPreviousWeek());
            nextWeekButton.setOnAction(e -> loadNextWeek());
            currentWeekButton.setOnAction(e -> loadCurrentWeek());
 
            setupWeekdaysHeader();
            displayEvents();
            setEqualColumnWidths();
            
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

    private boolean isSpaceOccupied(GridPane gridPane, int column, int row) {
        for (Node child : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(child) == column && GridPane.getRowIndex(child) == row) {
                return true; // L'espace est déjà occupé
            }
        }
        return false; // L'espace n'est pas occupé
    }

    @FXML
    private void handleReservation() {
        Dialog<Pair<LocalDateTime, LocalDateTime>> dialog = new Dialog<>();
        dialog.setTitle("Sélection des Dates de Réservation");
        dialog.setHeaderText("Choisissez les dates de début et de fin pour la réservation");

        ButtonType okButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        ComboBox<Integer> startHourComboBox = new ComboBox<>();
        startHourComboBox.getItems().addAll(IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList()));
        startHourComboBox.setValue(LocalTime.now().getHour());

        ComboBox<Integer> startMinuteComboBox = new ComboBox<>();
        startMinuteComboBox.getItems().addAll(IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList()));
        startMinuteComboBox.setValue(LocalTime.now().getMinute());

        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1)); // Suggère le lendemain comme date de fin par défaut
        ComboBox<Integer> endHourComboBox = new ComboBox<>();
        endHourComboBox.getItems().addAll(IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList()));
        endHourComboBox.setValue(LocalTime.now().getHour());

        ComboBox<Integer> endMinuteComboBox = new ComboBox<>();
        endMinuteComboBox.getItems().addAll(IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList()));
        endMinuteComboBox.setValue(LocalTime.now().getMinute());

        // Ajoutez les composants au GridPane
        grid.add(new Label("Date de début:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(startHourComboBox, 2, 0);
        grid.add(startMinuteComboBox, 3, 0);
        grid.add(new Label("Date de fin:"), 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(endHourComboBox, 2, 1);
        grid.add(endMinuteComboBox, 3, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(startHourComboBox.getValue(), startMinuteComboBox.getValue()));
                LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(endHourComboBox.getValue(), endMinuteComboBox.getValue()));
                return new Pair<>(startDateTime, endDateTime);
            }
            return null;
        });

        Optional<Pair<LocalDateTime, LocalDateTime>> result = dialog.showAndWait();

        result.ifPresent(dateTimes -> {
            //System.out.println("Date de début sélectionnée : " + dateTimes.getKey());
            //System.out.println("Date de fin sélectionnée : " + dateTimes.getValue());

            String fullName = ConnexionController.currentUser.nom + " " + ConnexionController.currentUser.prenom;

            // Conversion en Date pour créer un Event
            ZonedDateTime startZonedDateTime = dateTimes.getKey().atZone(ZoneId.systemDefault());
            Date startDate = Date.from(startZonedDateTime.toInstant());

            ZonedDateTime endZonedDateTime = dateTimes.getValue().atZone(ZoneId.systemDefault());
            Date endDate = Date.from(endZonedDateTime.toInstant());

            String location = searchField.getText();

            location = getFullLocationName(location);

            Event event = new Event(startDate, endDate, fullName, location, "Réservation de salle", null, null);

            int dayColumnStart = dayOfWeekToColumn(dateTimes.getKey().getDayOfWeek());
            int startRow = timeToRow(LocalTime.from(dateTimes.getKey()));

            int dayColumnEnd = dayOfWeekToColumn(dateTimes.getValue().getDayOfWeek());
            int endRow = timeToRow(LocalTime.from(dateTimes.getValue()));

            boolean isRoomFree = true;

            for(int i = dayColumnStart; i <= dayColumnEnd; i++) {
                for(int j = startRow; j < endRow; j++) {
                    if(isSpaceOccupied(scheduleGridPane, i, j)) {
                        isRoomFree = false;
                    }
                }
            }

            if(isRoomFree) {
                events.add(event);
                updateWeekView();
                ConnexionController.mongoService.addReservation(ConnexionController.currentUser.id, event);
            }

        });
    }

    private String getFullLocationName(String location) {
        for(String s : parser.getDistinctLocation()) {
            if(s.contains(location)) {
                location = s; // on récupère l'intitulé complet de la salle
            }
        }
        return location;
    }

    @FXML
    private void handlePersonalEDTButton() {
        edtFormation.setValue(false);
        edtPerso.setValue(true);
        edtSalle.setValue(false);

        events.clear();
        Thread parserThread = new Thread(() -> {
            try {
                parser.loadDefaultURL();
                loadEvents();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                filterChoice.getItems().clear();
                ArrayList<String> distinctSubjects = parser.getDistinctSubjects();
                filterChoice.getItems().addAll(distinctSubjects);
                if(!distinctSubjects.isEmpty()) {
                    filterChoice.setValue(distinctSubjects.get(0));
                }
                updateWeekView();
            });
        });
        parserThread.start();
    }

    @FXML
    private void handleFilterButton() throws IOException, ParseException {
       
        events.clear();
        events.addAll(calendarCERI.getEvents());
        parser.filter(events, filterType.getValue().toString().strip(), filterChoice.getValue().toString().strip());
        updateWeekView();
    }

    @FXML
    private void handleSearchButton() throws IOException, ParseException {
        switch (searchBox.getValue().toString()) {
            case "Formation":
                parser.setFormationURL(searchField.getText());
                edtFormation.setValue(true);
                edtPerso.setValue(false);
                edtSalle.setValue(false);
                break;
            case "Enseignant":
                parser.setEnseignantURL(searchField.getText());
                edtFormation.setValue(false);
                edtPerso.setValue(false);
                edtSalle.setValue(false);
                break;
            case "Salle":
                parser.setSalleURL(searchField.getText());
                edtFormation.setValue(false);
                edtPerso.setValue(false);
                edtSalle.setValue(true);
                break;
            default:
                System.out.println("Sélection invalide ...");
        }
        events.clear();
        Thread parserThread = new Thread(() -> {
            try {
                loadEvents();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                filterChoice.getItems().clear();
                ArrayList<String> distinctSubjects = parser.getDistinctSubjects();
                filterChoice.getItems().addAll(distinctSubjects);
                if(!distinctSubjects.isEmpty()) {
                    filterChoice.setValue(distinctSubjects.get(0));
                }
                updateWeekView();
            });
        });
        parserThread.start();
    }

    private void loadReservations() {
        if (ConnexionController.currentUser.reservations != null) {
            for (Document d : ConnexionController.currentUser.reservations) {
                Date startDate = d.getDate("startDate");
                Date endDate = d.getDate("endDate");
                String location = d.getString("location");
                String fullName = ConnexionController.currentUser.nom + " " + ConnexionController.currentUser.prenom;

                if (edtSalle.get() && Objects.equals(getFullLocationName(searchField.getText()), location) || edtPerso.get()) { // Si la salle saisie est bien la salle recherchée
                    Event event = new Event(startDate, endDate, fullName, location, "Réservation de salle", null, null);
                    events.add(event);
                }
            }
            parser.filter(events, filterType.getValue().toString(), filterChoice.getValue().toString());
            System.out.println(events.size());


            RadioButton selectedRadioButton = (RadioButton) viewToggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                if (selectedRadioButton == radioButtonDay) {
                    updateDayView();
                } else if (selectedRadioButton == radioButtonWeek) {
                    updateWeekView();
                }
            }
        }
    }
    
    private void loadEvents() throws IOException, ParseException {
        calendarCERI = parser.getCalendarHeader();
        parser.getCalendarEvents(calendarCERI.getEvents());
        events.addAll(calendarCERI.getEvents());
        loadReservations();
    }

    private void setWeekViewColumnWidths() {
        int numberOfDaysInWeek = 7;
        setColumnWidths(numberOfDaysInWeek); // Pour la vue par semaine
    }
    
    // Méthode pour configurer les largeurs de colonnes pour la vue jour
    private void setDayViewColumnWidths() {
        int numberOfColumnsForDayView = 1;
        setColumnWidths(numberOfColumnsForDayView); // Pour la vue par jour
    }

    private void setupWeekdaysHeader() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRENCH);
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentMonday.plusDays(i);
            String headerText = date.format(formatter);
            Label dayLabel = new Label(headerText);
            
            // Utilisez une expression lambda pour passer la date à la méthode onDayHeaderClicked
            dayLabel.setOnMouseClicked(e -> onDayHeaderClicked(date));
            
            scheduleGridPane.add(dayLabel, i + 1, 0);
            GridPane.setMargin(dayLabel, new Insets(0, 0, 0, 125));
        }
    }
    
    private void onDayHeaderClicked(LocalDate date) {
        radioButtonDay.setSelected(true); // Sélectionnez le bouton radio Jour
        currentDay = date;
        updateDayView(); // Mettez à jour la vue pour le jour sélectionné
        
        // Mettez à jour les libellés et les actions des boutons
        updateButtonLabels("Jour précédent", "Jour suivant", "Jour d'aujourd'hui");
        previousWeekButton.setOnAction(e -> loadPreviousDay());
        nextWeekButton.setOnAction(e -> loadNextDay());
        currentWeekButton.setOnAction(e -> loadCurrentDay());
    }

private void displayDayView(LocalDate l) {
    clearGridPane();
    createDefaultTimeSlots();
    setupDayHeader(l);
    displayEventsForDay(l);
  
}
/*private void setupMonthView() {
    clearGridPane();
    setWeekViewColumnWidths(); 

    LocalDate firstDayOfMonth = currentMonth.with(TemporalAdjusters.firstDayOfMonth());
    int dayOfWeekOffset = firstDayOfMonth.getDayOfWeek().getValue() % 7; 
    for (int i = 0; i < dayOfWeekOffset; i++) {
        scheduleGridPane.add(new Label(""), i, 1); // Ajoutez des cellules vides au besoin
    }

    LocalDate currentDate = firstDayOfMonth;
    while (currentDate.getMonth() == currentMonth.getMonth()) {
        int dayOfWeek = currentDate.getDayOfWeek().getValue() % 7;
        int weekOfMonth = (int) ChronoUnit.WEEKS.between(firstDayOfMonth, currentDate) + 1;

        int eventCount = getEventCountForDate(currentDate);
        VBox dayBox = new VBox(new Label(Integer.toString(currentDate.getDayOfMonth())), getEventIndicators(eventCount));
        dayBox.setOnMouseClicked(e -> onDayBoxClicked(currentDate));

        scheduleGridPane.add(dayBox, dayOfWeek, weekOfMonth);
        currentDate = currentDate.plusDays(1);
    }
}
*/
private int getEventCountForDate(LocalDate date) {
    // Votre logique pour obtenir le nombre d'événements pour une date spécifique
    // Cela peut nécessiter d'interroger votre source de données / backend / liste d'événements
    return 0; // Retournez le nombre réel d'événements ici
}

private Node getEventIndicators(int eventCount) {
    HBox indicators = new HBox();
    for (int i = 0; i < eventCount; i++) {
        Circle circle = new Circle(5);
        circle.setFill(Color.BLUE);
        indicators.getChildren().add(circle);
    }
    return indicators;
}

private void onDayBoxClicked(LocalDate date) {
    // Logic to transition to the week view for the selected date
    System.out.println("Day box clicked: " + date);
    // Potentially set currentMonday to the start of the week for this date
    // and update the view to week view
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
    scheduleGridPane.add(dayLabel, 1, 0);
    GridPane.setMargin(dayLabel, new Insets(0, 0, 0, 125));
}
private void updateDayView() {
    clearGridPane();
    createDefaultTimeSlots();
    setupDayHeader(currentDay);
    displayEventsForDay(currentDay);
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
private void loadCurrentDay() {
    currentDay = LocalDate.now();
    updateDayView();
}


    private void displayEvents() {
        for (Event event : events) {
            if (event.getStartDate() == null) {
                //System.out.println("Date nulle détectée...");
                continue; // on passe à l'évènement suivant
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
            //scene.getStylesheets().add(getClass().getResource("/lightmode.css").toExternalForm()); // version Junior
            scene.getStylesheets().add(getClass().getResource("lightmode.css").toExternalForm()); // version Walid
        }
         ConnexionController.updateMode("light");
    }

    public void applyDarkMode() {
        // Même chose pour le mode sombre.
        Scene scene = scheduleGridPane.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            // scene.getStylesheets().add(getClass().getResource("/darkmode.css").toExternalForm()); // version Junior
            scene.getStylesheets().add(getClass().getResource("darkmode.css").toExternalForm()); // version Walid
        }
        ConnexionController.updateMode("dark");
    }

    private void addEventToGridday(Event event, LocalDate displayDate) {
        LocalDate date = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

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
        scheduleGridPane.add(eventBox, dayColumn, startRow, 4, durationInHalfHours);
        GridPane.setValignment(eventBox, VPos.TOP);
        GridPane.setMargin(eventBox, new Insets(MIN_HEIGHT_PER_HALF_HOUR / 2, 0, 0, 100));
    }

    private void addEventToGrid(Event event) {
        LocalDate date = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime startTime = event.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = event.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        int dayColumn = dayOfWeekToColumn(date.getDayOfWeek());
        int startRow = timeToRow(startTime);
        int durationInHalfHours = (int) Duration.between(startTime, endTime).toMinutes() / 30;

        String[] teachers = List.of("").toArray(new String[0]);
        if(event.getTeacher() != null) {
            teachers = event.getTeacher().split(","); // Séparez les noms des enseignants en utilisant la virgule comme délimiteur
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
        scheduleGridPane.getColumnConstraints().add(new ColumnConstraints()); 
        for (int i = 0; i < 7; i++) {
            scheduleGridPane.getColumnConstraints().add(columnConstraints);
        }
    }
    private void setColumnWidths(int numberOfColumns) {
        scheduleGridPane.getColumnConstraints().clear(); 
        double columnWidthPercentage = 100.0 / numberOfColumns;
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(columnWidthPercentage);
        for (int i = 0; i < numberOfColumns; i++) {
            scheduleGridPane.getColumnConstraints().add(columnConstraints);
        }
    }

}

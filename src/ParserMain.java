import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ParserMain {
    public static void main(String[] args) {
        ParserTest parser = new ParserTest();
        try {
            CalendarCERI calendarCERI = parser.getCalendarHeader();
            parser.getCalendarEvents(calendarCERI.getEvents());
            System.out.println(calendarCERI.getEvents().size());
            saveEventsToFile(calendarCERI, "events.txt");
        } catch (IOException | ParseException e) {
            System.out.println("Erreur !");
        }
    }

    private static void saveEventsToFile(CalendarCERI calendarCERI, String fileName) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Event event : calendarCERI.getEvents()) {
                // Vérification que les dates ne sont pas null
                if (event.getStartDate() == null || event.getEndDate() == null) {
                    // Gérer le cas où une date est null, par exemple en sautant l'événement ou en notant une valeur par défaut
                    continue; // ici, on saute simplement cet événement
                }
                
                String eventDetails = String.format("Début : %s, Fin : %s, Enseignant : %s, Lieu : %s, Sujet : %s, Type : %s",
                        dateFormat.format(event.getStartDate()),
                        dateFormat.format(event.getEndDate()),
                        event.getTeacher() != null ? event.getTeacher() : "Inconnu",
                        event.getLocation() != null ? event.getLocation() : "Lieu inconnu",
                        event.getSubject() != null ? event.getSubject() : "Sujet inconnu",
                        event.getType() != null ? event.getType() : "Type inconnu");
                writer.write(eventDetails);
                writer.newLine();
            }
        }
    }
    
}
import java.io.IOException;
import java.text.ParseException;

public class ParserMain {
    public static void main(String[] args) {
        ParserTest parser = new ParserTest();
        try {
            CalendarCERI calendarCERI = parser.getCalendarHeader();
            parser.getCalendarEvents(calendarCERI);
            parser.filterBySubject(calendarCERI, "Group", "ILSEN");
        } catch (IOException | ParseException e) {
            System.out.println("Erreur !");
        }
    }
}

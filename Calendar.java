import java.util.ArrayList;
import java.util.Date;

public class Calendar {
    public Date startDate;
    public Date endDate;
    public String course;
    public ArrayList<Event> events;
    public Calendar(Date startDate, Date endDate, String course) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.course = course;
        events = new ArrayList<Event>();
    }
}

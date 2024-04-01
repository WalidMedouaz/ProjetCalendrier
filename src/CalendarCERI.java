import java.util.ArrayList;
import java.util.Date;

public class CalendarCERI {
    private Date startDate;
    private Date endDate;
    private String course;
    private ArrayList<Event> events;
    public CalendarCERI(Date startDate, Date endDate, String course) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.course = course;
        events = new ArrayList<Event>();
    }
    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCourse() {
        return course;
    }
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
import java.util.Date;

public class Event {
    public Date startDate;
    public Date endDate;
    public String summary;
    public String location;
    public String description;
    public Event(Date startDate, Date endDate, String summary, String location, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.summary = summary;
        this.location = location;
        this.description = description;
    }
}

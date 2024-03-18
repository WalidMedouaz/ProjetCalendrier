import java.util.Date;

public class Event {
    private Date startDate;
    private Date endDate;
    private String teacher;
    private String group;
    private String location;
    private String subject;
    private String type;

    public Event(Date startDate, Date endDate, String teacher, String location, String subject, String type, String group) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.teacher = teacher;
        this.location = location;
        this.subject = subject;
        this.type = type;
        this.group = group;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getLocation() {
        return location;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Start date : " + startDate + "\n" +
                "End date : " + endDate + "\n" +
                "Teacher : " + teacher + "\n" +
                "Location : " + location + "\n" +
                "Subject : " + subject + "\n" +
                "Type : " + type + "\n" +
                "Group " + group + "\n";
    }
}

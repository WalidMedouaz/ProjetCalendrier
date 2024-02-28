import java.util.Date;

public class Event {
    private Date startDate;
    private Date endDate;
    private String teacher;
    private String location;
    private String classroom;
    private String subject;
    public Event(Date startDate, Date endDate, String teacher, String location, String classroom) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.teacher = teacher;
        this.location = location;
        this.classroom = classroom;
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

    public String getClassroom() {
        return classroom;
    }
    public String getSubject() {
        return subject;
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

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

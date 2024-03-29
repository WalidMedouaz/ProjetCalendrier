import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.Date;

public class Event {
    private final ObjectProperty<Date> startDate;
    private final ObjectProperty<Date> endDate;
    private final SimpleStringProperty teacher;
    private final SimpleStringProperty location;
    private final SimpleStringProperty subject;
    private final SimpleStringProperty type;
    private final SimpleStringProperty group;

    public Event(Date startDate, Date endDate, String teacher, String location, String subject, String type, String group) {
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.teacher = new SimpleStringProperty(teacher);
        this.location = new SimpleStringProperty(location);
        this.subject = new SimpleStringProperty(subject);
        this.type = new SimpleStringProperty(type);
        this.group = new SimpleStringProperty(group);
    }

    // StartDate
    public Date getStartDate() {
        return startDate.get();
    }

    public void setStartDate(Date startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<Date> startDateProperty() {
        return startDate;
    }

    // EndDate
    public Date getEndDate() {
        return endDate.get();
    }

    public void setEndDate(Date endDate) {
        this.endDate.set(endDate);
    }

    public ObjectProperty<Date> endDateProperty() {
        return endDate;
    }

    // Teacher
    public String getTeacher() {
        return teacher.get();
    }

    public void setTeacher(String teacher) {
        this.teacher.set(teacher);
    }

    public SimpleStringProperty teacherProperty() {
        return teacher;
    }

    // Location
    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    // Subject
    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    // Type
    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    // Group
    public String getGroup() {
        return group.get();
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    public SimpleStringProperty groupProperty() {
        return group;
    }
}

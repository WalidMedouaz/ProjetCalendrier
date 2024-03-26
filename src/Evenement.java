import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evenement {
private String summary;
    private String location;
    private String description;
    private String categories;
    private String uid;
    private LocalDateTime dtStart;
    private LocalDateTime dtEnd;
    private LocalDateTime dtStamp;
    private LocalDateTime lastModified;
    public Evenement() {
        // Initialisation des attributs à des valeurs par défaut ou à null
        this.summary = "";
        this.location = "";
        this.description = "";
        this.categories = "";
        this.uid = "";
        this.dtStart = null;
        this.dtEnd = null;
        this.dtStamp = null;
        this.lastModified = null;
    }
    
    public Evenement(String summary, String location, String description, String uid, String uid2, LocalDateTime dtStart, LocalDateTime dtEnd, LocalDateTime dtStamp, LocalDateTime lastModified) {
        this.summary = summary;
        this.location = location;
        this.description = description;
        this.uid = uid;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.dtStamp = dtStamp;
        this.lastModified = lastModified;
    }
    // Getters et setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }
     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Summary: ").append(summary).append("\n");
        sb.append("Location: ").append(location).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Categories: ").append(categories).append("\n");
        sb.append("UID: ").append(uid).append("\n");
        sb.append("Start Time: ").append(formatDateTime(dtStart)).append("\n");
        sb.append("End Time: ").append(formatDateTime(dtEnd)).append("\n");
        sb.append("Timestamp: ").append(formatDateTime(dtStamp)).append("\n");
        sb.append("Last Modified: ").append(formatDateTime(lastModified)).append("\n");
        return sb.toString();
    }

    // Méthode utilitaire pour formater une LocalDateTime en une chaîne de caractères
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public LocalDateTime getDtStart() {
        return dtStart;
    }

    public void setDtStart(LocalDateTime dtStart) {
        this.dtStart = dtStart;
    }

    public LocalDateTime getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(LocalDateTime dtEnd) {
        this.dtEnd = dtEnd;
    }

    public LocalDateTime getDtStamp() {
        return dtStamp;
    }

    public void setDtStamp(LocalDateTime dtStamp) {
        this.dtStamp = dtStamp;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }


}

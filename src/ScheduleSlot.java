import javafx.beans.property.SimpleStringProperty;

public class ScheduleSlot {
    private SimpleStringProperty timeSlot;
    private SimpleStringProperty mondayDetails;
    private SimpleStringProperty tuesdayDetails;
    private SimpleStringProperty wednesdayDetails;
    private SimpleStringProperty thursdayDetails;
    private SimpleStringProperty fridayDetails;
    private SimpleStringProperty saturdayDetails;
    private SimpleStringProperty sundayDetails;

    public ScheduleSlot(String timeSlot, String mondayDetails, String tuesdayDetails, 
                        String wednesdayDetails, String thursdayDetails, 
                        String fridayDetails, String saturdayDetails, String sundayDetails) {
        this.timeSlot = new SimpleStringProperty(timeSlot);
        this.mondayDetails = new SimpleStringProperty(mondayDetails);
        this.tuesdayDetails = new SimpleStringProperty(tuesdayDetails);
        this.wednesdayDetails = new SimpleStringProperty(wednesdayDetails);
        this.thursdayDetails = new SimpleStringProperty(thursdayDetails);
        this.fridayDetails = new SimpleStringProperty(fridayDetails);
        this.saturdayDetails = new SimpleStringProperty(saturdayDetails);
        this.sundayDetails = new SimpleStringProperty(sundayDetails);
    }

    // TimeSlot getters and setters
    public String getTimeSlot() {
        return timeSlot.get();
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot.set(timeSlot);
    }

    public SimpleStringProperty timeSlotProperty() {
        return timeSlot;
    }

    // MondayDetails getters and setters
    public String getMondayDetails() {
        return mondayDetails.get();
    }

    public void setMondayDetails(String mondayDetails) {
        this.mondayDetails.set(mondayDetails);
    }

    public SimpleStringProperty mondayDetailsProperty() {
        return mondayDetails;
    }

    // TuesdayDetails getters and setters
    public String getTuesdayDetails() {
        return tuesdayDetails.get();
    }

    public void setTuesdayDetails(String tuesdayDetails) {
        this.tuesdayDetails.set(tuesdayDetails);
    }

    public SimpleStringProperty tuesdayDetailsProperty() {
        return tuesdayDetails;
    }

    // WednesdayDetails getters and setters
    public String getWednesdayDetails() {
        return wednesdayDetails.get();
    }

    public void setWednesdayDetails(String wednesdayDetails) {
        this.wednesdayDetails.set(wednesdayDetails);
    }

    public SimpleStringProperty wednesdayDetailsProperty() {
        return wednesdayDetails;
    }

    // ThursdayDetails getters and setters
    public String getThursdayDetails() {
        return thursdayDetails.get();
    }

    public void setThursdayDetails(String thursdayDetails) {
        this.thursdayDetails.set(thursdayDetails);
    }

    public SimpleStringProperty thursdayDetailsProperty() {
        return thursdayDetails;
    }

    // FridayDetails getters and setters
    public String getFridayDetails() {
        return fridayDetails.get();
    }

    public void setFridayDetails(String fridayDetails) {
        this.fridayDetails.set(fridayDetails);
    }

    public SimpleStringProperty fridayDetailsProperty() {
        return fridayDetails;
    }

    // SaturdayDetails getters and setters
    public String getSaturdayDetails() {
        return saturdayDetails.get();
    }

    public void setSaturdayDetails(String saturdayDetails) {
        this.saturdayDetails.set(saturdayDetails);
    }

    public SimpleStringProperty saturdayDetailsProperty() {
        return saturdayDetails;
    }

    // SundayDetails getters and setters
    public String getSundayDetails() {
        return sundayDetails.get();
    }

    public void setSundayDetails(String sundayDetails) {
        this.sundayDetails.set(sundayDetails);
    }

    public SimpleStringProperty sundayDetailsProperty() {
        return sundayDetails;
    }
}


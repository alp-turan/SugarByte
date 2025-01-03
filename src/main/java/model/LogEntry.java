package model;

/**
 * Represents a single logbook entry for a user.
 * Includes data for blood sugar, carbs, exercise, etc.
 */
public class LogEntry {
    private int id;
    private int userId;
    private String date;           // e.g. "2024-12-23"
    private String timeOfDay;      // e.g. "Breakfast", "Lunch", ...
    private double bloodSugar;
    private double carbsEaten;
    private int hoursSinceMeal;
    private String foodDetails;
    private String exerciseType;
    private int exerciseDuration;  // in minutes
    private double insulinDose;
    private String otherMedications;

    public LogEntry() {
        // No-arg constructor
    }

    public LogEntry(int userId,
                    String date,
                    String timeOfDay,
                    double bloodSugar,
                    double carbsEaten,
                    int hoursSinceMeal,
                    String foodDetails,
                    String exerciseType,
                    int exerciseDuration,
                    double insulinDose,
                    String otherMedications) {
        this.userId = userId;
        this.date = date;
        this.timeOfDay = timeOfDay;
        this.bloodSugar = bloodSugar;
        this.carbsEaten = carbsEaten;
        this.hoursSinceMeal = hoursSinceMeal;
        this.foodDetails = foodDetails;
        this.exerciseType = exerciseType;
        this.exerciseDuration = exerciseDuration;
        this.insulinDose = insulinDose;
        this.otherMedications = otherMedications;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public double getBloodSugar() {
        return bloodSugar;
    }
    public void setBloodSugar(double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public double getCarbsEaten() {
        return carbsEaten;
    }
    public void setCarbsEaten(double carbsEaten) {
        this.carbsEaten = carbsEaten;
    }

    public double getHoursSinceMeal() {
        return hoursSinceMeal;
    }
    public void setHoursSinceMeal(int hoursSinceMeal) {
        this.hoursSinceMeal = hoursSinceMeal;
    }

    public String getFoodDetails() {
        return foodDetails;
    }
    public void setFoodDetails(String foodDetails) {
        this.foodDetails = foodDetails;
    }

    public String getExerciseType() {
        return exerciseType;
    }
    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getExerciseDuration() {
        return exerciseDuration;
    }
    public void setExerciseDuration(int exerciseDuration) {
        this.exerciseDuration = exerciseDuration;
    }

    public double getInsulinDose() {
        return insulinDose;
    }
    public void setInsulinDose(double insulinDose) {
        this.insulinDose = insulinDose;
    }

    public String getOtherMedications() {
        return otherMedications;
    }
    public void setOtherMedications(String otherMedications) {
        this.otherMedications = otherMedications;
    }
}
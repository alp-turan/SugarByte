package model;

/**
 * Simple model for the user, including personal + doctor info.
 */
public class User {
    // Private instance variables ensure encapsulation, a core OOP principle
    // These comments may be an over-explanation, as the variable names are quite self-explanatory, but including them
    // for good practice anyway
    private int id; // An integer to uniquely identify the user in the database or system
    private String name; // Stores the user's name, a mandatory detail for personalization
    private String diabetesType; // Tracks the type of diabetes (e.g., Type 1 or Type 2) for relevant data entry
    private String insulinType; // Specifies the type of insulin the user uses (e.g., rapid-acting, long-acting)
    private String insulinAdmin; // Indicates the method of insulin administration (e.g., pen, pump, injection)
    private String email; // Email is the primary contact and authentication detail for the user
    private String phone; // Stores the user's phone number, typically for secondary contact or alerts
    private String doctorName; // Holds the name of the user's primary healthcare provider
    private String doctorEmail; // Represents the doctor's email for communication purposes
    private String logbookType; // Tracks the logbook type ("Simple", "Comprehensive", etc.)
    private String doctorAddress; // Captures the doctor's physical address for detailed reference
    private String doctorEmergencyPhone; // Emergency contact for the doctor in critical scenarios
    private String password; // Stores the user's hashed password for authentication (security critical)

    // ===== Getters & Setters =====

    /**
     * Retrieves the user's unique ID.
     *
     * @return the ID of the user
     */
    public int getId() {
        return id; // Directly returning the private `id` field
    }

    public void setId(int id) {
        this.id = id; // The `this` keyword resolves field-shadowing when parameter names match field names
    }

    public String getName() {
        return name; // Provides the user's name for display or processing
    }

    public void setName(String name) {
        this.name = name; // Assigning the provided value to the private `name` field
    }

    public String getDiabetesType() {
        return diabetesType; // Fetching the type of diabetes stored for this user
    }

    public void setDiabetesType(String diabetesType) {
        this.diabetesType = diabetesType; // Replacing the old diabetes type with the provided value
    }

    public String getInsulinType() {
        return insulinType; // Returning the type of insulin prescribed to the user
    }

    public void setInsulinType(String insulinType) {
        this.insulinType = insulinType; // Updating the insulin type for the user
    }

    public String getInsulinAdmin() {
        return insulinAdmin; // Getting the method of insulin administration (e.g., pump)
    }

    public void setInsulinAdmin(String insulinAdmin) {
        this.insulinAdmin = insulinAdmin; // Setting a new insulin administration method
    }

    public String getEmail() {
        return email; // Accessing the user's email for communication or login
    }

    public void setEmail(String email) {
        this.email = email; // Assigning a new email address to the user
    }

    public String getPhone() {
        return phone; // Returns the stored phone number
    }

    public void setPhone(String phone) {
        this.phone = phone; // Updates the phone field with a new value
    }

    public String getDoctorEmail() {
        return doctorEmail; // Retrieves the email of the doctor linked to this user
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail; // Assigning the provided doctor email to the field
    }

    public String getDoctorAddress() {
        return doctorAddress; // Fetching the physical address of the user's doctor
    }

    public void setDoctorAddress(String doctorAddress) {
        this.doctorAddress = doctorAddress; // Storing a new address for the user's doctor
    }

    public String getDoctorEmergencyPhone() {
        return doctorEmergencyPhone; // Returning the emergency phone number of the doctor
    }

    public void setDoctorEmergencyPhone(String doctorEmergencyPhone) {
        this.doctorEmergencyPhone = doctorEmergencyPhone; // Updating the emergency contact for the doctor
    }

    public String getPassword() {
        return password; // Password retrieval for hashing or validation (never plain-text)
    }

    public void setPassword(String password) {
        this.password = password; // Assigning a secure password for the user
    }

    public String getDoctorName() {
        return doctorName; // Accessing the name of the healthcare provider
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName; // Updating the doctor's name with a new value
    }

    public String getLogbookType() {
        return logbookType; // Fetching the type of logbook associated with the user
    }

    public void setLogbookType(String logbookType) {
        this.logbookType = logbookType; // Setting or modifying the logbook type for the user
    }
}


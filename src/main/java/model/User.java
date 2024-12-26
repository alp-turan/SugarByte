package model;

/**
 * Simple model for the user, including personal + doctor info.
 */
public class User {
    private int id;
    private String name;
    private String diabetesType;
    private String insulinType;
    private String insulinAdmin;
    private String email;
    private String phone;
    private String doctorEmail;
    private String doctorAddress;
    private String doctorEmergencyPhone;
    private String password;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDiabetesType() { return diabetesType; }
    public void setDiabetesType(String diabetesType) { this.diabetesType = diabetesType; }

    public String getInsulinType() { return insulinType; }
    public void setInsulinType(String insulinType) { this.insulinType = insulinType; }

    public String getInsulinAdmin() { return insulinAdmin; }
    public void setInsulinAdmin(String insulinAdmin) { this.insulinAdmin = insulinAdmin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }

    public String getDoctorAddress() { return doctorAddress; }
    public void setDoctorAddress(String doctorAddress) { this.doctorAddress = doctorAddress; }

    public String getDoctorEmergencyPhone() { return doctorEmergencyPhone; }
    public void setDoctorEmergencyPhone(String doctorEmergencyPhone) { this.doctorEmergencyPhone = doctorEmergencyPhone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

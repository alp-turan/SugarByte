package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class Profile extends BaseUI {

    private User currentUser;

    public Profile(User user) {
        this();
        this.currentUser = user;
    }

    public Profile() {
        super("Profile");

        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Load fonts
        Font lobsterFont  = loadCustomFont("/Fonts/Lobster.ttf", 38f);
        Font poppinsBold   = new Font("SansSerif", Font.BOLD, 16);
        Font poppinsNormal = new Font("SansSerif", Font.PLAIN, 14);

        // Title Label: "SugarByte"
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Panel for User and Doctor Information
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // “User’s Information” Section
        JPanel userPanel = createInfoSection(
                "User’s Information",
                poppinsBold.deriveFont(14f),
                poppinsNormal,
                new String[]{
                        "Full Name",
                        "Username/Email",
                        "Type of Diabetes",
                        "Type of Insulin",
                        "Insulin Administration",
                        "Phone Number",
                        "Password"
                },
                new String[]{
                        (currentUser != null) ? currentUser.getName() : "Name",
                        (currentUser != null) ? currentUser.getEmail() : "Email",
                        (currentUser != null) ? currentUser.getDiabetesType() : "Type 1",
                        (currentUser != null) ? currentUser.getInsulinType() : "Rapid-acting",
                        (currentUser != null) ? currentUser.getInsulinAdmin() : "Pen",
                        (currentUser != null) ? currentUser.getPhone() : "07498375960",
                        (currentUser != null) ? "**********" : "**********"
                }
        );

        // “Doctor’s Information” Section
        JPanel doctorPanel = createInfoSection(
                "Doctor’s Information",
                poppinsBold.deriveFont(14f),
                poppinsNormal,
                new String[]{
                        "Doctor Email",
                        "Address",
                        "Emergency Phone"
                },
                new String[]{
                        (currentUser != null) ? currentUser.getDoctorEmail() : "namesurname@gmail.com",
                        (currentUser != null) ? currentUser.getDoctorAddress() : "City, Street, Flat, Postcode",
                        (currentUser != null) ? currentUser.getDoctorEmergencyPhone() : "07287567281"
                }
        );

        userPanel.setOpaque(true);
        doctorPanel.setOpaque(true);
        userPanel.setBackground(Color.LIGHT_GRAY);
        doctorPanel.setBackground(Color.LIGHT_GRAY);
        centerPanel.add(userPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(doctorPanel);
        centerPanel.add(Box.createVerticalGlue());
        JPanel navBar = createBottomNavBar("Profile", currentUser,"/Icons/home.png","/Icons/logbook.png","/Icons/profilefull.png"); // Pass current screen name
        mainPanel.add(navBar, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            dummyUser.setEmail("mark@example.com");
            dummyUser.setDiabetesType("Type 1");
            dummyUser.setInsulinType("Rapid-acting");
            dummyUser.setInsulinAdmin("Pen");
            dummyUser.setPhone("07498375960");
            dummyUser.setDoctorEmail("doctor@example.com");
            dummyUser.setDoctorAddress("City, Street, Flat, Postcode");
            dummyUser.setDoctorEmergencyPhone("07287567281");
            new Profile(dummyUser);
        });
    }
}

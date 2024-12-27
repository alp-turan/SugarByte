package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class Profile extends BaseUI {

    private User currentUser;

    public Profile(User user) {
        super("Profile");
        this.currentUser = user;

        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Load fonts
        Font lobsterFont = loadCustomFont(38f);
        Font poppinsBold = new Font("SansSerif", Font.BOLD, 16);
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

        // User Information Section
        centerPanel.add(createInfoSection(
                "User’s Information",
                poppinsBold.deriveFont(14f),
                poppinsNormal,
                new String[]{
                        "Full Name", "Username/Email", "Type of Diabetes", "Type of Insulin", "Insulin Administration", "Phone Number", "Password"
                },
                new String[]{
                        safeValue(currentUser, User::getName, "Name"),
                        safeValue(currentUser, User::getEmail, "Email"),
                        safeValue(currentUser, User::getDiabetesType, "Type 1"),
                        safeValue(currentUser, User::getInsulinType, "Rapid-acting"),
                        safeValue(currentUser, User::getInsulinAdmin, "Pen"),
                        safeValue(currentUser, User::getPhone, "07498375960"),
                        "**********"
                }
        ));

        centerPanel.add(Box.createVerticalStrut(20));

        // Doctor Information Section
        centerPanel.add(createInfoSection(
                "Doctor’s Information",
                poppinsBold.deriveFont(14f),
                poppinsNormal,
                new String[]{"Doctor's Name","Doctor Email", "Address", "Emergency Phone"},
                new String[]{
                        safeValue(currentUser, User::getDoctorName, "Name Surname"),
                        safeValue(currentUser, User::getDoctorEmail, "namesurname@gmail.com"),
                        safeValue(currentUser, User::getDoctorAddress, "City, Street, Flat, Postcode"),
                        safeValue(currentUser, User::getDoctorEmergencyPhone, "07287567281")
                }
        ));

        centerPanel.add(Box.createVerticalGlue());

        // Bottom Navigation Bar
        JPanel navBar = createBottomNavBar(
                "Profile", currentUser, "/Icons/home.png", "/Icons/logbook.png", "/Icons/profilefull.png"
        );
        mainPanel.add(navBar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String safeValue(User user, java.util.function.Function<User, String> getter, String defaultValue) {
        return (user != null && getter.apply(user) != null) ? getter.apply(user) : defaultValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            new Calendar(dummyUser);
        });
    }
}

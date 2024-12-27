package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Home extends BaseUI {

    private JLabel greetingLabel;

    public Home(User user) {
        super("Home"); // Call BaseUI constructor with the title
        this.currentUser = user; // Assign the current user
        buildUI();

        // Set greeting to user's name
        if (greetingLabel != null && currentUser != null) {
            greetingLabel.setText("Hi, " + currentUser.getName());
        }

        setVisible(true);
    }

    private void buildUI() {
        // Initialize with the current date
        LocalDate today = LocalDate.now();

        // Main panel with BoxLayout to stack components vertically
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(255, 255, 255)); // Using BaseUI's method
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        // =======================
        // Top Content
        // =======================

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false); // Transparent to show gradient
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Title Label: "SugarByte"
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Using BaseUI's method
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0); // Top padding
        topPanel.add(titleLabel, gbc);

        // Date Label: e.g., "Monday, 1 Jan"
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM"));
        JLabel dateLabel = new JLabel(formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        dateLabel.setForeground(new Color(200, 40, 40));

        gbc.gridy = 1; // Position the date label
        gbc.insets = new Insets(0, 0, 20, 0); // Padding below title
        topPanel.add(dateLabel, gbc);

        mainPanel.add(topPanel); // Added directly to mainPanel for vertical stacking

        // =======================
        // Center Content
        // =======================

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Transparent to show gradient
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Greeting and Reminder Panel
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);

        greetingLabel = new JLabel("Hi, Name");
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        greetingLabel.setForeground(Color.BLACK);

        JLabel greenCircle = new JLabel();
        greenCircle.setOpaque(true);
        greenCircle.setBackground(new Color(0, 128, 0)); // Green color
        greenCircle.setPreferredSize(new Dimension(10, 10)); // Circle size
        greenCircle.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        greetingPanel.add(greetingLabel, BorderLayout.WEST);
        greetingPanel.add(greenCircle, BorderLayout.EAST);

        gbc.gridy = 2; // Position greeting directly below the date label
        gbc.insets = new Insets(5, 20, 0, 20); // Padding between date and greeting
        centerPanel.add(greetingPanel, gbc);

        JLabel reminderLabel = new JLabel("It’s meal time soon, don’t forget to log your values");
        reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reminderLabel.setForeground(new Color(0x88, 0x88, 0x88));

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 200, 20); // Padding around reminder
        centerPanel.add(reminderLabel, gbc);

        // Quick Log Panel (Placeholder)
        JPanel quickLogPanel = createQuickLogPanel();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 0, 20); // Padding below quick log
        centerPanel.add(quickLogPanel, gbc);

        // Logbook Button
        String logbookLabel = "Logbook for " + today.format(DateTimeFormatter.ofPattern("d MMM"));
        RoundedButton logbookButton = new RoundedButton(logbookLabel, new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = 5;
        gbc.insets = new Insets(50, 20, 200, 20); // Bottom padding before nav bar
        centerPanel.add(logbookButton, gbc);

        mainPanel.add(centerPanel); // Added directly to mainPanel for vertical stacking

        // =======================
        // Bottom Navigation Bar
        // =======================

        JPanel navBar = createBottomNavBar("Home", currentUser, "/Icons/homefull.png", "/Icons/logbook.png", "/Icons/profile.png");
        mainPanel.add(navBar); // Added directly to mainPanel for vertical stacking
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            new Home(dummyUser);
        });
    }
}

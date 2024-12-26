package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The Home screen of the SugarByte application.
 * Displays user greetings and includes a bottom navigation bar
 * to switch between Home, Calendar, and Profile pages.
 */
public class Home extends BaseUI {

    private User currentUser;
    private JLabel greetingLabel;

    /**
     * Constructor that accepts a User object.
     *
     * @param user The currently logged-in user.
     */
    public Home(User user) {
        super("Home"); // Call BaseUI constructor with the title
        this.currentUser = user;
        buildUI();

        // Set greeting to user's name
        if (greetingLabel != null && currentUser != null) {
            greetingLabel.setText("Hi, " + currentUser.getName());
        }

        setVisible(true);
    }

    /**
     * No-argument constructor.
     * Useful if you need to instantiate Home without a User.
     */
    public Home() {
        super("Home"); // Call BaseUI constructor with the title
        buildUI();
        setVisible(true);
    }

    /**
     * Builds the UI components of the Home screen.
     */
    private void buildUI() {
        // Initialize with the current date
        LocalDate today = LocalDate.now();

        // Main panel with BorderLayout to organize components
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240)); // Using BaseUI's method
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // =======================
        // Top and Center Content
        // =======================

        // Panel to hold top and center content using GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Transparent to show gradient
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;


        // Title Label: "SugarByte"
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK); // Using BaseUI's method
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 10, 0); // Top padding
        contentPanel.add(titleLabel, gbc);

        // Date Label: e.g., "Monday, 1 Jan"
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM"));
        JLabel dateLabel = new JLabel(formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        dateLabel.setForeground(new Color(200, 40, 40));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Between title and date
        contentPanel.add(dateLabel, gbc);

        // Greeting and Reminder Panel
        greetingLabel = new JLabel("Hi, Name", SwingConstants.LEFT);
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        greetingLabel.setForeground(Color.BLACK);

        JLabel reminderLabel = new JLabel("It’s meal time soon, don’t forget to log your values", SwingConstants.LEFT);
        reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reminderLabel.setForeground(new Color(0x88, 0x88, 0x88));

        JPanel greetingPanel = new JPanel();
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));
        greetingPanel.setOpaque(false);
        greetingPanel.add(greetingLabel);
        greetingPanel.add(reminderLabel);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 20, 0); // Padding around greeting
        contentPanel.add(greetingPanel, gbc);

        // Quick Log Panel (Placeholder)
        JPanel quickLogPanel = createQuickLogPanel();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0); // Padding below quick log
        contentPanel.add(quickLogPanel, gbc);

        // Logbook Button
        String logbookLabel = "Logbook for " + today.format(DateTimeFormatter.ofPattern("d MMM"));
        RoundedButton logbookButton = new RoundedButton(logbookLabel, new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(220, 40));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 30, 0); // Bottom padding before nav bar
        contentPanel.add(logbookButton, gbc);

        // Add contentPanel to the center of mainPanel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // =====================
        // Bottom Navigation Bar
        // =====================

        // Create navigation bar panel using BaseUI's method
        JPanel navBar = createBottomNavBar();
        mainPanel.add(navBar, BorderLayout.SOUTH);
    }

    /**
     * Creates the Quick Log panel.
     * Replace the placeholder with your actual quick log components.
     *
     * @return A JPanel representing the Quick Log section.
     */
    private JPanel createQuickLogPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false); // Transparent to show gradient
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Adjust layout as needed

        // Example components - replace with your actual quick log inputs/buttons
        JButton quickLogButton = new JButton("Quick Log");
        quickLogButton.setBackground(new Color(237, 165, 170));
        quickLogButton.setForeground(Color.BLACK);
        quickLogButton.setFocusPainted(false);
        quickLogButton.setBorderPainted(false);
        quickLogButton.setPreferredSize(new Dimension(150, 40));
        quickLogButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Add action listener to Quick Log button
        quickLogButton.addActionListener(e -> {
            // Handle quick log action
            JOptionPane.showMessageDialog(this, "Quick Log Clicked!", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            // Implement your quick log functionality here
        });

        panel.add(quickLogButton);
        return panel;
    }

    /**
     * Creates the bottom navigation bar with Home, Calendar, and Profile buttons.
     *
     * @return A JPanel representing the bottom navigation bar.
     */
    private JPanel createBottomNavBar() {
        JPanel navBar = new JPanel();
        navBar.setOpaque(false); // Transparent to show gradient
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10)); // Centered with gaps
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding around nav bar

        // Load icons using BaseUI's method
        ImageIcon homeIcon = loadScaledIcon("/Icons/homefull.png", 30, 30);
        ImageIcon calendarIcon = loadScaledIcon("/Icons/logbook.png", 30, 30);
        ImageIcon profileIcon = loadScaledIcon("/Icons/profile.png", 30, 30);

        // Create navigation buttons using BaseUI's method
        JButton homeButton = createIconButton(homeIcon, "Home");
        JButton calendarButton = createIconButton(calendarIcon, "Calendar");
        JButton profileButton = createIconButton(profileIcon, "Profile");

        // Add action listeners to navigation buttons
        homeButton.addActionListener(e -> {
            // Already on Home - optional: show a message or refresh
            JOptionPane.showMessageDialog(this, "Already on Home.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        calendarButton.addActionListener(e -> {
            dispose(); // Close current window
            new Calendar(currentUser); // Open Calendar screen with User object
        });

        profileButton.addActionListener(e -> {
            dispose(); // Close current window
            new Profile(currentUser); // Open Profile screen with User object
        });

        // Add buttons to navigation bar
        navBar.add(homeButton);
        navBar.add(calendarButton);
        navBar.add(profileButton);

        return navBar;
    }


    class RoundedButton extends JButton {
        private Color bgColor;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }
    }

    /**
     * Main method to run the Home screen independently.
     * Useful for testing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, create a dummy User
            User dummyUser = new User(); // Ensure User has setters
            dummyUser.setName("Mark");
            new Home(dummyUser);
        });
    }
}

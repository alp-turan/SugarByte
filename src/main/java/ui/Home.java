package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Home extends BaseUI {

    private JLabel greetingLabel;

    // Fields for Quick Log
    private JTextField preBloodSugarField;
    private JTextField preCarbsField;
    private JTextField postBloodSugarField;
    private JTextField postCarbsField;

    public Home(User user) {
        super("Home");
        this.currentUser = user;  // from BaseUI
        buildUI();

        // Immediately update the label now that the UI is built
        if (greetingLabel != null && currentUser != null && currentUser.getName() != null) {
            greetingLabel.setText("Hi, " + currentUser.getName());
        }

        setVisible(true);
    }

    private void buildUI() {
        // Current date for display
        LocalDate today = LocalDate.now();

        // ============ MAIN PANEL ============
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        // ============ TOP PANEL (Title + Date) ============
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Title: SugarByte
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        topPanel.add(titleLabel, gbc);

        // Date Label, e.g.: "Monday, 1 Jan"
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM"));
        JLabel dateLabel = new JLabel(formattedDate, SwingConstants.CENTER);
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        dateLabel.setForeground(new Color(200, 40, 40));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        topPanel.add(dateLabel, gbc);

        mainPanel.add(topPanel);

        // ============ CENTER PANEL ============
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Greeting + Green Circle row
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);

        greetingLabel = new JLabel("Hi, Name");
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        greetingLabel.setForeground(Color.BLACK);

        // Decorative green circle
        JLabel greenCircle = new JLabel();
        greenCircle.setOpaque(true);
        greenCircle.setBackground(new Color(0, 128, 0));
        greenCircle.setPreferredSize(new Dimension(10, 10));
        greenCircle.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        greetingPanel.add(greetingLabel, BorderLayout.WEST);
        greetingPanel.add(greenCircle, BorderLayout.EAST);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 20, 0, 20);
        centerPanel.add(greetingPanel, gbc);

        // Reminder message
        JLabel reminderLabel = new JLabel("Don’t forget to log your values");
        reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reminderLabel.setForeground(new Color(0x88, 0x88, 0x88));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 50, 20);
        centerPanel.add(reminderLabel, gbc);

        // Quick Log panel
        JPanel quickLogPanel = createActualQuickLogPanel();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 0, 20);
        centerPanel.add(quickLogPanel, gbc);

        // "Logbook for (date)" button
        String logbookLabel = "Logbook for " + today.format(DateTimeFormatter.ofPattern("d MMM"));
        RoundedButton logbookButton = new RoundedButton(logbookLabel, new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(200, 40));

        // NEW: Open the Logbook page for "today"
        logbookButton.addActionListener(e -> {
            dispose();
            new Logbook(currentUser, today.toString());
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(50, 20, 20, 20);
        centerPanel.add(logbookButton, gbc);

        mainPanel.add(centerPanel);

        // ============ BOTTOM NAV BAR ============
        JPanel navBar = createBottomNavBar("Home", currentUser,
                "/Icons/homefull.png", "/Icons/logbook.png", "/Icons/profile.png");
        mainPanel.add(navBar);
    }

    /**
     * Creates a Quick Log panel with Pre/Post BG + Carbs.
     * (unchanged from your code)
     */
    private JPanel createActualQuickLogPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Quick Log"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Meal label
        JLabel mealLabel = new JLabel("Meal: " + getCurrentMeal());
        mealLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mealLabel.setForeground(new Color(200, 40, 40));

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(mealLabel, gbc);

        gbc.gridwidth = 1;

        // Table Headers
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel(""), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Blood Glucose"), gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Carbs"), gbc);

        // Pre row
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Pre:"), gbc);

        gbc.gridx = 1;
        preBloodSugarField = new JTextField(5);
        panel.add(preBloodSugarField, gbc);

        gbc.gridx = 2;
        preCarbsField = new JTextField(5);
        panel.add(preCarbsField, gbc);

        // Post row
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Post:"), gbc);

        gbc.gridx = 1;
        postBloodSugarField = new JTextField(5);
        panel.add(postBloodSugarField, gbc);

        gbc.gridx = 2;
        postCarbsField = new JTextField(5);
        panel.add(postCarbsField, gbc);

        // Save button
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JButton saveBtn = new JButton("Save Log");
        saveBtn.setBackground(new Color(237, 165, 170));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(e -> saveQuickLog());
        panel.add(saveBtn, gbc);

        return panel;
    }

    /**
     * Determine which meal it is, based on local time.
     */
    private String getCurrentMeal() {
        int hour = LocalTime.now().getHour();
        if (hour >= 6 && hour < 12) {
            return "Breakfast";
        } else if (hour >= 12 && hour < 17) {
            return "Lunch";
        } else if (hour >= 17 && hour < 21) {
            return "Dinner";
        } else {
            return "Bedtime";
        }
    }

    /**
     * Saves the quick log(s) to DB.
     * By default, it saves "Pre" as one LogEntry,
     * and if the "Post" fields have data, it saves a second LogEntry.
     */
    private void saveQuickLog() {
        double preBG = parseDoubleSafe(preBloodSugarField.getText());
        double preCarbs = parseDoubleSafe(preCarbsField.getText());

        String meal = getCurrentMeal();
        // Create a "Pre" log entry
        if (preBG > 0 || preCarbs > 0) {
            LogEntry entryPre = new LogEntry();
            entryPre.setUserId(currentUser.getId());
            entryPre.setDate(LocalDate.now().toString());
            entryPre.setTimeOfDay(meal + " Pre");  // Match "ROW_LABELS"
            entryPre.setBloodSugar(preBG);
            entryPre.setCarbsEaten(preCarbs);
            entryPre.setFoodDetails("Quick log (Pre)");

            // Save the "Pre" entry
            LogService.createEntry(entryPre, currentUser);
        }

        // Check if "Post" fields have data
        double postBG = parseDoubleSafe(postBloodSugarField.getText());
        double postCarbs = parseDoubleSafe(postCarbsField.getText());
        if (postBG > 0 || postCarbs > 0) {
            LogEntry entryPost = new LogEntry();
            entryPost.setUserId(currentUser.getId());
            entryPost.setDate(LocalDate.now().toString());
            entryPost.setTimeOfDay(meal + " Post");  // Match "ROW_LABELS"
            entryPost.setBloodSugar(postBG);
            entryPost.setCarbsEaten(postCarbs);
            entryPost.setFoodDetails("Quick log (Post)");

            // Save the "Post" entry
            LogService.createEntry(entryPost, currentUser);
        }

        JOptionPane.showMessageDialog(this, "Quick log saved!");
    }


    /**
     * Safe parse for double fields.
     */
    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // For quick testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyUser = new User();
            dummyUser.setName("Mark");
            new Home(dummyUser);
        });
    }
}
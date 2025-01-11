package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class Home extends BaseUI {

    private JLabel greetingLabel;

    // Fields for Quick Log
    private JTextField preBloodSugarField;
    private JTextField preCarbsField;
    private JTextField postBloodSugarField;
    private JTextField postCarbsField;
    private GlucoseIndicator glucoseIndicator;

    public Home(User user) {
        super("Home");
        this.currentUser = user;  // from BaseUI
        buildUI();

        // Immediately update the label now that the UI is built
        if (greetingLabel != null && currentUser != null && currentUser.getName() != null) {
            greetingLabel.setText("Hi, " + currentUser.getName());
        }

        setVisible(true);
        addLogoutButton();
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

        // Greeting + Glucose Indicator row
        JPanel greetingPanel = new JPanel(new BorderLayout(20, 0));
        greetingPanel.setOpaque(false);

        // Greeting on the left
        greetingLabel = new JLabel("Hi, " + currentUser.getName());
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        greetingLabel.setForeground(Color.BLACK);

        // Glucose indicator on the right
        glucoseIndicator = new GlucoseIndicator();
        double latestGlucose = getLatestGlucoseReading();
        glucoseIndicator.updateGlucoseLevel(latestGlucose);

        greetingPanel.add(greetingLabel, BorderLayout.WEST);
        greetingPanel.add(glucoseIndicator, BorderLayout.EAST);

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

        // ============ LAST WEEK'S TREND BUTTON ============
        JButton trendButton = new RoundedButton("Last Week's Glucose Trend", new Color(240, 240, 240));
        trendButton.setForeground(Color.BLACK);
        trendButton.setFont(new Font("Poppins", Font.BOLD, 14));
        trendButton.setPreferredSize(new Dimension(250, 40));

        trendButton.addActionListener(e -> {
            dispose(); // Close Home
            new GlucoseGraph(currentUser); // Open the new graph page
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 10, 20);
        centerPanel.add(trendButton, gbc);

        // "View Today's Logbook" button
        RoundedButton logbookButton = new RoundedButton("View Today's Logbook", new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(200, 40));

        // Now open the CORRECT logbook based on user.getLogbookType()
        logbookButton.addActionListener(e -> {
            dispose();
            String logbookType = currentUser.getLogbookType(); // "Simple", "Comprehensive", or "Intensive"

            switch (logbookType) {
                case "Simple":
                    new Logbook(currentUser, today.toString());
                    break;
                case "Comprehensive":
                    new ComprehensiveLogbook(currentUser, today.toString());
                    break;
                case "Intensive":
                    new IntensiveLogbook(currentUser, today.toString());
                    break;
                default:
                    JOptionPane.showMessageDialog(
                            this,
                            "Unknown logbook type: " + logbookType,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
            }
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(15, 20, 20, 20);
        centerPanel.add(logbookButton, gbc);

        mainPanel.add(centerPanel);

        // ============ BOTTOM NAV BAR ============
        JPanel navBar = createBottomNavBar("Home", currentUser,
                "/Icons/homefull.png", "/Icons/logbook.png", "/Icons/graph.png", "/Icons/profile.png");
        mainPanel.add(navBar);
    }
    private double getLatestGlucoseReading() {
        List<LogEntry> todaysLogs = LogService.getEntriesForDate(
                currentUser.getId(),
                LocalDate.now().toString()
        );

        if (!todaysLogs.isEmpty()) {
            return todaysLogs.get(0).getBloodSugar();
        }
        return 6.0; // Default "normal" value if no readings today
    }

    /**
     * Creates a Quick Log panel with Pre/Post BG + Carbs.
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
        panel.add(new JLabel("Blood Glucose (mmol/L)"), gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Carbs Eaten (g)"), gbc);

        // Pre row
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Pre:"), gbc);

        gbc.gridx = 1;
        preBloodSugarField = new JTextField(5);
        applyNumericFilter(preBloodSugarField);
        panel.add(preBloodSugarField, gbc);

        gbc.gridx = 2;
        preCarbsField = new JTextField(5);
        applyNumericFilter(preCarbsField);
        panel.add(preCarbsField, gbc);

        // Post row
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Post:"), gbc);

        gbc.gridx = 1;
        postBloodSugarField = new JTextField(5);
        applyNumericFilter(postBloodSugarField);
        panel.add(postBloodSugarField, gbc);

        gbc.gridx = 2;
        postCarbsField = new JTextField(5);
        applyNumericFilter(postCarbsField);
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

        LocalDate today = LocalDate.now();
        String meal = getCurrentMeal();

        // Create a "Pre" log entry
        if (preBG > 0 || preCarbs > 0) {
            LogEntry entryPre = new LogEntry();
            entryPre.setUserId(currentUser.getId());
            entryPre.setDate(today.toString());
            entryPre.setTimeOfDay(meal + " Pre");
            entryPre.setBloodSugar(preBG);
            entryPre.setCarbsEaten(preCarbs);
            entryPre.setFoodDetails("Quick log (Pre)");
            LogService.createEntry(entryPre, currentUser);
        }

        // Check if "Post" fields have data
        double postBG = parseDoubleSafe(postBloodSugarField.getText());
        double postCarbs = parseDoubleSafe(postCarbsField.getText());
        if (postBG > 0 || postCarbs > 0) {
            LogEntry entryPost = new LogEntry();
            entryPost.setUserId(currentUser.getId());
            entryPost.setDate(today.toString());
            entryPost.setTimeOfDay(meal + " Post");
            entryPost.setBloodSugar(postBG);
            entryPost.setCarbsEaten(postCarbs);
            entryPost.setFoodDetails("Quick log (Post)");
            LogService.createEntry(entryPost, currentUser);
        }

        JOptionPane.showMessageDialog(this, "Quick log saved!");
    }

    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Apply a numeric filter to a JTextField.
     */
    private void applyNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericFilter());
    }

    // Numeric filter that only allows numeric input
    public static class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || !string.matches("\\d*\\.?\\d*")) {
                return; // Ignore non-numeric input
            }
            super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
            if (string == null || !string.matches("\\d*\\.?\\d*")) {
                return; // Ignore non-numeric input
            }
            super.replace(fb, offset, length, string, attrs);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }
    }
}

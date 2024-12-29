package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Home extends BaseUI {

    private JLabel greetingLabel;

    // Fields for Quick Log
    private JTextField preBloodSugarField;
    private JTextField preCarbsField;
    private JTextField postBloodSugarField;
    private JTextField postCarbsField;
    private JTextField exerciseTypeField;
    private JTextField exerciseDurationField;
    private JTextField insulinDoseField;
    private JTextField otherMedicationsField;
    private JTextField foodDetailsField;

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
        JPanel quickLogPanel = createActualQuickLogPanel(today);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 0, 20);
        centerPanel.add(quickLogPanel, gbc);

        // "Logbook for (date)" button
        String logbookLabel = "Logbook for " + today.format(DateTimeFormatter.ofPattern("d MMM"));
        RoundedButton logbookButton = new RoundedButton(logbookLabel, new Color(240, 240, 240));
        logbookButton.setForeground(Color.BLACK);
        logbookButton.setFont(new Font("Poppins", Font.BOLD, 14));
        logbookButton.setPreferredSize(new Dimension(200, 40));

        // NEW: Open the Correct Logbook for "today"
        logbookButton.addActionListener(e -> openCorrectLogbook(today.toString()));

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
     * Opens the correct logbook type based on user preferences.
     */
    private void openCorrectLogbook(String date) {
        String logbookType = currentUser.getLogbookType();
        dispose();
        switch (logbookType) {
            case "Comprehensive":
                new ComprehensiveLogbook(currentUser, date);
                break;
            case "Intensive":
                new IntensiveLogbook(currentUser, date);
                break;
            default:  // Default is Simple
                new Logbook(currentUser, date);
        }
    }

    /**
     * Creates a Quick Log panel with categories for the current meal (Breakfast/Lunch/Dinner/Bedtime).
     */
    private JPanel createActualQuickLogPanel(LocalDate today) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Quick Log"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Meal label (depending on current time)
        JLabel mealLabel = new JLabel("Meal: " + getCurrentMeal());
        mealLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mealLabel.setForeground(new Color(200, 40, 40));

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(mealLabel, gbc);

        gbc.gridwidth = 1;

        // Categories based on the logbook type
        String logbookType = currentUser.getLogbookType();
        List<String> categories = getLogCategoriesForMeal(logbookType);

        // Add category input fields dynamically
        int row = 1;
        for (String category : categories) {
            addCategoryInput(panel, gbc, category, row++);
        }

        // Table Headers for Glucose and Carbs
        gbc.gridy = row++;
        gbc.gridx = 0;
        panel.add(new JLabel("Blood Glucose"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("Carbs"), gbc);

        // Pre row
        gbc.gridy = row++;
        gbc.gridx = 0;
        panel.add(new JLabel("Pre:"), gbc);

        gbc.gridx = 1;
        preBloodSugarField = new JTextField(5);
        panel.add(preBloodSugarField, gbc);

        gbc.gridx = 2;
        preCarbsField = new JTextField(5);
        panel.add(preCarbsField, gbc);

        // Post row
        gbc.gridy = row++;
        gbc.gridx = 0;
        panel.add(new JLabel("Post:"), gbc);

        gbc.gridx = 1;
        postBloodSugarField = new JTextField(5);
        panel.add(postBloodSugarField, gbc);

        gbc.gridx = 2;
        postCarbsField = new JTextField(5);
        panel.add(postCarbsField, gbc);

        // Save button
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JButton saveBtn = new JButton("Save Log");
        saveBtn.setBackground(new Color(237, 165, 170));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.addActionListener(e -> {
            saveQuickLog();
            openCorrectLogbook(today.toString()); // Open the logbook after saving
        });
        panel.add(saveBtn, gbc);

        return panel;
    }

    /**
     * Returns the categories for the current meal based on the user's logbook type.
     */
    private List<String> getLogCategoriesForMeal(String logbookType) {
        List<String> categories = new ArrayList<>();

        // Add categories based on logbook type
        if (logbookType.equals("Comprehensive")) {
            categories.add("Exercise Type");
            categories.add("Exercise Duration");
            categories.add("Food Details");
        } else if (logbookType.equals("Intensive")) {
            categories.add("Exercise Type");
            categories.add("Exercise Duration");
            categories.add("Food Details");
            categories.add("Insulin Dose");
            categories.add("Other Medications");
        }

        // Add common categories for all logbook types
        categories.add("Blood Glucose");
        categories.add("Carbs");

        return categories;
    }

    /**
     * Adds input fields for a specified category (Exercise, Food, Insulin Dose, etc.).
     */
    private void addCategoryInput(JPanel panel, GridBagConstraints gbc, String categoryName, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        panel.add(new JLabel(categoryName + ":"), gbc);

        gbc.gridx = 1;
        JTextField categoryField = new JTextField(5);
        panel.add(categoryField, gbc);

        // Assign specific category fields to instance variables
        if (categoryName.equals("Exercise Type")) {
            exerciseTypeField = categoryField;
        } else if (categoryName.equals("Exercise Duration")) {
            exerciseDurationField = categoryField;
        } else if (categoryName.equals("Food Details")) {
            foodDetailsField = categoryField;
        } else if (categoryName.equals("Insulin Dose")) {
            insulinDoseField = categoryField;
        } else if (categoryName.equals("Other Medications")) {
            otherMedicationsField = categoryField;
        }
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
            entryPre.setTimeOfDay(meal + " Pre");
            entryPre.setBloodSugar(preBG);
            entryPre.setCarbsEaten(preCarbs);
            entryPre.setFoodDetails(foodDetailsField.getText());
            LogService.createEntry(entryPre, currentUser);
        }

        double postBG = parseDoubleSafe(postBloodSugarField.getText());
        double postCarbs = parseDoubleSafe(postCarbsField.getText());
        if (postBG > 0 || postCarbs > 0) {
            LogEntry entryPost = new LogEntry();
            entryPost.setUserId(currentUser.getId());
            entryPost.setDate(LocalDate.now().toString());
            entryPost.setTimeOfDay(meal + " Post");
            entryPost.setBloodSugar(postBG);
            entryPost.setCarbsEaten(postCarbs);
            entryPost.setFoodDetails(foodDetailsField.getText());
            LogService.createEntry(entryPost, currentUser);
        }

        // Log additional details for comprehensive or intensive
        if (exerciseTypeField != null && !exerciseTypeField.getText().isEmpty()) {
            LogEntry exerciseEntry = new LogEntry();
            exerciseEntry.setUserId(currentUser.getId());
            exerciseEntry.setDate(LocalDate.now().toString());
            exerciseEntry.setTimeOfDay(meal + " Exercise");
            exerciseEntry.setFoodDetails(exerciseTypeField.getText());
            LogService.createEntry(exerciseEntry, currentUser);
        }

        if (exerciseDurationField != null && !exerciseDurationField.getText().isEmpty()) {
            LogEntry exerciseDurationEntry = new LogEntry();
            exerciseDurationEntry.setUserId(currentUser.getId());
            exerciseDurationEntry.setDate(LocalDate.now().toString());
            exerciseDurationEntry.setTimeOfDay(meal + " Exercise Duration");
            exerciseDurationEntry.setFoodDetails(exerciseDurationField.getText());
            LogService.createEntry(exerciseDurationEntry, currentUser);
        }

        if (insulinDoseField != null && !insulinDoseField.getText().isEmpty()) {
            LogEntry insulinDoseEntry = new LogEntry();
            insulinDoseEntry.setUserId(currentUser.getId());
            insulinDoseEntry.setDate(LocalDate.now().toString());
            insulinDoseEntry.setTimeOfDay(meal + " Insulin Dose");
            insulinDoseEntry.setFoodDetails(insulinDoseField.getText());
            LogService.createEntry(insulinDoseEntry, currentUser);
        }

        if (otherMedicationsField != null && !otherMedicationsField.getText().isEmpty()) {
            LogEntry otherMedicationsEntry = new LogEntry();
            otherMedicationsEntry.setUserId(currentUser.getId());
            otherMedicationsEntry.setDate(LocalDate.now().toString());
            otherMedicationsEntry.setTimeOfDay(meal + " Other Medications");
            otherMedicationsEntry.setFoodDetails(otherMedicationsField.getText());
            LogService.createEntry(otherMedicationsEntry, currentUser);
        }
    }

    // Safe parse double
    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

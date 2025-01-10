package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ComprehensiveLogbook - extends Logbook
 * Adds functionality for exercise type and insulin dose tracking.
 */
public class ComprehensiveLogbook extends Logbook {

    protected JTextField[] exerciseFields = new JTextField[7];  // Initialize the exerciseFields array
    protected JTextField[] insulinDoseFields = new JTextField[7];  // Initialize the insulinDoseFields array

    // Constructor for ComprehensiveLogbook
    public ComprehensiveLogbook(User user, String date) {
        super(user, date);

        // Initialize the fields array if not already initialized (safety check)
        if (exerciseFields == null) {
            exerciseFields = new JTextField[7];
        }
        if (insulinDoseFields == null) {
            insulinDoseFields = new JTextField[7];
        }

        // Debugging statement
        System.out.println("ComprehensiveLogbook Constructor: exerciseFields and insulinDoseFields initialized.");
    }

    @Override
    protected void buildUI() {
        super.buildUI();  // Call the base class's buildUI method
        System.out.println("Base buildUI() method executed");  // Debugging print

        // Check if arrays are initialized properly
        if (exerciseFields == null) {
            System.out.println("exerciseFields array is null!");
        } else {
            System.out.println("exerciseFields array is initialized.");
        }

        if (insulinDoseFields == null) {
            System.out.println("insulinDoseFields array is null!");
        } else {
            System.out.println("insulinDoseFields array is initialized.");
        }

        JPanel centerPanel = (JPanel) getContentPane().getComponent(1); // Retrieve the center panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Exercise Type and Insulin Dose columns
        gbc.gridy = 0;
        gbc.gridx = 4;
        JLabel exerciseHeader = new JLabel("Exercise Type");
        exerciseHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(exerciseHeader, gbc);

        gbc.gridx = 5;
        JLabel insulinHeader = new JLabel("Insulin Dose");
        insulinHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(insulinHeader, gbc);

        // Add fields for each row
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2;
            gbc.gridx = 4;
            exerciseFields[i] = new JTextField(5);  // Create a new JTextField for exercise
            centerPanel.add(exerciseFields[i], gbc);

            gbc.gridx = 5;
            insulinDoseFields[i] = new JTextField(5);  // Create a new JTextField for insulin dose
            centerPanel.add(insulinDoseFields[i], gbc);
        }
    }

    @Override
    protected void handleSaveAll() {
        super.handleSaveAll();  // Handle saving the data in the base class
        for (int i = 0; i < ROW_LABELS.length; i++) {
            // Debug: Check if fields are being accessed correctly
            System.out.println("handleSaveAll: Accessing field " + i + " for exercise and insulin dose.");
            String exerciseType = exerciseFields[i].getText();
            double insulinDose = parseDoubleSafe(insulinDoseFields[i].getText());

            // Check if exerciseType is not empty or insulinDose is greater than 0
            if (!exerciseType.isEmpty() || insulinDose > 0) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setExerciseType(exerciseType);
                entry.setInsulinDose(insulinDose);
                LogService.createEntry(entry, currentUser);
            }
        }
    }

    // Helper method to safely parse the double value for insulin dose
    protected double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;  // Return 0 if the input is not a valid number
        }
    }
}

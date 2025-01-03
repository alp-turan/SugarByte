package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;

/**
 * ComprehensiveLogbook - extends Logbook
 * Adds functionality for exercise type and insulin dose tracking.
 */
public class ComprehensiveLogbook extends Logbook {
    private JTextField[] exerciseFields = new JTextField[7];  // Exercise Type
    private JTextField[] insulinDoseFields = new JTextField[7]; // Insulin Dose Fields

    public ComprehensiveLogbook(User user, String date) {
        super(user, date);
    }

    @Override
    protected void buildUI() {
        super.buildUI();

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
            exerciseFields[i] = new JTextField(5);
            centerPanel.add(exerciseFields[i], gbc);

            gbc.gridx = 5;
            insulinDoseFields[i] = new JTextField(5);
            centerPanel.add(insulinDoseFields[i], gbc);
        }
    }

    @Override
    protected void handleSaveAll() {
        super.handleSaveAll();
        for (int i = 0; i < ROW_LABELS.length; i++) {
            String exerciseType = exerciseFields[i].getText();
            double insulinDose = parseDoubleSafe(insulinDoseFields[i].getText());

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

}

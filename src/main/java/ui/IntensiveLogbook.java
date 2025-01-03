package ui;

import model.LogEntry;
import model.User;
import service.LogService;

import javax.swing.*;
import java.awt.*;

/**
 * IntensiveLogbook - extends ComprehensiveLogbook
 * Adds functionality for food diary and other event tracking.
 */
public class IntensiveLogbook extends ComprehensiveLogbook {
    private JTextField[] foodDiaryFields = new JTextField[7];  // Food Diary
    private JTextField[] otherEventsFields = new JTextField[7]; // Other Events

    public IntensiveLogbook(User user, String date) {
        super(user, date);
    }

    @Override
    protected void buildUI() {
        super.buildUI();

        JPanel centerPanel = (JPanel) getContentPane().getComponent(1); // Retrieve the center panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Food Diary and Other Events columns
        gbc.gridy = 0;
        gbc.gridx = 6;
        JLabel foodDiaryHeader = new JLabel("Food Diary");
        foodDiaryHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(foodDiaryHeader, gbc);

        gbc.gridx = 7;
        JLabel otherEventsHeader = new JLabel("Other Events");
        otherEventsHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        centerPanel.add(otherEventsHeader, gbc);

        // Add fields for each row
        for (int i = 0; i < ROW_LABELS.length; i++) {
            gbc.gridy = i + 2;
            gbc.gridx = 6;
            foodDiaryFields[i] = new JTextField(5);
            centerPanel.add(foodDiaryFields[i], gbc);

            gbc.gridx = 7;
            otherEventsFields[i] = new JTextField(5);
            centerPanel.add(otherEventsFields[i], gbc);
        }
    }

    @Override
    protected void handleSaveAll() {
        super.handleSaveAll();
        for (int i = 0; i < ROW_LABELS.length; i++) {
            String foodDiary = foodDiaryFields[i].getText();
            String otherEvents = otherEventsFields[i].getText();

            if (!foodDiary.isEmpty() || !otherEvents.isEmpty()) {
                LogEntry entry = new LogEntry();
                entry.setUserId(currentUser.getId());
                entry.setDate(targetDate);
                entry.setTimeOfDay(ROW_LABELS[i]);
                entry.setFoodDetails(foodDiary);
                entry.setOtherMedications(otherEvents);
                LogService.createEntry(entry, currentUser);
            }
        }
    }
}

package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Calendar extends BaseUI {

    public Calendar(User user) {
        super("Calendar");
        this.currentUser = user;
        refreshCalendar(); // Rebuild the calendar UI
        setVisible(true);
    }

    @Override
    public JPanel createDaysPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Day initials row
        JPanel dayInitialsPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        dayInitialsPanel.setOpaque(false);
        String[] dayInitials = {"M", "T", "W", "T", "F", "S", "S"};
        for (String d : dayInitials) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(new Color(0xD8, 0x38, 0x42));
            dayInitialsPanel.add(label);
        }

        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 10, 10));
        daysGrid.setOpaque(false);

        // We’ll build out the days for the current month
        java.time.YearMonth yearMonth = currentYearMonth;
        int lengthOfMonth = yearMonth.lengthOfMonth();

        // Get the first day of the month and its day of the week
        java.time.LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();

        // Fill in blank days
        int blankDaysBefore = (dayOfWeekValue == 7) ? 6 : dayOfWeekValue - 1;
        for (int i = 0; i < blankDaysBefore; i++) {
            daysGrid.add(new JLabel(""));
        }

        // Fill in actual days
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int day = 1; day <= lengthOfMonth; day++) {
            java.time.LocalDate date = yearMonth.atDay(day);

            // Custom DayCircle
            DayCircle dayCircle = new DayCircle(day);
            dayCircle.setHorizontalAlignment(SwingConstants.CENTER);
            dayCircle.setFont(new Font("SansSerif", Font.BOLD, 14));

            if (date.equals(today)) {
                dayCircle.setCircleColor(new Color(0xD8, 0x38, 0x42));
                dayCircle.setForeground(Color.WHITE);
            } else {
                dayCircle.setCircleColor(new Color(0xE1, 0xE1, 0xE1));
                dayCircle.setForeground(Color.BLACK);
            }

            // Make it clickable
            dayCircle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Open Logbook for that day
                    dispose();
                    new Logbook(currentUser, date.toString());
                }
            });

            // Put DayCircle in a small panel
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setOpaque(false);
            dayPanel.add(dayCircle, BorderLayout.CENTER);

            daysGrid.add(dayPanel);
        }

        // Fill the remaining cells
        int totalCells = 6 * 7;
        int filledCells = blankDaysBefore + lengthOfMonth;
        for (int i = filledCells; i < totalCells; i++) {
            daysGrid.add(new JLabel(""));
        }

        panel.add(dayInitialsPanel, BorderLayout.NORTH);
        panel.add(daysGrid, BorderLayout.CENTER);
        return panel;
    }
}
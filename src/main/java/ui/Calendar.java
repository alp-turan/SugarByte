package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;

/**
 * Represents the Calendar screen of the application.
 * This class displays a monthly calendar with clickable days for navigating to corresponding logbooks.
 */
public class Calendar extends BaseUI {

    /**
     * Constructs the Calendar screen for the specified user.
     *
     * @param user The currently logged-in user, providing access to user-specific details and logbook type.
     */
    public Calendar(User user) {
        super("Calendar"); // Sets the window title using the BaseUI constructor
        this.currentUser = user; // Stores the current user
        refreshCalendar(); // Builds and displays the calendar UI
        setVisible(true); // Makes the calendar window visible
    }

    /**
     * Creates a panel displaying the days of the current month.
     * Includes day initials (e.g., "M", "T", "W") and a grid of days.
     *
     * @return A JPanel containing the days panel.
     */
    @Override
    public JPanel createDaysPanel() {
        // Main panel for the days section
        JPanel panel = new JPanel(new BorderLayout()); // BorderLayout to structure day initials and day grid
        panel.setOpaque(false); // Transparent background to blend with the overall UI theme

        // Create the day initials row (e.g., "M", "T", "W", etc.)
        JPanel dayInitialsPanel = new JPanel(new GridLayout(1, 7, 10, 0)); // GridLayout for 7 columns (one for each day)
        dayInitialsPanel.setOpaque(false); // Transparent background
        String[] dayInitials = {"M", "T", "W", "T", "F", "S", "S"}; // Array of day initials
        for (String d : dayInitials) {
            JLabel label = new JLabel(d, SwingConstants.CENTER); // Center-aligned day initial
            label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bold font style
            label.setForeground(new Color(0xD8, 0x38, 0x42)); // Red color for emphasis
            dayInitialsPanel.add(label); // Add the label to the initials panel
        }

        // Create the grid for displaying days
        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 10, 10)); // 6 rows, 7 columns for a full month view
        daysGrid.setOpaque(false); // Transparent background

        // Retrieve details for the current month
        YearMonth yearMonth = currentYearMonth; // Current YearMonth object
        int lengthOfMonth = yearMonth.lengthOfMonth(); // Number of days in the current month

        // Get the first day of the month and determine its day of the week
        java.time.LocalDate firstOfMonth = yearMonth.atDay(1); // Get the first date of the month
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // Get the day of the week (Monday = 1)

        // Calculate how many blank cells are needed before the first day
        int blankDaysBefore = (dayOfWeekValue == 7) ? 6 : dayOfWeekValue - 1; // Adjust Sunday as the last day
        for (int i = 0; i < blankDaysBefore; i++) {
            daysGrid.add(new JLabel("")); // Add empty labels for blank cells
        }

        // Add actual days to the grid
        java.time.LocalDate today = java.time.LocalDate.now(); // Get today's date
        for (int day = 1; day <= lengthOfMonth; day++) {
            java.time.LocalDate date = yearMonth.atDay(day); // Get the date for the current day

            // Create a clickable circle for each day
            DayCircle dayCircle = new DayCircle(day); // Custom JLabel subclass for a circular day display
            dayCircle.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the text
            dayCircle.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bold font style for day numbers

            // Highlight the current day with a specific color
            if (date.equals(today)) {
                dayCircle.setCircleColor(new Color(0xD8, 0x38, 0x42)); // Red background for the current day
                dayCircle.setForeground(Color.WHITE); // White text for visibility
            } else {
                dayCircle.setCircleColor(new Color(0xE1, 0xE1, 0xE1)); // Light gray background for other days
                dayCircle.setForeground(Color.BLACK); // Black text
            }

            // Add a click event to navigate to the corresponding logbook
            dayCircle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose(); // Close the calendar window
                    String logbookType = currentUser.getLogbookType(); // Get the user's logbook type
                    System.out.println("User logbook type: " + logbookType); // Log the user's logbook type

                    // Open the corresponding logbook based on the user's preference
                    switch (logbookType) {
                        case "Simple":
                            new Logbook(currentUser, date.toString()); // Open Simple logbook
                            break;
                        case "Comprehensive":
                            new ComprehensiveLogbook(currentUser, date.toString()); // Open Comprehensive logbook
                            break;
                        case "Intensive":
                            new IntensiveLogbook(currentUser, date.toString()); // Open Intensive logbook
                            break;
                        default:
                            // Show an error message if the logbook type is unknown
                            JOptionPane.showMessageDialog(null,
                                    "Unknown logbook type: " + logbookType,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Add the DayCircle to a panel and then to the grid
            JPanel dayPanel = new JPanel(new BorderLayout()); // Panel to hold the DayCircle
            dayPanel.setOpaque(false); // Transparent background
            dayPanel.add(dayCircle, BorderLayout.CENTER); // Center the DayCircle within the panel
            daysGrid.add(dayPanel); // Add the panel to the days grid
        }

        // Add empty labels for the remaining cells in the grid
        int totalCells = 6 * 7; // Total number of cells (6 rows, 7 columns)
        int filledCells = blankDaysBefore + lengthOfMonth; // Count of filled cells
        for (int i = filledCells; i < totalCells; i++) {
            daysGrid.add(new JLabel("")); // Add empty labels for remaining cells
        }

        // Add the day initials and the days grid to the main panel
        panel.add(dayInitialsPanel, BorderLayout.NORTH); // Day initials at the top
        panel.add(daysGrid, BorderLayout.CENTER); // Days grid in the center
        return panel; // Return the completed days panel
    }
}

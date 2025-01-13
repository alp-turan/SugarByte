package ui;

import database.UserDAO;
import model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * BaseUI serves as the foundational class for creating graphical user interfaces (GUIs) in the application.
 * It extends JFrame to provide a custom window frame and includes utility methods for UI components like
 * custom fonts, gradient panels, and rounded panels.
 */
public class BaseUI extends JFrame {
    public YearMonth currentYearMonth = YearMonth.now(); // Current year and month, defaulting to now
    public User currentUser; // Holds the current user instance
    public JTextField usernameField; // Text field for entering the username
    public JPasswordField passwordField; // Password field for entering the password
    private JCheckBox rememberMeCheckBox; // Checkbox for remembering login credentials
    private String icon1; // Placeholder for an icon path or name

    /**
     * Constructor for BaseUI.
     *
     * @param title The title of the window to be displayed on the JFrame.
     */
    public BaseUI(String title) {
        setTitle(title); // Setting the title of the JFrame

        setSize(400, 800); // Setting the size to mimic a mobile screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ensure the frame is disposed when closed
        setLocationRelativeTo(null); // Center the window on the screen
        setResizable(false); // Prevent resizing of the window
    }

    /**
     * Loads a custom font from resources and scales it to the desired size.
     *
     * @param size The font size to use.
     * @return A custom Font object, or a fallback font if the resource is not found.
     */
    protected Font loadCustomFont(float size) {
        try (InputStream is = getClass().getResourceAsStream("/Fonts/Lobster.ttf")) {
            // Attempting to load the font from the resources directory
            if (is == null) {
                // If the font file is not found, fallback to the default Serif font
                System.err.println("Font not found: /Fonts/Lobster.ttf");
                return new Font("Serif", Font.PLAIN, (int) size);
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            // Loading the font as TrueType and setting the desired size
        } catch (Exception e) {
            // If an error occurs (e.g., file not found or invalid font), fallback to Serif
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    // Load the Lobster font at a default size of 46
    Font lobsterFont = loadCustomFont(46);

    /**
     * Creates a styled JLabel to serve as a title.
     *
     * @param text  The text to display on the label.
     * @param font  The font to use for the label. Defaults to a Serif font if null.
     * @param color The color to set for the text.
     * @return A JLabel configured as a title.
     */
    protected JLabel createTitleLabel(String text, Font font, Color color) {
        JLabel titleLabel = new JLabel(text); // Creating a JLabel with the given text
        titleLabel.setFont(font != null ? font : new Font("Serif", Font.BOLD, 36));
        // Applying the provided font or a default one
        titleLabel.setForeground(color); // Setting the text color
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-aligning the text
        return titleLabel;
    }

    /**
     * Creates a gradient background panel.
     *
     * @param startColor The starting color of the gradient.
     * @param endColor   The ending color of the gradient.
     * @return A JPanel with a gradient background.
     */
    protected JPanel createGradientPanel(Color startColor, Color endColor) {
        return new GradientPanel(startColor, endColor); // Using the GradientPanel class for implementation
    }

    /**
     * A custom JPanel implementation that paints a gradient background.
     */
    static class GradientPanel extends JPanel {
        private final Color startColor; // The starting gradient color
        private final Color endColor;   // The ending gradient color

        /**
         * Constructs a GradientPanel with the specified start and end colors.
         *
         * @param startColor The starting gradient color.
         * @param endColor   The ending gradient color.
         */
        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Calling the parent class's paintComponent method
            Graphics2D g2d = (Graphics2D) g; // Casting Graphics to Graphics2D for advanced rendering
            int width = getWidth(); // Getting the panel's width
            int height = getHeight(); // Getting the panel's height
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
            // Creating a vertical gradient from startColor to endColor
            g2d.setPaint(gradient); // Applying the gradient paint
            g2d.fillRect(0, 0, width, height); // Filling the panel with the gradient
        }
    }

    /**
     * A custom JPanel implementation with rounded corners and shadow effects.
     */
    public static class RoundedPanel extends JPanel {
        private int arc; // The radius for rounded corners
        private int shadowSize; // The size of the shadow
        private Color shadowColor; // The color of the shadow

        /**
         * Constructs a RoundedPanel with specified corner radius and shadow size.
         *
         * @param arc        The radius for rounded corners.
         * @param shadowSize The size of the shadow.
         */
        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            this.shadowColor = new Color(2, 2, 2, 3); // A semi-transparent black shadow
            setOpaque(false); // Ensuring the background remains transparent
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); // Creating a Graphics2D instance

            // Enabling anti-aliasing for smoother rendering of rounded corners
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Drawing the shadow effect
            g2.setColor(shadowColor); // Setting the color to shadowColor
            for (int i = 0; i < shadowSize; i++) {
                // Iteratively drawing slightly smaller rectangles for a fading shadow
                g2.fillRoundRect(i, i + shadowSize / 2, getWidth() - i * 2, getHeight() - i * 2 - shadowSize / 2, arc, arc);
            }

            // Drawing the rounded panel
            g2.setColor(getBackground()); // Setting the color to the panel's background
            g2.fillRoundRect(shadowSize / 2, shadowSize / 2, getWidth() - shadowSize, getHeight() - shadowSize, arc, arc);
            g2.dispose(); // Disposing of the Graphics2D instance to free resources
        }
    }

    /**
     * Loads and scales an image icon to a predefined size for use in UI components.
     *
     * @param path The path to the image file in the resources folder.
     * @return A scaled ImageIcon object or null if the image is not found or an error occurs.
     */
    protected ImageIcon loadScaledIcon(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            // Ensures the resource stream is closed automatically after use
            if (is == null) {
                // Logs an error if the specified image file is not found
                System.err.println("Icon not found: " + path);
                return null; // Return null if the icon is not found
            }

            // Reads the image from the input stream into a BufferedImage
            BufferedImage sourceImage = ImageIO.read(is);

            // Creates a new BufferedImage with the desired size (30x30 pixels)
            BufferedImage resized = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics(); // Gets a Graphics2D instance for rendering

            // Enables high-quality scaling using bicubic interpolation
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // Draws the source image onto the resized BufferedImage
            g2.drawImage(sourceImage, 0, 0, 30, 30, null);
            g2.dispose(); // Releases resources used by the Graphics2D instance

            // Returns the resized image as an ImageIcon
            return new ImageIcon(resized);
        } catch (Exception e) {
            // Logs any exceptions that occur during image loading or scaling
            e.printStackTrace();
            return null; // Returns null if an error occurs
        }
    }

    /**
     * Creates a JButton with an icon and optional tooltip text.
     *
     * @param icon    The ImageIcon to display on the button.
     * @param altText The tooltip text to describe the button's purpose.
     * @return A JButton with the specified icon and tooltip.
     */
    public JButton createIconButton(ImageIcon icon, String altText) {
        JButton button = new JButton(icon); // Creates a new JButton with the provided icon
        button.setToolTipText(altText); // Sets the tooltip text for the button
        button.setFocusPainted(false); // Disables the focus highlight around the button
        button.setBorderPainted(false); // Removes the button's border
        button.setContentAreaFilled(false); // Makes the button's background transparent
        return button; // Returns the customized JButton
    }

    /**
     * Creates a bottom navigation bar with buttons for navigating between screens.
     *
     * @param currentScreen The name of the currently active screen.
     * @param currentUser   The currently logged-in user, used to pass context to other screens.
     * @param icon1         Path to the icon for the "Home" button.
     * @param icon2         Path to the icon for the "Calendar" button.
     * @param icon3         Path to the icon for the "Graph" button.
     * @param icon4         Path to the icon for the "Profile" button.
     * @return A JPanel containing the navigation bar.
     */
    public JPanel createBottomNavBar(String currentScreen, User currentUser, String icon1, String icon2, String icon3, String icon4) {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10)); // Aligns buttons horizontally
        navBar.setOpaque(false); // Makes the navigation bar transparent
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Adds vertical padding

        // Loads icons for the navigation buttons
        ImageIcon homeIcon = loadScaledIcon(icon1); // Loads the "Home" icon
        ImageIcon calendarIcon = loadScaledIcon(icon2); // Loads the "Calendar" icon
        ImageIcon graphIcon = loadScaledIcon(icon3); // Loads the "Graph" icon
        ImageIcon profileIcon = loadScaledIcon(icon4); // Loads the "Profile" icon

        // Creates buttons for navigation
        JButton homeButton = createIconButton(homeIcon, "Home"); // "Home" button
        JButton calendarButton = createIconButton(calendarIcon, "Calendar"); // "Calendar" button
        JButton graphButton = createIconButton(graphIcon, "Graph"); // "Graph" button
        JButton profileButton = createIconButton(profileIcon, "Profile"); // "Profile" button

        // Adds ActionListeners to each button for navigation
        homeButton.addActionListener(e -> navigateTo("Home", currentScreen, currentUser));
        calendarButton.addActionListener(e -> navigateTo("Calendar", currentScreen, currentUser));
        graphButton.addActionListener(e -> navigateTo("GlucoseGraph", currentScreen, currentUser));
        profileButton.addActionListener(e -> navigateTo("Profile", currentScreen, currentUser));

        // Adds the buttons to the navigation bar
        navBar.add(homeButton);
        navBar.add(calendarButton);
        navBar.add(graphButton);
        navBar.add(profileButton);

        return navBar; // Returns the completed navigation bar
    }

    /**
     * Handles navigation between different screens in the application.
     *
     * @param targetScreen  The name of the target screen to navigate to.
     * @param currentScreen The name of the currently active screen.
     * @param currentUser   The currently logged-in user, passed to the new screen for context.
     */
    private void navigateTo(String targetScreen, String currentScreen, User currentUser) {
        // Checks if the target screen is different from the current screen
        if (!targetScreen.equals(currentScreen)) {
            dispose(); // Closes the current screen
            switch (targetScreen) {
                case "Home":
                    new Home(currentUser); // Opens the "Home" screen
                    break;
                case "Calendar":
                    new Calendar(currentUser); // Opens the "Calendar" screen
                    break;
                case "GlucoseGraph":
                    new GlucoseGraph(currentUser); // Opens the "Graph" screen
                    break;
                case "Profile":
                    new Profile(currentUser); // Opens the "Profile" screen
                    break;
                default:
                    // Displays an error message if the target screen is unknown
                    JOptionPane.showMessageDialog(
                            this,
                            "Unknown target screen.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
            }
        } else {
            // Displays a message if the user is already on the target screen
            JOptionPane.showMessageDialog(
                    this,
                    "Already on the " + targetScreen + " page.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Refreshes the calendar view by rebuilding its components.
     * This method removes existing components, recreates the layout, and revalidates the UI.
     */
    protected void refreshCalendar() {
        // Removing all components from the content pane to prepare for rebuilding
        getContentPane().removeAll();

        // Creating the main panel with a gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, Color.WHITE);
        mainPanel.setLayout(new BorderLayout()); // Setting a BorderLayout for better content alignment

        // Creating the central content panel using a GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Setting transparency for the panel
        GridBagConstraints gbc = new GridBagConstraints(); // Constraints for GridBagLayout

        // Adding the title label to the top of the content panel
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);
        gbc.gridy = 0; // Setting the Y-axis position for the title
        gbc.insets = new Insets(0, 0, 30, 0); // Adding bottom padding before the nav bar
        contentPanel.add(titleLabel, gbc); // Adding the title to the content panel

        // Adding the month-year label to display the current month and year
        JLabel monthYearLabel = new JLabel(getMonthYearString(currentYearMonth), SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Applying bold SansSerif font

        gbc.gridy = 1; // Positioning the month-year label below the title
        contentPanel.add(monthYearLabel, gbc); // Adding the label to the content panel

        // Adding the navigation panel for month control
        JPanel topNavPanel = createTopNavPanel(monthYearLabel);
        gbc.gridy = 2; // Positioning the navigation panel below the month-year label
        contentPanel.add(topNavPanel, gbc);

        // Adding the days panel to display the days of the month
        JPanel daysPanel = createDaysPanel();
        gbc.gridy = 3; // Positioning the days panel below the navigation panel
        contentPanel.add(daysPanel, gbc);

        // Adding the content panel to the center of the main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Adding the bottom navigation bar
        mainPanel.add(createBottomNavBar("Calendar", currentUser, "/Icons/home.png", "/Icons/logbookfull.png", "/Icons/graph.png", "/Icons/profile.png"), BorderLayout.SOUTH);

        // Adding the main panel to the frame and refreshing the UI
        add(mainPanel);
        revalidate(); // Revalidates the layout to ensure proper display
        repaint(); // Repaints the frame to reflect updates
    }

    /**
     * Creates a navigation panel for moving between months.
     *
     * @param monthYearLabel The label displaying the current month and year, which updates dynamically.
     * @return A JPanel containing the navigation controls (arrows and label).
     */
    public JPanel createTopNavPanel(JLabel monthYearLabel) {
        JPanel panel = new JPanel(new BorderLayout()); // Using BorderLayout for arrow and label alignment
        panel.setOpaque(false); // Setting transparency for the panel

        // Creating the left arrow button for navigating to the previous month
        JButton leftArrow = createArrowButton("\u25C0"); // Unicode for left arrow
        leftArrow.setFont(new Font("SansSerif", Font.BOLD, 18)); // Applying bold font
        leftArrow.setForeground(Color.BLACK); // Setting text color to black
        leftArrow.setToolTipText("Previous Month"); // Adding a tooltip for the button

        // Creating the right arrow button for navigating to the next month
        JButton rightArrow = createArrowButton("\u25B6"); // Unicode for right arrow
        rightArrow.setFont(new Font("SansSerif", Font.BOLD, 18)); // Applying bold font
        rightArrow.setForeground(Color.BLACK); // Setting text color to black
        rightArrow.setToolTipText("Next Month"); // Adding a tooltip for the button

        // Adding an ActionListener to the left arrow for decrementing the month
        leftArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYearMonth = currentYearMonth.minusMonths(1); // Decrementing the current month
                refreshCalendar(); // Refreshing the calendar view
            }
        });

        // Adding an ActionListener to the right arrow for incrementing the month
        rightArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYearMonth = currentYearMonth.plusMonths(1); // Incrementing the current month
                refreshCalendar(); // Refreshing the calendar view
            }
        });

        // Adding the left arrow, month-year label, and right arrow to the panel
        panel.add(leftArrow, BorderLayout.WEST); // Positioning the left arrow on the west
        panel.add(monthYearLabel, BorderLayout.CENTER); // Centering the month-year label
        panel.add(rightArrow, BorderLayout.EAST); // Positioning the right arrow on the east

        return panel; // Returning the navigation panel
    }

    /**
     * Creates an arrow button with a given symbol.
     *
     * @param symbol The Unicode symbol representing the arrow direction.
     * @return A JButton styled to represent an arrow.
     */
    public JButton createArrowButton(String symbol) {
        JButton button = new JButton(symbol); // Creating a button with the provided symbol
        button.setFont(new Font("SansSerif", Font.BOLD, 18)); // Applying bold SansSerif font
        button.setFocusPainted(false); // Disabling focus highlight for the button
        button.setBorderPainted(false); // Removing the button's border
        button.setContentAreaFilled(false); // Making the button's background transparent
        return button; // Returning the styled button
    }

    /**
     * Creates a panel displaying day initials and a grid for the days of the month.
     *
     * @return A JPanel containing the day initials and days grid.
     */
    public JPanel createDaysPanel() {
        JPanel panel = new JPanel(new BorderLayout()); // Using BorderLayout for separating components
        panel.setOpaque(false); // Setting transparency for the panel

        // Creating a row for the day initials (e.g., "M", "T", "W")
        JPanel dayInitialsPanel = new JPanel(new GridLayout(1, 7, 10, 0)); // Grid layout for day initials
        dayInitialsPanel.setOpaque(false); // Transparent background for the panel
        String[] dayInitials = {"M", "T", "W", "T", "F", "S", "S"}; // Array of day initials

        // Adding each day initial to the panel
        for (String d : dayInitials) {
            JLabel label = new JLabel(d, SwingConstants.CENTER); // Center-aligning text
            label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Applying bold font style
            label.setForeground(new Color(0xD8, 0x38, 0x42)); // Setting text color to a custom red
            dayInitialsPanel.add(label); // Adding the label to the day initials panel
        }

        // Creating a grid for the days of the month
        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 10, 10)); // Grid layout for days (6 rows, 7 columns)
        daysGrid.setOpaque(false); // Transparent background for the grid

        // Getting the current month and year
        YearMonth yearMonth = currentYearMonth;
        int lengthOfMonth = yearMonth.lengthOfMonth(); // Getting the number of days in the month

        // Determining the first day of the month and its day of the week
        java.time.LocalDate firstOfMonth = yearMonth.atDay(1); // First day of the current month
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // Getting the day of the week as an integer

        // Calculating the number of blank cells to add before the first day
        int blankDaysBefore = (dayOfWeekValue == 7) ? 6 : dayOfWeekValue - 1;

        // Adding blank labels to represent empty cells before the first day
        for (int i = 0; i < blankDaysBefore; i++) {
            daysGrid.add(new JLabel("")); // Adding an empty JLabel for each blank day
        }


        // Fill in actual days for the calendar grid
        java.time.LocalDate today = java.time.LocalDate.now(); // Fetching the current system date
        for (int day = 1; day <= lengthOfMonth; day++) {
            java.time.LocalDate date = yearMonth.atDay(day); // Creating a LocalDate object for each day in the month

            // Creating a custom panel for each day's representation
            JPanel dayPanel = new JPanel(new BorderLayout()); // Using BorderLayout to center the day circle
            dayPanel.setOpaque(false); // Ensuring the panel's background is transparent

            // Creating a circular label for the day number
            DayCircle dayCircle = new DayCircle(day); // Custom class for visually styling the day number
            dayCircle.setHorizontalAlignment(SwingConstants.CENTER); // Aligning the day number text to the center
            dayCircle.setFont(new Font("SansSerif", Font.BOLD, 14)); // Applying a bold font for better visibility

            // Highlighting the current date with a distinct style
            if (date.equals(today)) {
                dayCircle.setCircleColor(new Color(0xD8, 0x38, 0x42)); // Setting the circle background to a red color
                dayCircle.setForeground(Color.WHITE); // Setting the text color to white for contrast
            } else {
                // Styling for regular days
                dayCircle.setCircleColor(new Color(0xE1, 0xE1, 0xE1)); // Setting the circle background to light gray
                dayCircle.setForeground(Color.BLACK); // Default black text color for regular days
            }

            dayPanel.add(dayCircle, BorderLayout.CENTER); // Adding the styled day circle to the center of the panel
            daysGrid.add(dayPanel); // Adding the day panel to the grid of days
        }

// Filling the remaining cells in the grid with empty labels to maintain structure
        int totalCells = 6 * 7; // Maximum number of cells in a calendar grid (6 rows × 7 columns)
        int filledCells = blankDaysBefore + lengthOfMonth; // Counting the cells already filled with days or blanks
        for (int i = filledCells; i < totalCells; i++) {
            daysGrid.add(new JLabel("")); // Adding empty labels for unused cells
        }

// Adding the day initials row and the grid of days to the main panel
        panel.add(dayInitialsPanel, BorderLayout.NORTH); // Adding the row of day initials to the top
        panel.add(daysGrid, BorderLayout.CENTER); // Adding the grid of days below the initials
        return panel; // Returning the complete panel with day initials and days grid
    }

    /**
     * Changes the currently displayed month and updates the calendar accordingly.
     *
     * @param increment      The number of months to move forward or backward.
     * @param monthYearLabel The JLabel displaying the current month and year.
     */
    private void changeMonth(int increment, JLabel monthYearLabel) {
        currentYearMonth = currentYearMonth.plusMonths(increment); // Adjusting the current month by the specified increment
        monthYearLabel.setText(getMonthYearString(currentYearMonth)); // Updating the label with the new month and year
        refreshCalendar(); // Refreshing the calendar to reflect the changes
    }

    /**
     * Generates a string representation of the current month and year.
     *
     * @param yearMonth The YearMonth object representing the current month and year.
     * @return A formatted string in the format "Month YYYY" (e.g., "January 2025").
     */
    String getMonthYearString(YearMonth yearMonth) {
        return yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + yearMonth.getYear(); // Formatting the YearMonth
    }

    /**
     * Custom JLabel subclass for rendering day numbers as circles with configurable colors.
     */
    class DayCircle extends JLabel {
        private Color circleColor; // The background color of the circular representation

        /**
         * Constructs a DayCircle object for a specific day.
         *
         * @param dayNumber The day number to display in the circle.
         */
        public DayCircle(int dayNumber) {
            super(String.valueOf(dayNumber)); // Setting the day number as the label's text
            this.circleColor = Color.LIGHT_GRAY; // Default color for the circle
            setPreferredSize(new Dimension(40, 40)); // Setting a fixed size for the circle
            setOpaque(false); // Ensuring the label's background remains transparent
        }

        /**
         * Sets the color of the circle's background.
         *
         * @param circleColor The new color for the circle's background.
         */
        public void setCircleColor(Color circleColor) {
            this.circleColor = circleColor; // Updating the circle's background color
        }

        /**
         * Custom painting method to draw the circular background.
         *
         * @param g The Graphics object used for rendering.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create(); // Creating a Graphics2D instance for enhanced rendering

            // Enabling anti-aliasing for smoother edges
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculating the position and size of the circle
            int diameter = Math.min(getWidth(), getHeight()); // Ensuring the circle fits within the component
            int x = (getWidth() - diameter) / 2; // Centering the circle horizontally
            int y = (getHeight() - diameter) / 2; // Centering the circle vertically

            // Filling the circle with the specified background color
            g2d.setColor(circleColor);
            g2d.fillOval(x, y, diameter, diameter); // Drawing the filled circle

            g2d.dispose(); // Releasing resources used by Graphics2D
            super.paintComponent(g); // Rendering the label's text on top of the circle
        }
    }

    /**
     * Creates a panel containing a "Quick Log" button for quickly logging data.
     *
     * @return A JPanel containing the Quick Log button.
     */
    public JPanel createQuickLogPanel() {
        JPanel panel = new JPanel(); // Creating a new JPanel
        panel.setOpaque(false); // Ensuring the panel's background is transparent
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Using FlowLayout for centered alignment

        // Creating a Quick Log button with custom styling
        JButton quickLogButton = new RoundedButton("Quick Log", new Color(237, 165, 170)); // Rounded button with pink background
        quickLogButton.setPreferredSize(new Dimension(150, 40)); // Setting a fixed size for the button
        quickLogButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // Applying a bold font for the button text

        // Adding a click action to the button
        quickLogButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Quick Log Clicked!", // Message displayed on button click
                "Info", // Dialog title
                JOptionPane.INFORMATION_MESSAGE // Information icon for the dialog
        ));

        panel.add(quickLogButton); // Adding the button to the panel
        return panel; // Returning the completed Quick Log panel
    }

    /**
     * A custom JButton subclass for creating rounded buttons with a specific background color.
     */
    public static class RoundedButton extends JButton {
        private final Color bgColor; // The background color of the button

        /**
         * Constructs a RoundedButton with the specified text and background color.
         *
         * @param text    The text displayed on the button.
         * @param bgColor The background color of the button.
         */
        public RoundedButton(String text, Color bgColor) {
            super(text); // Passing the text to the parent JButton constructor
            this.bgColor = bgColor; // Setting the background color
            setContentAreaFilled(false); // Ensures no default button background is painted
            setFocusPainted(false); // Disables the focus border painting
            setBorderPainted(false); // Disables the border painting
            setOpaque(false); // Makes the button's background transparent
        }

        /**
         * Custom rendering of the button's background to create a rounded rectangle.
         *
         * @param g The Graphics object used for painting.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); // Creating a Graphics2D instance for custom rendering
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enables anti-aliasing for smoother edges
            g2.setColor(bgColor); // Sets the paint color to the background color
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Draws a rounded rectangle
            super.paintComponent(g); // Calls the parent class's painting logic for text and other properties
            g2.dispose(); // Releases resources used by the Graphics2D instance
        }

        /**
         * Overrides the default border painting to do nothing.
         *
         * @param g The Graphics object used for painting.
         */
        @Override
        protected void paintBorder(Graphics g) {
            // No border painting for this button
        }
    }

    /**
     * Creates a panel containing a labeled input field (e.g., Username or Password) with additional styling.
     *
     * @param labelText The label text for the input field.
     * @return A JPanel containing the labeled input field and associated components.
     */
    JPanel createInputPanel(String labelText) {
        JPanel containerPanel = new JPanel(); // The main panel containing the label and input field
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // Vertical alignment for components
        containerPanel.setOpaque(false); // Transparent background for the panel

        // Creating and styling the label for the input field
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.GRAY); // Sets the text color to gray
        label.setFont(new Font("SansSerif", Font.BOLD, 14)); // Sets a bold SansSerif font
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0)); // Adds padding below the label

        // Creating a rounded panel for the input field
        RoundedPanel roundedPanel = new RoundedPanel(70, 10); // Custom rounded panel with specified corner radius and shadow
        roundedPanel.setPreferredSize(new Dimension(300, 75)); // Fixed size for the input panel
        roundedPanel.setBackground(new Color(240, 240, 240)); // Light gray background color
        roundedPanel.setLayout(new BorderLayout()); // Layout for placing the input field in the center

        // Adding an input field based on the label text
        if (labelText.equalsIgnoreCase("Username")) {
            usernameField = new JTextField(); // Creating a text field for the username
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10)); // Adds padding inside the field
            usernameField.setBackground(new Color(240, 240, 240)); // Matches the rounded panel's background color
            usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Sets a plain font for text input
            usernameField.setOpaque(false); // Transparent background for the text field
            roundedPanel.add(usernameField, BorderLayout.CENTER); // Adding the text field to the rounded panel
        } else if (labelText.equalsIgnoreCase("Password")) {
            passwordField = new JPasswordField(); // Creating a password field
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10)); // Adds padding inside the field
            passwordField.setBackground(new Color(240, 240, 240)); // Matches the rounded panel's background color
            passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Sets a plain font for text input
            passwordField.setOpaque(false); // Transparent background for the password field
            roundedPanel.add(passwordField, BorderLayout.CENTER); // Adding the password field to the rounded panel
        }

        containerPanel.add(label); // Adding the label to the container panel
        containerPanel.add(roundedPanel); // Adding the rounded panel to the container panel

        // Adding a "Remember Me" checkbox if the label is for the password field
        if (labelText.equalsIgnoreCase("Password")) {
            rememberMeCheckBox = new JCheckBox("Remember Me"); // Checkbox for saving user credentials
            rememberMeCheckBox.setOpaque(false); // Transparent background for the checkbox
            rememberMeCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Plain font for the checkbox text
            rememberMeCheckBox.setForeground(Color.GRAY); // Gray text color for the checkbox
            containerPanel.add(rememberMeCheckBox); // Adding the checkbox to the container panel
            loadCredentials(); // Loads saved credentials if available
        }

        return containerPanel; // Returning the fully constructed container panel
    }

    /**
     * Handles the sign-in process, including user authentication and saving credentials.
     */
    protected void handleSignIn() {
        String email = (usernameField != null) ? usernameField.getText() : ""; // Retrieving the username from the text field
        String pass = (passwordField != null) ? new String(passwordField.getPassword()) : ""; // Retrieving the password from the password field

        // Validating if both fields are filled
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill out both fields.", // Error message
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO(); // DAO for interacting with the user database
        java.util.Optional<User> maybeUser = userDAO.getUserByEmail(email); // Fetching user details by email

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (user.getPassword() != null && user.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Login successful!"); // Login success message

                // Handling the "Remember Me" functionality
                if (rememberMeCheckBox.isSelected()) {
                    saveCredentials(email, pass); // Saving credentials if the checkbox is selected
                } else {
                    clearCredentials(); // Clearing saved credentials otherwise
                }

                dispose(); // Closing the current window
                new Home(user); // Opening the Home screen
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect password!", // Error message for incorrect password
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "User not found!", // Error message for non-existent user
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the user's credentials (email and password) to a properties file.
     *
     * @param email    The user's email address to save.
     * @param password The user's password to save.
     */
    private void saveCredentials(String email, String password) {
        try (FileWriter writer = new FileWriter("credentials.properties")) {
            Properties props = new Properties(); // Using Java's Properties class to handle key-value pairs
            props.setProperty("username", email); // Storing the username as a property
            props.setProperty("password", password); // Storing the password as a property
            props.store(writer, "User Credentials"); // Writing the properties to the file with a header comment
        } catch (IOException e) {
            e.printStackTrace(); // Printing the exception stack trace for debugging purposes
        }
    }

    /**
     * Loads the user's saved credentials (if available) from a properties file.
     * This method populates the username and password fields if credentials are found.
     */
    private void loadCredentials() {
        try (FileReader reader = new FileReader("credentials.properties")) {
            Properties props = new Properties(); // Loading stored properties from the file
            props.load(reader); // Reading the properties from the file input stream
            String savedUsername = props.getProperty("username"); // Retrieving the saved username
            String savedPassword = props.getProperty("password"); // Retrieving the saved password
            if (savedUsername != null && savedPassword != null) {
                usernameField.setText(savedUsername); // Populating the username field
                passwordField.setText(savedPassword); // Populating the password field
                rememberMeCheckBox.setSelected(true); // Checking the "Remember Me" box
            }
        } catch (IOException e) {
            // File might not exist; this is acceptable as credentials might not be saved
        }
    }

    /**
     * Clears the saved credentials by deleting the credentials.properties file.
     * Ensures that the user's login details are not stored locally.
     */
    private void clearCredentials() {
        File file = new File("credentials.properties"); // Refers to the credentials file
        if (file.exists()) {
            file.delete(); // Deletes the file if it exists
        }
    }

    /**
     * Creates an information section containing labeled fields and values within a rounded panel.
     *
     * @param sectionTitle The title of the section to display above the info panel.
     * @param headerFont   The font used for the section title.
     * @param labelFont    The font used for the field labels and values.
     * @param fieldNames   The array of field names to display.
     * @param fieldValues  The array of corresponding field values.
     * @return A JPanel containing the styled information section.
     */
    public JPanel createInfoSection(String sectionTitle, Font headerFont, Font labelFont, String[] fieldNames, String[] fieldValues) {
        // Creating an outer panel to hold the section title and info panel
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout(0, 10)); // Using a BorderLayout for structure
        outerPanel.setOpaque(false); // Ensures the panel's transparency

        // Creating and styling the section title
        JLabel titleLabel = new JLabel(sectionTitle);
        titleLabel.setFont(headerFont); // Applying the provided font
        titleLabel.setForeground(Color.BLACK); // Setting the text color
        outerPanel.add(titleLabel, BorderLayout.NORTH); // Adding the title above the info panel

        // Creating a rounded container for the info fields
        RoundedPanel container = new RoundedPanel(20, 10); // 20px corner radius and 10px shadow size
        container.setPreferredSize(new Dimension(75, 75)); // Setting the preferred size
        container.setBackground(new Color(240, 240, 240)); // Light gray background
        container.setLayout(new BorderLayout(0, 10)); // Setting the layout for the rounded panel

        // Creating a panel to display field names and values
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false); // Transparent to inherit the background of the rounded container
        container.add(infoPanel, BorderLayout.CENTER); // Adding the info panel to the rounded container

        // Adding field names and values to the info panel using GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); // Setting vertical padding
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Aligning fields to the left

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel fieldNameLabel = new JLabel(fieldNames[i] + ": "); // Label for the field name
            fieldNameLabel.setFont(labelFont.deriveFont(Font.BOLD)); // Bold font for field names
            fieldNameLabel.setForeground(Color.BLACK); // Black text color

            JLabel fieldValueLabel = new JLabel(fieldValues[i]); // Label for the field value
            fieldValueLabel.setFont(labelFont); // Regular font for field values
            fieldValueLabel.setForeground(Color.BLACK); // Black text color

            gbc.gridx = 0; // Adding the field name label to the first column
            infoPanel.add(fieldNameLabel, gbc);

            gbc.gridx = 1; // Adding the field value label to the second column
            infoPanel.add(fieldValueLabel, gbc);

            gbc.gridy++; // Moving to the next row for the next field
        }

        outerPanel.add(container, BorderLayout.CENTER); // Adding the rounded container to the outer panel

        return outerPanel; // Returning the completed info section
    }

    /**
     * Custom JButton implementation for rounded buttons with a specified background color.
     */
    public static class RoundedButtonLogin extends JButton {
        private final Color backgroundColor; // Background color for the button

        /**
         * Constructor for the RoundedButtonLogin class.
         *
         * @param text            The text displayed on the button.
         * @param backgroundColor The background color of the button.
         */
        public RoundedButtonLogin(String text, Color backgroundColor) {
            super(text); // Setting the button text
            this.backgroundColor = backgroundColor; // Storing the background color
            setContentAreaFilled(false); // Ensuring custom painting
            setFocusPainted(false); // Disabling the focus indicator
            setBorderPainted(false); // Hiding the default border
        }

        /**
         * Custom rendering for the button to draw a rounded rectangle.
         *
         * @param g The Graphics object used for drawing.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); // Creating a Graphics2D object for advanced rendering
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enabling anti-aliasing
            g2.setColor(backgroundColor); // Setting the background color
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Drawing a rounded rectangle
            super.paintComponent(g); // Calling the parent method to render text and other elements
            g2.dispose(); // Disposing of the Graphics2D object to free resources
        }

        /**
         * Overriding the border rendering method to remove the border.
         *
         * @param g The Graphics object used for drawing.
         */
        @Override
        protected void paintBorder(Graphics g) {
            // No border is painted
        }
    }

    /**
     * The main method to launch the application.
     * Initializes the `Login` screen using Swing's Event Dispatch Thread.
     *
     * @param args Command-line arguments (unused in this context).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new); // Launching the Login screen
    }
}


package ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;

/**
 * A visual indicator that shows blood glucose levels with color coding and warnings.
 * Colors indicate:
 * - Green: Target range (4.0-8.5 mmol/L)
 * - Yellow: Warning levels that need attention
 * - Red: Dangerous levels requiring immediate action
 */
public class GlucoseIndicator extends JPanel {
    // Defining colors for different glucose level states
    private static final Color SAFE_COLOR = new Color(46, 204, 113);    // A green shade representing safe levels
    private static final Color WARNING_COLOR = new Color(243, 156, 18); // An orange shade representing warning levels
    private static final Color DANGER_COLOR = new Color(231, 76, 60);   // A red shade representing danger levels

    // Defining medical thresholds for blood glucose levels in mmol/L
    private static final double HYPO_THRESHOLD = 3.3;    // Threshold for dangerously low levels
    private static final double LOW_WARNING = 4.0;       // Lower bound of the target range
    private static final double HIGH_WARNING = 7.0;      // Upper bound of the target range
    private static final double HYPER_THRESHOLD = 11.0;  // Threshold for dangerously high levels

    // Declaring variables to store the current glucose level and animation state
    private double glucoseLevel;       // Current blood glucose level
    private Timer warningTimer;        // Timer controlling the flashing animation
    private boolean isFlashing = false; // Boolean flag to indicate if flashing is active

    /**
     * Constructing the GlucoseIndicator panel.
     * Setting up its appearance and initializing the warning animation timer.
     * -- used Oracle (https://docs.oracle.com/javase/8/docs/api/javax/swing/Timer.html#:~:text=Timers%20are%20constructed%20by%20specifying,first%20ActionEvent%20to%20registered%20listeners)
     *          to understand the syntax & role of the built-in Timer function --
     */
    public GlucoseIndicator() {
        // Setting the preferred size of the panel
        setPreferredSize(new Dimension(150, 35));

        // Making the panel transparent
        setOpaque(false);

        // Initializing a Timer for creating a flashing effect during warning/danger states
        warningTimer = new Timer(500, e -> {
            // Toggling the flashing state on each timer tick
            isFlashing = !isFlashing;

            // Requesting a repaint to update the UI with the flashing effect
            repaint();
        });
    }

    /**
     * Updating the displayed glucose level and managing warning animations.
     * @param level The new blood glucose level to display.
     */
    public void updateGlucoseLevel(double level) {
        // Storing the provided glucose level
        this.glucoseLevel = level;

        // Starting the flashing animation if the level is dangerously high or low
        if (level < HYPO_THRESHOLD || level > HYPER_THRESHOLD) {
            if (!warningTimer.isRunning()) {
                // Starting the warning timer if it is not already running
                warningTimer.start();
            }
        } else {
            // Stopping the flashing animation for safe levels
            warningTimer.stop();
            isFlashing = false; // Ensuring the flashing state is reset
        }

        // Requesting a repaint to update the indicator with the new glucose level
        repaint();
    }

    /**
     * Overriding the paintComponent method to draw the glucose indicator.
     * This method customizes the appearance of the panel.
     * @param g The Graphics object used for drawing.
     */
    /* reference -  inspiration & in-built functions were taken from Oracle (https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics2D.html)
     *          albeit entire 6 consecutive lines weren't copied, but cumulatively over 6 lines worth of code came from Oracle*/
    @Override
    protected void paintComponent(Graphics g) {
        // Calling the superclass method to ensure the panel is drawn correctly
        super.paintComponent(g);

        // Creating a Graphics2D object for advanced rendering
        Graphics2D g2 = (Graphics2D) g.create();

        // Enabling anti-aliasing to make edges smoother
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculating the dimensions for the indicator
        int height = getHeight() - 4;  // Subtracting 4 to leave padding around the edges
        int width = getWidth() - 4;   // Subtracting 4 for padding

        // Determining the color and warning message based on the current glucose level
        Color indicatorColor;
        String warningMessage = "";  // Initializing with no message

        // Checking the glucose level and setting the appropriate color and message
        if (glucoseLevel < HYPO_THRESHOLD) {
            // Indicating a dangerously low level
            indicatorColor = isFlashing ? DANGER_COLOR : DANGER_COLOR.brighter(); // Flashing effect
            warningMessage = "LOW - Take fast-acting carbs";
        } else if (glucoseLevel > HYPER_THRESHOLD) {
            // Indicating a dangerously high level
            indicatorColor = isFlashing ? DANGER_COLOR : DANGER_COLOR.brighter(); // Flashing effect
            warningMessage = "HIGH - Check ketones";
        } else if (glucoseLevel < LOW_WARNING) {
            // Indicating a level approaching low
            indicatorColor = WARNING_COLOR;
            warningMessage = "Monitor - Trending Low";
        } else if (glucoseLevel > HIGH_WARNING) {
            // Indicating a level approaching high
            indicatorColor = WARNING_COLOR;
            warningMessage = "Monitor - Trending High";
        } else {
            // Indicating a safe level
            indicatorColor = SAFE_COLOR;
            warningMessage = "In Target Range";
        }

        // Drawing a rounded rectangle as the background for the indicator
        g2.setColor(indicatorColor);
        g2.fillRoundRect(2, 2, width, height, 20, 20); // Rounded corners with radius 20

        // Drawing the glucose level text
        g2.setColor(Color.WHITE); // Using white text for better contrast
        g2.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bold font for emphasis
        String glucoseText = String.format("%.1f mmol/L", glucoseLevel); // Formatting the level

        // Centering the glucose level text horizontally
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(glucoseText)) / 2 + 2;
        int textY = height / 2;  // Adjusting the vertical position
        g2.drawString(glucoseText, textX, textY);

        // Drawing the warning message below the glucose level text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10)); // Smaller font for warning message
        fm = g2.getFontMetrics(); // Recalculating font metrics
        textX = (width - fm.stringWidth(warningMessage)) / 2 + 2; // Centering the message
        textY = height / 2 + 10; // Positioning below the glucose level
        g2.drawString(warningMessage, textX, textY);

        // Releasing the Graphics2D resources
        g2.dispose();
    }
    /* end of reference*/

    /**
     * Overriding the getToolTipText method to provide detailed information about glucose ranges.
     * @return A string containing HTML-formatted tooltip text.
     */
    @Override
    public String getToolTipText() {
        return "<html><b>Blood Glucose Ranges:</b><br>" +
                "Target Range (Green): 4.0-8.5 mmol/L<br>" +
                "Warning (Yellow): 3.3-4.0 or 8.5-10.0 mmol/L<br>" +
                "Danger (Red): Below 3.3 or Above 10.0 mmol/L</html>";
    }
}

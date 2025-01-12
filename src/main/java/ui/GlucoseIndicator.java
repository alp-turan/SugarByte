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
    // Define colors for different states
    private static final Color SAFE_COLOR = new Color(46, 204, 113);    // A clear green for good levels
    private static final Color WARNING_COLOR = new Color(243, 156, 18); // A visible orange for warning levels
    private static final Color DANGER_COLOR = new Color(231, 76, 60);   // A strong red for dangerous levels

    // Medical thresholds for blood glucose (in mmol/L)
    private static final double HYPO_THRESHOLD = 3.3;    // Dangerously low
    private static final double LOW_WARNING = 4.0;       // Lower bound of target range
    private static final double HIGH_WARNING = 8.5;      // Upper bound of target range
    private static final double HYPER_THRESHOLD = 10.0;  // Dangerously high

    // Current glucose level and animation state
    private double glucoseLevel;
    private Timer warningTimer;
    private boolean isFlashing = false;

    public GlucoseIndicator() {
        // Set up the panel's size and appearance
        setPreferredSize(new Dimension(150, 35));
        setOpaque(false);

        // Create a timer for the warning flash animation (twice per second)
        warningTimer = new Timer(500, e -> {
            isFlashing = !isFlashing;  // Toggle flashing state
            repaint();                 // Trigger redraw
        });
    }

    /**
     * Update the displayed glucose level and manage warning animations.
     */
    public void updateGlucoseLevel(double level) {
        this.glucoseLevel = level;

        // Start flashing animation for dangerous levels
        if (level < HYPO_THRESHOLD || level > HYPER_THRESHOLD) {
            if (!warningTimer.isRunning()) {
                warningTimer.start();
            }
        } else {
            warningTimer.stop();
            isFlashing = false;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate dimensions for the indicator
        int height = getHeight() - 4;
        int width = getWidth() - 4;

        // Determine indicator color and warning message based on glucose level
        Color indicatorColor;
        String warningMessage = "";

        if (glucoseLevel < HYPO_THRESHOLD) {
            // Dangerous low level
            indicatorColor = isFlashing ? DANGER_COLOR : DANGER_COLOR.brighter();
            warningMessage = "LOW - Take fast-acting carbs";
        } else if (glucoseLevel > HYPER_THRESHOLD) {
            // Dangerous high level
            indicatorColor = isFlashing ? DANGER_COLOR : DANGER_COLOR.brighter();
            warningMessage = "HIGH - Check ketones";
        } else if (glucoseLevel < LOW_WARNING) {
            // Approaching low
            indicatorColor = WARNING_COLOR;
            warningMessage = "Monitor - Trending Low";
        } else if (glucoseLevel > HIGH_WARNING) {
            // Approaching high
            indicatorColor = WARNING_COLOR;
            warningMessage = "Monitor - Trending High";
        } else {
            // Target range
            indicatorColor = SAFE_COLOR;
            warningMessage = "In Target Range";
        }

        // Draw the rounded rectangle background
        g2.setColor(indicatorColor);
        g2.fillRoundRect(2, 2, width, height, 20, 20);

        // Draw the glucose level
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        String glucoseText = String.format("%.1f mmol/L", glucoseLevel);

        // Center the text
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(glucoseText)) / 2 + 2;
        int textY = height / 2 ;  // Adjust for warning text below

        g2.drawString(glucoseText, textX, textY);

        // Draw the warning message in smaller text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fm = g2.getFontMetrics();
        textX = (width - fm.stringWidth(warningMessage)) / 2 + 2;
        textY = height / 2 + 10;  // Position below glucose level

        g2.drawString(warningMessage, textX, textY);

        g2.dispose();
    }

    /**
     * Override the tooltip to provide detailed range information
     */
    @Override
    public String getToolTipText() {
        return "<html><b>Blood Glucose Ranges:</b><br>" +
                "Target Range (Green): 4.0-8.5 mmol/L<br>" +
                "Warning (Yellow): 3.3-4.0 or 8.5-10.0 mmol/L<br>" +
                "Danger (Red): Below 3.3 or Above 10.0 mmol/L</html>";
    }
}
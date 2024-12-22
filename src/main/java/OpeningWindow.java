import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpeningWindow extends JFrame {

    public OpeningWindow() {
        // Set JFrame properties
        setTitle("SugarByte");
        setSize(400, 800); // Adjusted size to mimic the mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Custom gradient panel as background
        GradientPanel gradientPanel = new GradientPanel();
        gradientPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering components

        // Load the Lobster font from a file
        Font lobsterFont = null;
        try {
            lobsterFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/Resource/Fonts/Lobster.ttf")).deriveFont(36f); // Set the size of the font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(lobsterFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        // Create App Title (SugarByte)
        JLabel titleLabel = new JLabel("SugarByte");
        if (lobsterFont != null) {
            titleLabel.setFont(lobsterFont); // Set custom Lobster font
        } else {
            titleLabel.setFont(new Font("Serif", Font.BOLD, 36)); // Fallback font
        }
        titleLabel.setForeground(Color.WHITE); // Set text color

        // Create Subtitle
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Quick and Easy<br>Diabetic Logbook</div></html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);

        // Add components to panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Add spacing
        gradientPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gradientPanel.add(subtitleLabel, gbc);

        // Add the gradient panel to the frame
        add(gradientPanel);

        setVisible(true); // Display the JFrame
    }

    // Custom JPanel class to draw gradient background
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            // Create gradient from light pink to dark red
            Color startColor = new Color(243, 154, 155); // Light pink
            Color endColor = new Color(136, 16, 24);    // Dark red
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);

            // Draw gradient
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(() -> new OpeningWindow());
    }
}

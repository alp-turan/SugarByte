import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class Login extends JFrame {

    public Login() {
        // Set JFrame properties
        setTitle("LogIn");
        setSize(400, 800); // Adjusted to mimic a mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Load Lobster Font
        Font lobsterFont = loadCustomFont("src/main/Resource/Fonts/Lobster.ttf", 36f);

        // SugarByte Title
        JLabel titleLabel = new JLabel("SugarByte");
        titleLabel.setFont((lobsterFont != null) ? lobsterFont : new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);

        // Username and Password Panels
        JPanel usernamePanel = createInputPanel("Username");
        JPanel passwordPanel = createInputPanel("Password");

        // Sign In Button
        RoundedButton signInButton = new RoundedButton("Sign In", new Color(237, 165, 170));
        signInButton.setForeground(Color.BLACK);
        signInButton.setFont(new Font("Poppins", Font.BOLD, 16));
        signInButton.setPreferredSize(new Dimension(120, 45));

        // Add components to panel
        gbc.insets = new Insets(10, 0, 30, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.gridy = 1;
        mainPanel.add(usernamePanel, gbc);
        gbc.insets = new Insets(13, 0, 0, 0);
        gbc.gridy = 2;
        mainPanel.add(passwordPanel, gbc);
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 0, 0, 0);
        mainPanel.add(signInButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createInputPanel(String labelText) {
        // Outer container to hold the label and input field
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        // Label above the input box
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        // Rounded input panel
        RoundedPanel roundedPanel = new RoundedPanel(70, 10); // 70 for arc, 10 for pixels (can be adjusted)

        roundedPanel.setPreferredSize(new Dimension(300, 75));
        roundedPanel.setBackground(new Color(240, 240, 240));
        roundedPanel.setLayout(new BorderLayout());

        // Text field inside the rounded panel
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        textField.setBackground(new Color(240, 240, 240));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textField.setOpaque(false); // Allow the rounded panel background to show

        roundedPanel.add(textField, BorderLayout.CENTER);

        // Add label and rounded panel to the container
        containerPanel.add(label);
        containerPanel.add(roundedPanel);

        return containerPanel;
    }

    public class RoundedPanel extends JPanel {
        private int arc;      // Corner radius
        private int shadowSize; // Shadow size
        private Color shadowColor;

        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            this.shadowColor = new Color(2, 2, 2, 3); // Semi-transparent shadow
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Enable anti-aliasing for smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the shadow
            g2.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2.fillRoundRect(i, i + shadowSize / 2, getWidth() - i * 2, getHeight() - i * 2 - shadowSize / 2, arc, arc);
            }

            // Draw the panel
            g2.setColor(getBackground());
            g2.fillRoundRect(shadowSize / 2, shadowSize / 2, getWidth() - shadowSize, getHeight() - shadowSize, arc, arc);

            g2.dispose();
        }
    }

    class RoundedButton extends JButton {
        private Color backgroundColor;

        public RoundedButton(String text, Color backgroundColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }

    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color startColor = new Color(255, 255, 255);
            Color endColor = new Color(240, 240, 240);
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);

            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    private Font loadCustomFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}

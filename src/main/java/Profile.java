import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Profile extends JFrame {

    public Profile() {
        // Basic JFrame setup
        setTitle("Profile");
        setSize(400, 800); // Mimic a mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main gradient background panel
        GradientPanel mainPanel = new GradientPanel();
        // Use a vertical BoxLayout so we can stack components
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Load Lobster font (adjust path if needed)
        Font lobsterFont = loadCustomFont("src/fonts/Lobster.ttf", 30f);

        // "SugarByte" title, centered horizontally
        JLabel titleLabel = new JLabel("SugarByte", SwingConstants.CENTER);
        titleLabel.setFont((lobsterFont != null)
                ? lobsterFont
                : new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add some space above/below the title
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
        mainPanel.add(titleLabel);

        // "User’s Information" section
        JLabel userInfoLabel = new JLabel("User’s Information", SwingConstants.CENTER);
        userInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        userInfoLabel.setForeground(Color.BLACK);
        userInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(userInfoLabel);

        // Create rounded panel for user's info
        RoundedPanel userInfoPanel = createInfoPanel(new String[][] {
                {"Full Name", "Name Surname"},
                {"Username", "namesurname"},
                {"Email", "namesurname@gmail.com"},
                {"Type of Diabetes", "Type 1"},
                {"Type of Insulin", "Rapid-acting"},
                {"Type of Insulin Administration", "Pen"},
                {"Logbook Method", "Simple"},
                {"Phone Number", "07498375960"},
                {"Password", "********"}
        });
        mainPanel.add(userInfoPanel);

        // Spacing between sections
        mainPanel.add(Box.createVerticalStrut(20));

        // "Doctor’s Information" section
        JLabel doctorInfoLabel = new JLabel("Doctor’s Information", SwingConstants.CENTER);
        doctorInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        doctorInfoLabel.setForeground(Color.BLACK);
        doctorInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        doctorInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(doctorInfoLabel);

        // Create rounded panel for doctor's info
        RoundedPanel doctorInfoPanel = createInfoPanel(new String[][] {
                {"Full Name", "Name Surname"},
                {"Email", "namesurname@gmail.com"},
                {"Address", "City, Street, Flat, Postcode"},
                {"Emergency Phone", "07287567281"}
        });
        mainPanel.add(doctorInfoPanel);

        // Bottom spacing
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Creates a rounded panel containing rows of (Label | TextField).
     * The label’s width depends on its text length, and the text field
     * expands to fill the remaining space. The label is left-aligned,
     * the text field’s contents are center-aligned (#888888).
     */
    private RoundedPanel createInfoPanel(String[][] fields) {
        RoundedPanel panel = new RoundedPanel(30, 10);
        panel.setBackground(new Color(240, 240, 240));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String[] entry : fields) {
            String labelText = entry[0];
            String defaultValue = entry[1];

            // A row panel for the label + text field
            JPanel rowPanel = new JPanel(new GridBagLayout());
            rowPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST; // keep label to the left

            // Label
            JLabel label = new JLabel(labelText, SwingConstants.LEFT);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(Color.BLACK);

            // textField
            JTextField textField = new JTextField(defaultValue);
            textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            textField.setForeground(new Color(0x88, 0x88, 0x88)); // #888888
            textField.setBackground(new Color(240, 240, 240));
            textField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            // center-align text inside the text field
            textField.setHorizontalAlignment(JTextField.CENTER);

            // GridBag setup: label in column 0, text field in column 1
            // Label: no horizontal stretch
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0; // label gets only the space it needs
            rowPanel.add(label, gbc);

            // Text field: fill remaining horizontal space
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 1;
            gbc.weightx = 1.0; // text field expands
            rowPanel.add(textField, gbc);

            panel.add(rowPanel);
        }
        return panel;
    }

    /**
     * A panel that draws a rounded rectangle with a slight shadow.
     */
    class RoundedPanel extends JPanel {
        private int arc;
        private int shadowSize;
        private Color shadowColor;

        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            this.shadowColor = new Color(2, 2, 2, 3);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Draw the shadow
            g2.setColor(shadowColor);
            for (int i = 0; i < shadowSize; i++) {
                g2.fillRoundRect(
                        i, i + shadowSize / 2,
                        getWidth() - i * 2,
                        getHeight() - i * 2 - shadowSize / 2,
                        arc, arc
                );
            }

            // Draw the actual panel background
            g2.setColor(getBackground());
            g2.fillRoundRect(
                    shadowSize / 2, shadowSize / 2,
                    getWidth() - shadowSize,
                    getHeight() - shadowSize,
                    arc, arc
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * The same gradient panel approach used in your other classes.
     */
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color startColor = new Color(255, 255, 255);
            Color endColor = new Color(240, 240, 240);
            GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, height, endColor
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    /**
     * Loads the custom Lobster font, similar to your previous examples.
     */
    private Font loadCustomFont(String path, float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path))
                    .deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // For testing the Profile page
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Profile());
    }
}
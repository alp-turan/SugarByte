import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class Logbook extends JFrame {

    public Logbook() {
        // Set JFrame properties
        setTitle("Logbook");
        setSize(400, 800); // Adjusted to mimic a mobile screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Load Lobster Font (adjust path as needed)
        Font lobsterFont = loadCustomFont("src/fonts/Lobster.ttf", 32f);

        // "SugarByte" Title
        JLabel titleLabel = new JLabel("SugarByte");
        titleLabel.setFont((lobsterFont != null)
                ? lobsterFont
                : new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.BLACK);

        // Date Label
        JLabel dateLabel = new JLabel("Thursday, 28 Nov");
        dateLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        dateLabel.setForeground(new Color(200, 40, 40)); // or any color you prefer

        // Create the log table panel
        JPanel tablePanel = createLogTable();

        // Save Button (background #D83842)
        RoundedButton saveButton = new RoundedButton("Save", new Color(0xD8, 0x38, 0x42));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Poppins", Font.BOLD, 16));
        saveButton.setPreferredSize(new Dimension(120, 45));

        // Layout setup using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(dateLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(tablePanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 30, 0);
        mainPanel.add(saveButton, gbc);

        // Add the main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Creates the main log table panel with rows for Breakfast, Lunch,
     * Dinner, and Bedtime, each having Pre and Post fields. Each row
     * has two columns for Blood Glucose and Carbs Eaten.
     */
    private JPanel createLogTable() {
        // We'll use a GridBagLayout to emulate a table-like layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // let the gradient show through
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Header row
        JLabel bgHeader = new JLabel("Blood Glucose (mg/dL)");
        bgHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel carbsHeader = new JLabel("Carbs Eaten (g)");
        carbsHeader.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Add empty label in the first column for alignment
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel(""), gbc);

        gbc.gridx = 1;
        panel.add(bgHeader, gbc);

        gbc.gridx = 2;
        panel.add(carbsHeader, gbc);

        // Now add rows for each meal/time slot
        int row = 1;
        row = addMealRows(panel, "Breakfast", row);
        row = addMealRows(panel, "Lunch", row);
        row = addMealRows(panel, "Dinner", row);
        row = addMealRows(panel, "Bedtime", row);

        return panel;
    }

    /**
     * Helper method to add "Pre" and "Post" rows for each meal,
     * each with 2 text fields (Blood Glucose, Carbs Eaten).
     */
    private int addMealRows(JPanel panel, String mealName, int startRow) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 4, 4, 4);

        // First row: e.g. "Breakfast" Pre
        gbc.gridx = 0;
        gbc.gridy = startRow;
        // We'll combine the meal name + "Pre" in one label
        JLabel mealPreLabel = new JLabel(mealName + " Pre");
        mealPreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(mealPreLabel, gbc);

        gbc.gridx = 1;
        JTextField bgPreField = createCellField();
        panel.add(bgPreField, gbc);

        gbc.gridx = 2;
        JTextField carbsPreField = createCellField();
        panel.add(carbsPreField, gbc);

        // Second row: e.g. "Breakfast" Post
        gbc.gridx = 0;
        gbc.gridy = startRow + 1;
        JLabel mealPostLabel = new JLabel(mealName + " Post");
        mealPostLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(mealPostLabel, gbc);

        gbc.gridx = 1;
        JTextField bgPostField = createCellField();
        panel.add(bgPostField, gbc);

        gbc.gridx = 2;
        JTextField carbsPostField = createCellField();
        panel.add(carbsPostField, gbc);

        // Return the next available row
        return startRow + 2;
    }

    /**
     * Creates a text field styled similarly to the input fields
     * from the Login page.
     */
    private JTextField createCellField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(100, 30));
        return textField;
    }

    /**
     * Same GradientPanel class used in your Login code.
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
     * Same RoundedButton class but with a different color for “Save”.
     */
    class RoundedButton extends JButton {
        private Color backgroundColor;

        public RoundedButton(String text, Color backgroundColor) {
            super(text);
            this.backgroundColor = backgroundColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }
    }

    /**
     * Loads the custom Lobster font from file, similarly to your Login code.
     */
    private Font loadCustomFont(String path, float size) {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT, new File(path)
            ).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Logbook());
    }
}

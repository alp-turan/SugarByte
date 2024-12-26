package ui;

import model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;


public class BaseUI extends JFrame {

    public BaseUI(String title) {
        // Set basic JFrame properties
        setTitle(title);
        setSize(400, 800); // Mimic a mobile screen size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Method to load custom fonts
    protected Font loadCustomFont(String path, float size) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Font not found: " + path);
                return new Font("Serif", Font.PLAIN, (int) size);
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    Font lobsterFont = loadCustomFont("/Fonts/Lobster.ttf", 44f);

    // Method to create a title label
    protected JLabel createTitleLabel(String text, Font font, Color color) {
        JLabel titleLabel = new JLabel(text);
        titleLabel.setFont(font != null ? font : new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return titleLabel;
    }

    // Method to create a gradient panel
    protected JPanel createGradientPanel(Color startColor, Color endColor) {
        return new GradientPanel(startColor, endColor);
    }

    // Gradient panel class
    class GradientPanel extends JPanel {
        private final Color startColor;
        private final Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
        }
    }

    // Rounded panel class
    public static class RoundedPanel extends JPanel {
        private final int arc;
        private final int shadowSize;

        public RoundedPanel(int arc, int shadowSize) {
            this.arc = arc;
            this.shadowSize = shadowSize;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shadow
            g2.setColor(new Color(2, 2, 2, 3)); // Semi-transparent shadow
            for (int i = 0; i < shadowSize; i++) {
                g2.fillRoundRect(
                        i,
                        i + shadowSize / 2,
                        getWidth() - i * 2,
                        getHeight() - i * 2 - shadowSize / 2,
                        arc, arc
                );
            }

            // Draw panel
            g2.setColor(getBackground());
            g2.fillRoundRect(
                    shadowSize / 2,
                    shadowSize / 2,
                    getWidth() - shadowSize,
                    getHeight() - shadowSize,
                    arc, arc
            );
            g2.dispose();
        }
    }

    protected ImageIcon loadScaledIcon(String path, int width, int height) {
        BufferedImage sourceImage = null;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Icon not found: " + path);
                return null;
            }
            sourceImage = ImageIO.read(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (sourceImage == null) {
            System.err.println("Failed to read image: " + path);
            return null;
        }

        if (sourceImage.getWidth() == width && sourceImage.getHeight() == height) {
            return new ImageIcon(sourceImage);
        }

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(sourceImage, 0, 0, width, height, null);
        g2.dispose();

        return new ImageIcon(resized);
    }

    public JPanel createInfoSection(
            String sectionTitle,
            Font headerFont,
            Font labelFont,
            String[] fieldNames,
            String[] fieldValues
    ) {
        RoundedPanel container = new RoundedPanel(20, 5);
        container.setBackground(new Color(0, 0, 0, 0));
        container.setLayout(new BorderLayout(0, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Section title
        JLabel titleLabel = new JLabel(sectionTitle);
        titleLabel.setFont(headerFont);
        titleLabel.setForeground(Color.BLACK);
        container.add(titleLabel, BorderLayout.NORTH);

        // Panel for info rows
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(true);
        container.add(infoPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel fieldNameLabel = new JLabel(fieldNames[i] + ": ");
            fieldNameLabel.setFont(labelFont.deriveFont(Font.BOLD));
            fieldNameLabel.setForeground(Color.DARK_GRAY);

            JLabel fieldValueLabel = new JLabel(fieldValues[i]);
            fieldValueLabel.setFont(labelFont);
            fieldValueLabel.setForeground(Color.GRAY);

            gbc.gridx = 0;
            infoPanel.add(fieldNameLabel, gbc);

            gbc.gridx = 1;
            infoPanel.add(fieldValueLabel, gbc);

            gbc.gridy++;
        }

        return container;
    }

    public JButton createIconButton(ImageIcon icon, String altText) {
        JButton button = new JButton(icon);
        button.setToolTipText(altText);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    public JPanel createBottomNavBar(String currentScreen, User currentUser, String icon1, String icon2, String icon3) {
        JPanel navBar = new JPanel();
        navBar.setOpaque(false); // Transparent to show gradient
        navBar.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10)); // Centered with gaps
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding around nav bar

        // Load icons using BaseUI's method
        ImageIcon homeIcon    = loadScaledIcon(icon1, 30, 30);
        ImageIcon calendarIcon = loadScaledIcon(icon2, 30, 30);
        ImageIcon profileIcon = loadScaledIcon(icon3, 30, 30);

        // Create navigation buttons using BaseUI's method
        JButton homeButton = createIconButton(homeIcon, "Home");
        JButton calendarButton = createIconButton(calendarIcon, "Calendar");
        JButton profileButton = createIconButton(profileIcon, "Profile");

        // Refactored ActionListeners using Lambdas

        // Home Button: Already on Home - show a message
        homeButton.addActionListener(e -> {
            if (!"Home".equals(currentScreen)) {
                dispose();
                new Home(currentUser);
            } else {
                JOptionPane.showMessageDialog(this, "Already on Home.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Calendar Button: Navigate to Calendar screen
        calendarButton.addActionListener(e -> {
            if (!"Calendar".equals(currentScreen)) {
                dispose();
                new Calendar(currentUser); 
            } else {
                JOptionPane.showMessageDialog(this, "Already on Calendar.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Profile Button: Already on Profile - show a message
        if (!"Profile".equals(currentScreen)) {
            dispose();
            new Profile(currentUser);
        } else {
            JOptionPane.showMessageDialog(this, "Already on the Profile page.", "Profile",
                    JOptionPane.INFORMATION_MESSAGE);
        }


        // Add buttons to navigation bar
        navBar.add(homeButton);
        navBar.add(calendarButton);
        navBar.add(profileButton);

        return navBar;
    }
}

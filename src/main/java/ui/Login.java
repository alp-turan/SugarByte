package ui;

import database.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends BaseUI {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        super("LogIn");

        // Create main panel with gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title: SugarByte
        JLabel titleLabel = createTitleLabel("SugarByte",lobsterFont, java.awt.Color.BLACK);

        // Username & Password panels
        JPanel usernamePanel = createInputPanel("Username");
        JPanel passwordPanel = createInputPanel("Password");

        // Sign In Button
        RoundedButton signInButton = new RoundedButton("Sign In", new Color(237, 165, 170));
        signInButton.setPreferredSize(new Dimension(130, 40));
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signInButton.setForeground(Color.BLACK);

        // Create Account Button
        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        createAccountBtn.setBackground(new Color(240, 240, 240));
        createAccountBtn.setForeground(Color.DARK_GRAY);
        createAccountBtn.setFocusPainted(false);

        // Layout
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

        gbc.insets = new Insets(15, 0, 0, 0);
        gbc.gridy = 3;
        mainPanel.add(signInButton, gbc);

        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.gridy = 4;
        mainPanel.add(createAccountBtn, gbc);

        add(mainPanel);
        setVisible(true);

        // Action: Sign In
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignIn();
            }
        });

        // Action: Create Account
        createAccountBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CreateAccount();
            }
        });
    }

    private JPanel createInputPanel(String labelText) {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(0,5,5,0));

        RoundedPanel roundedPanel = new RoundedPanel(70, 10);
        roundedPanel.setPreferredSize(new Dimension(300, 75));
        roundedPanel.setBackground(new Color(240, 240, 240));
        roundedPanel.setLayout(new BorderLayout());

        if (labelText.equalsIgnoreCase("Username")) {
            usernameField = new JTextField();
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
            usernameField.setBackground(new Color(240, 240, 240));
            usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            usernameField.setOpaque(false);
            roundedPanel.add(usernameField, BorderLayout.CENTER);
        } else if (labelText.equalsIgnoreCase("Password")) {
            passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
            passwordField.setBackground(new Color(240, 240, 240));
            passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            passwordField.setOpaque(false);
            roundedPanel.add(passwordField, BorderLayout.CENTER);
        }

        containerPanel.add(label);
        containerPanel.add(roundedPanel);
        return containerPanel;
    }

    private void handleSignIn() {
        String email = (usernameField != null) ? usernameField.getText() : "";
        String pass = (passwordField != null) ? new String(passwordField.getPassword()) : "";

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill out both fields.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        java.util.Optional<User> maybeUser = userDAO.getUserByEmail(email);

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (user.getPassword() != null && user.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new Home(user);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "User not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // A custom rounded button
    private static class RoundedButton extends JButton {
        private final Color backgroundColor;

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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // no border
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}

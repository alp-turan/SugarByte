package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends BaseUI {

    public Login() {
        super("LogIn");

        // Create main panel with gradient background
        JPanel mainPanel = createGradientPanel(Color.WHITE, new Color(240, 240, 240));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title: SugarByte
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.BLACK);

        // Username & Password panels (includes "Remember Me" checkbox in password panel)
        JPanel usernamePanel = createInputPanel("Username (email)");
        JPanel passwordPanel = createInputPanel("Password"); // Remember Me is handled in BaseUI

        // Sign In Button
        RoundedButtonLogin signInButton = new RoundedButtonLogin("Sign In", new Color(237, 165, 170));
        //signInButton.setPreferredSize(new Dimension(96, 40));
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signInButton.setForeground(Color.BLACK);

        // Create Account Button
        RoundedButtonLogin createAccountBtn = new RoundedButtonLogin("Create Account", new Color(220, 53, 69));
        //createAccountBtn.setPreferredSize(new Dimension(170, 40)); // Same size as sign-in button
        createAccountBtn.setFont(new Font("SansSerif", Font.BOLD, 16)); // Bold font to match
        createAccountBtn.setForeground(Color.WHITE); // Black text for contrast



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
                handleSignIn(); // Handles Remember Me in BaseUI
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}

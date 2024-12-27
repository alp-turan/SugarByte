package ui;

import database.UserDAO;
import model.User;

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
        JLabel titleLabel = createTitleLabel("SugarByte",lobsterFont, java.awt.Color.BLACK);

        // Username & Password panels
        JPanel usernamePanel = createInputPanel("Username");
        JPanel passwordPanel = createInputPanel("Password");

        // Sign In Button
        RoundedButtonLogin signInButton = new RoundedButtonLogin("Sign In", new Color(237, 165, 170));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}

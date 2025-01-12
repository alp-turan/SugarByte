package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpeningWindow extends BaseUI {

    public OpeningWindow() {
        super("SugarByte");

        // Create a gradient background panel
        JPanel gradientPanel = createGradientPanel(new Color(243, 154, 155), new Color(136, 16, 24));
        gradientPanel.setLayout(new GridBagLayout()); // Center components

        // Title label
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Quick and Easy<br>Diabetic Logbook</div></html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gradientPanel.add(titleLabel, gbc);
        gbc.gridy = 1;
        gradientPanel.add(subtitleLabel, gbc);
        add(gradientPanel);
        setVisible(true);

        // Timer to auto-navigate to Login after 4 seconds
        Timer timer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Login();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpeningWindow::new);
    }
}

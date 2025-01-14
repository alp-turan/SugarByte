package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpeningWindow extends BaseUI {

        /**
        * Constructor for the opening window - extends BaseUI's constructor
         */
        public OpeningWindow() {
            super("SugarByte"); // Calls the BaseUI constructor with the title "SugarByte"

            // Gradient background panel creation, specifying the start and end colors
            JPanel gradientPanel = createGradientPanel(new Color(243, 154, 155), new Color(136, 16, 24));
            gradientPanel.setLayout(new GridBagLayout()); // GridBagLayout centers components within the panel

            // Title label, using a custom font and color
            JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.WHITE);

            // Subtitle label with multi-line HTML styling for centered text
            JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Quick and Easy<br>Diabetic Logbook</div></html>");
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18)); // Sets the font to SansSerif with a plain style and size 18
            subtitleLabel.setForeground(Color.WHITE); // White text color for visibility against the red gradient background

            // Layout constraints for positioning components in the GridBagLayout
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; // Centers the component horizontally
            gbc.gridy = 0; // Positions the title label in the first row
            gbc.insets = new Insets(10, 0, 10, 0); // Adds vertical spacing between components
            gradientPanel.add(titleLabel, gbc); // Adds the title label to the gradient panel at row 0

            gbc.gridy = 1; // Moves to the second row for the subtitle
            gradientPanel.add(subtitleLabel, gbc); // Adds the subtitle label beneath the title

            add(gradientPanel); // Adds the gradient panel to the JFrame's content pane

            setVisible(true); // Displays the window

            // Timer for automatically transitioning to the Login window after 4 seconds
            Timer timer = new Timer(4000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Closes the current OpeningWindow
                    new Login(); // Opens the Login window
                }
            });
            timer.setRepeats(false); // Ensures the timer triggers only once
            timer.start(); // Starts the timer
        }

    }


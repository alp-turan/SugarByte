package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * This class creates the opening window of the SugarByte app, displaying the title
 * and a brief description, followed by an automatic transition (4 seconds later) to the login window.
 */
public class OpeningWindow extends BaseUI {
    // The constructor for the class
    public OpeningWindow() {
        //As OpeningWindow is a daughter class of BaseUI, we use the parent constructor of BaseUI to initialise this
        //class. We pass the title SugarByte, as this is the only argument that needs to be set in BaseUI's constructor.
        //This is the title at the top of the application window
        super("SugarByte");

        //Creating a gradient background panel for aesthetic purposes
        JPanel gradientPanel = createGradientPanel(
                new Color(243, 154, 155), // top gradient colour (light pink)
                new Color(136, 16, 24) // bottom gradient colour (dark red)
        );

        // Layout manager (defines how components arranged on the panel/window) is set to GridBagLayout
        // which arranges components in a grid-like structure.
        // Chosen as it provides precise control over size, position, and alignment of components.
        gradientPanel.setLayout(new GridBagLayout()); // Center components

        //The title label displayed in the center of the application window (ie within the main panel)
        JLabel titleLabel = createTitleLabel("SugarByte", lobsterFont, Color.WHITE);

        //Brief description of the app. HTML tags employed to permit multi-line text & chosen alignment (center).
        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Quick and Easy<br>Diabetic Logbook</div></html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);

        //GridBagConstraints manages components' layout & position.
        //We use GridBagConstraints as earlier we chose GridBagLayout as the layout manager, which interprets the info
        //provided by GridBagConstraints.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        //Adds the title label to the gradient panel according to the position passed above
        gradientPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        //Adds the subtitle to the gradient panel with the same gridx and insets as the title, but at y=1 (below title)
        gradientPanel.add(subtitleLabel, gbc);

        //Adjoins the gradient panel to the main frame & makes window visible
        add(gradientPanel);
        setVisible(true);

        //Creating a timer to auto-navigate to the Login after 4 seconds
        //Giving user sufficient time to read the description on the opening window
        Timer timer = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Dispose of the OpeningWindow firs to free up system resources
                dispose();
                //After disposing, opens a new login window (of class Login)
                new Login();
            }
        });

        //Ensures the timer only goes off once
        timer.setRepeats(false);
        timer.start();
    }
    //This main method lies outside the Constructor, so does not execute when the Main code is run & a new
    //OpeningWindow is instatiated. Instead, it only executes when you run the OpeningWindow class.
    //Useful for debugging & seeing updates/changes you make to the class without having to run the entire program.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(OpeningWindow::new);
    }
}

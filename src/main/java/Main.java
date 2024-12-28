import ui.OpeningWindow;

import javax.swing.SwingUtilities;

/**
 * The main entry point of the application.
 * Simply launches the OpeningWindow, which then leads the user to Login.
 */
public class Main {
    public static void main(String[] args) {
        // this utility 'SwingUtilities.invokeLater()'ensures that the code runs on the Event Dispatch Thread (EDT)
        // a new opening window is launched when running this main method
        SwingUtilities.invokeLater(OpeningWindow::new);
    }
}

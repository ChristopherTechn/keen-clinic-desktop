package utils;

import activation.Activate;
import activation.RestrictedFeature;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import main.Home;

/**
 *
 * @author Mustafa Mohamed.
 */
public class Utils {

    public static void closeDialogOnEscape(JDialog dialog) {
        dialog.getRootPane().registerKeyboardAction(e -> {
            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Show the RestrictedFeature dialog if the program has not been activated.
     *
     * @param feature
     * @return true if the dialog was shown, false otherwise.
     */
    public static boolean featureDeniedDialog(String feature) {
        if (!Activate.isActivated()) {
            RestrictedFeature dialog = new RestrictedFeature(Home.home, true);
            dialog.setText(feature);
            Utils.closeDialogOnEscape(dialog);
            dialog.setLocationRelativeTo(Home.home);
            dialog.setVisible(true);
            return true;
        }
        return false;
    }

    private static int ID = 1000;

    public static int getUniqueId() {
        return ID++;
    }
}

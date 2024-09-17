package main;

import settings.Settings;
import auth.Login;
import auth.Register;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import database.Database;
import java.awt.KeyboardFocusManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import javax.swing.JOptionPane;
import org.httprpc.sierra.ScrollingKeyboardFocusManager;

/**
 *
 * @author Mustafa Mohamed
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/database/logging.properties"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | SecurityException ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

        }
        KeyboardFocusManager.setCurrentKeyboardFocusManager(new ScrollingKeyboardFocusManager());
        FlatLightLaf.setup();
        try {
            Database.createDatabaseIfNotExists();
            Database.runPendingMigrations();
            String theme = "Light";
            try {
                theme = Settings.getTheme();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (theme.equals("Dark")) {
                FlatArcDarkOrangeIJTheme.setup();
            } else {
                FlatLightLaf.setup();
            }
            if (isUserExists()) {
                Login login = new Login();
                login.setVisible(true);
            } else {
                Register register = new Register();
                register.setVisible(true);
            }
        } catch (IOException | SQLException | URISyntaxException ex) {
            ex.printStackTrace();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, """
                                                An error occurred while starting the application.
                                                If the issue persists, please contact the developer.
                                                Click OK to close the application.""",
                    "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static boolean isUserExists() throws SQLException {
        String sql = "SELECT COUNT(*) FROM `user`";
        PreparedStatement stmt = Database.getConnection().prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }

}

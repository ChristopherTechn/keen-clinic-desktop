package settings;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import database.Database;
import java.awt.Color;
import java.awt.event.ItemEvent;
import static java.awt.event.ItemEvent.SELECTED;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import main.DoubleClickPatient;
import static settings.GlobalSettingsEventManager.SETTINGS_MANAGER;

/**
 *
 * @author Mustafa Mohamed
 */
public class Settings extends javax.swing.JDialog {

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());
    private final java.awt.Frame parent;

    /**
     * Creates new form Settings
     *
     * @param parent
     * @param modal
     */
    public Settings(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        try {
            String theme = getTheme();
            jComboBoxTheme.setSelectedItem(theme);
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            jComboBoxTheme.setSelectedItem("Light");
        }

        try {
            jSpinnerFontSize.setValue(getFontSize());
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            jSpinnerFontSize.setValue(12);
        }
        jComboBoxTheme.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == SELECTED) {
                onThemeChanged();
            }
        });
        jSpinnerFontSize.getModel().addChangeListener((ChangeEvent e) -> {
            int size = (int) jSpinnerFontSize.getValue();
            System.setProperty("flatlaf.defaultFont", String.valueOf(size));
            FlatLaf.updateUI();
            try {
                setFontSize(size);
            } catch (SQLException ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        DoubleClickPatient doubleClick = getDoubleClickPatient();
        switch (doubleClick) {
            case DoubleClickPatient.VISIT_HISTORY ->
                jComboBoxOnDobleClickPatient.setSelectedIndex(0);
            case DoubleClickPatient.EDIT_PATIENT ->
                jComboBoxOnDobleClickPatient.setSelectedIndex(1);
            default ->
                jComboBoxOnDobleClickPatient.setSelectedIndex(2);
        }
        jComboBoxOnDobleClickPatient.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == SELECTED) {
                int index = jComboBoxOnDobleClickPatient.getSelectedIndex();
                DoubleClickPatient d = switch (index) {
                    case 0 -> {
                        yield DoubleClickPatient.VISIT_HISTORY;
                    }
                    case 1 -> {
                        yield DoubleClickPatient.EDIT_PATIENT;
                    }
                    default -> {
                        yield DoubleClickPatient.DO_NOTHING;
                    }
                };
                try {
                    setDoubleClickPatient(d);
                } catch (SQLException ex) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        jLabelSomeSettings.putClientProperty("FlatLaf.styleClass", "medium");
    }

    public static DoubleClickPatient getDoubleClickPatient() {
        String sql = "SELECT doubleClickPatient FROM settings LIMIT 1";
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                return DoubleClickPatient.fromString(rs.getString(1));
            } else {
                return DoubleClickPatient.DO_NOTHING;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return DoubleClickPatient.DO_NOTHING;
        }
    }

    public static void setDoubleClickPatient(DoubleClickPatient doubleClick) throws SQLException {
        String insert = "INSERT INTO settings (doubleClickPatient) VALUES(?)";
        String update = "UPDATE settings SET doubleClickPatient = ? ";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, doubleClick.toString());
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                stmt = conn.prepareStatement(insert);
                stmt.setString(1, doubleClick.toString());
                stmt.executeUpdate();
            }
            conn.commit();
        }
    }

    private void onThemeChanged() {
        String theme = (String) jComboBoxTheme.getSelectedItem();
        if (theme.equals("Dark")) {
            //FlatDarkLaf.setup();
            FlatArcDarkOrangeIJTheme.setup();
        } else if (theme.equals("Light")) {
            FlatLightLaf.setup();
        }

        try {
            setTheme(theme);
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        FlatLaf.updateUI();
        SETTINGS_MANAGER.notifyThemeChanged(new ThemeChangedEvent(theme));
    }

    public static String getTheme() throws SQLException {
        String sql = "SELECT theme FROM settings LIMIT 1";
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
            return "Light";
        }
    }

    public static int getFontSize() throws SQLException {
        String sql = "SELECT fontSize FROM settings LIMIT 1";
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1) == null ? 12 : rs.getInt(1);
            }
            return 12;
        }
    }

    public static void setFontSize(int fontSize) throws SQLException {
        String sql = "UPDATE settings SET fontSize = ? ";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fontSize);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                sql = "INSERT INTO settings (fontSize) VALUES(?)";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, fontSize);
                stmt.execute();
            }
            conn.commit();
        }
    }

    public static void setTheme(String theme) throws SQLException {
        String sql = "UPDATE settings SET theme = ? ";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, theme);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                sql = "INSERT INTO settings (theme) VALUES(?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, theme);
                stmt.execute();
            }
            conn.commit();

        }
    }

    /**
     * Get the color to use for icons based on the current theme.
     *
     * @return the color to use.
     * @throws java.sql.SQLException
     */
    public static Color getIconsColor() throws SQLException {
        String theme = getTheme();
        if (theme.equalsIgnoreCase("dark")) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOK = new javax.swing.JButton();
        jLabelSomeSettings = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxTheme = new javax.swing.JComboBox<>();
        jSpinnerFontSize = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxOnDobleClickPatient = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jLabelSomeSettings.setText("Some changes will take effect the next time you start the application");

        jLabel1.setText("Theme");

        jLabel2.setText("Font size");

        jComboBoxTheme.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Light", "Dark" }));

        jSpinnerFontSize.setModel(new javax.swing.SpinnerNumberModel(5, 5, 30, 1));
        jSpinnerFontSize.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinnerFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(255, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxTheme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinnerFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(148, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Appearance", jPanel1);

        jLabel3.setText("Double clicking a patient on the main window will");

        jComboBoxOnDobleClickPatient.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Open \"Visit History\"", "Open \"Edit Patient\"", "Do nothing" }));
        jComboBoxOnDobleClickPatient.setSelectedIndex(2);
        jComboBoxOnDobleClickPatient.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxOnDobleClickPatientItemStateChanged(evt);
            }
        });

        jButton1.setText("Delete configuration data...");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxOnDobleClickPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxOnDobleClickPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Advanced", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonOK))
                    .addComponent(jLabelSomeSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSomeSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOK)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jComboBoxOnDobleClickPatientItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxOnDobleClickPatientItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxOnDobleClickPatientItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox<String> jComboBoxOnDobleClickPatient;
    private javax.swing.JComboBox<String> jComboBoxTheme;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelSomeSettings;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSpinner jSpinnerFontSize;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

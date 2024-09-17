package main;

import settings.Settings;
import activation.Activate;
import activation.ActivationDialog;
import auth.ChangePassword;
import auth.PromptPassword;
import auth.Register;
import auth.SecurityQuestions;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatButton;
import database.BackUp;
import database.RestoreBackup;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JOptionPane;
import main.drug.DrugsHome;
import static main.drug.event.GlobalDrugEventManager.DRUG_EVENT_MANAGER;
import main.patient.PatientHome;
import static main.patient.event.GlobalEventManager.PATIENT_LISTENER_MANAGER;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.PASSWORD;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.SETTINGS;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.USER_ACTIVITY;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.USER_AVATAR;
import org.kordamp.ikonli.swing.FontIcon;
import main.patient.patient.NewPatient;
import main.patient.patient.Patient;
import utils.Utils;
import static utils.Utils.featureDeniedDialog;
import main.patient.visit.NewOutpatient;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.MEDICATION;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.NOTIFICATION;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.USER;
import settings.DatabaseRestoredEvent;
import static settings.GlobalSettingsEventManager.SETTINGS_MANAGER;
import settings.SettingsEventListener;
import settings.ThemeChangedEvent;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import database.Database;
import static main.notification.GlobalNotificationEventManager.NOTIFICATION_EVENT_MANAGER;
import main.notification.ListNotifications;
import main.notification.NotificationAddedEvent;
import main.notification.NotificationEventListener;
import main.notification.NotificationViewedEvent;
import main.notification.NotificationsListedEvent;
import static main.patient.event.GlobalEventManager.VISIT_LISTENER_MANAGER;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.NOTIFICATION_NEW;
import java.sql.*;

/**
 *
 * @author Mustafa Mohamed
 */
public class Home extends javax.swing.JFrame implements SettingsEventListener, NotificationEventListener {

    private final PatientHome panelPatientHome = new PatientHome();
    private final DrugsHome panelDrugsHome = new DrugsHome();
    private final FlatButton buttonNotifications = new FlatButton();

    public static Home home;

    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();
        buttonNotifications.addActionListener(e -> onNotifications());
        jMenuBar1.add(Box.createGlue());
        jMenuBar1.add(buttonNotifications);
        setupMenubar();
        setToolbarIcons();
        setupTabs();
        if (!Activate.isActivated()) {
            setTitle(getTitle() + " - Evaluation Version");
        }

    }

    private Color getIconColor() {
        Color color = Color.BLACK;
        try {
            color = Settings.getIconsColor();
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
        return color;
    }

    private final int iconSize = 25;

    private void setupMenubar() {
        int unreadNotifs = 0;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT IFNULL(COUNT(*),0) FROM notifications WHERE viewedAt IS NULL";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                unreadNotifs = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }

        buttonNotifications.setText("");
        Color color = getIconColor();
        buttonNotifications.setIcon(FontIcon.of(unreadNotifs == 0 ? NOTIFICATION : NOTIFICATION_NEW, iconSize, color));
        buttonNotifications.setButtonType(ButtonType.toolBarButton);
        buttonNotifications.setFocusable(false);
        buttonNotifications.setToolTipText("Notifications");
    }

    public final void setToolbarIcons() {

        Color color = getIconColor();

        FontIcon icon = new FontIcon();
        icon.setIkon(USER_AVATAR);
        icon.setIconSize(iconSize);
        icon.setIconColor(color);
        jButtonAddPatient.setIcon(icon);
        jButtonAddPatient.setText("");

        icon = new FontIcon();
        icon.setIkon(USER_ACTIVITY);
        icon.setIconSize(iconSize);
        icon.setIconColor(color);
        jButtonAddOutpatient.setIcon(icon);
        jButtonAddOutpatient.setText("");

        icon = FontIcon.of(SETTINGS, iconSize, color);
        jButtonSettings.setIcon(icon);
        jButtonSettings.setText("");

        jButtonChangePassword.setIcon(FontIcon.of(PASSWORD, iconSize, color));
        jButtonChangePassword.setText("");

    }

    private void setupTabIcons() {
        Color color = getIconColor();
        jTabbedPaneTabs.setIconAt(0, FontIcon.of(USER, iconSize, color));
        jTabbedPaneTabs.setIconAt(1, FontIcon.of(MEDICATION, iconSize, color));
        jTabbedPaneTabs.revalidate();
        jTabbedPaneTabs.repaint();
    }

    private void setupTabs() {
        jPanelDrugs.add(panelDrugsHome);
        jPanelPatients.add(panelPatientHome);
        jTabbedPaneTabs.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ALIGNMENT, FlatClientProperties.TABBED_PANE_ALIGN_LEADING);
        jTabbedPaneTabs.putClientProperty("JTabbedPane.selectionFollowsFocus", true);
        jTabbedPaneTabs.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_ALIGNMENT,
                FlatClientProperties.TABBED_PANE_ALIGN_LEADING);
        jTabbedPaneTabs.revalidate();
        jTabbedPaneTabs.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButtonAddPatient = new javax.swing.JButton();
        jButtonAddOutpatient = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonSettings = new javax.swing.JButton();
        jButtonChangePassword = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jTabbedPaneTabs = new javax.swing.JTabbedPane();
        jPanelPatients = new javax.swing.JPanel();
        jPanelDrugs = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSecurityQuestions = new javax.swing.JMenuItem();
        jMenuItemChangePassword = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemBackUp = new javax.swing.JMenuItem();
        jMenuItemRestore = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemActivation = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Keen Clinic");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jToolBar1.setRollover(true);

        jButtonAddPatient.setText("jButton1");
        jButtonAddPatient.setToolTipText("Add a patient...");
        jButtonAddPatient.setFocusable(false);
        jButtonAddPatient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddPatient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPatientActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAddPatient);

        jButtonAddOutpatient.setText("jButton2");
        jButtonAddOutpatient.setToolTipText("Add an outpatient visit...");
        jButtonAddOutpatient.setFocusable(false);
        jButtonAddOutpatient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddOutpatient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddOutpatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddOutpatientActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAddOutpatient);
        jToolBar1.add(jSeparator1);

        jButtonSettings.setText("jButton1");
        jButtonSettings.setToolTipText("Settings");
        jButtonSettings.setFocusable(false);
        jButtonSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSettings);

        jButtonChangePassword.setText("jButton1");
        jButtonChangePassword.setToolTipText("Change password");
        jButtonChangePassword.setFocusable(false);
        jButtonChangePassword.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonChangePassword.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangePasswordActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonChangePassword);
        jToolBar1.add(filler1);

        jTabbedPaneTabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPaneTabs.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanelPatients.setToolTipText("");
        jPanelPatients.setLayout(new java.awt.BorderLayout());
        jTabbedPaneTabs.addTab("", FontIcon.of(USER, iconSize, getIconColor()), jPanelPatients, "Patients and visits");

        jPanelDrugs.setLayout(new java.awt.BorderLayout());
        jTabbedPaneTabs.addTab("", FontIcon.of(MEDICATION, iconSize, getIconColor()), jPanelDrugs, "Manage your drugs stock");

        jMenu1.setText("File");

        jMenuItemSettings.setText("Settings");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSettings);
        jMenu1.add(jSeparator5);

        jMenuItemSecurityQuestions.setText("Security Questions");
        jMenuItemSecurityQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSecurityQuestionsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSecurityQuestions);

        jMenuItemChangePassword.setText("Change Password...");
        jMenuItemChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangePasswordActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemChangePassword);
        jMenu1.add(jSeparator2);

        jMenuItemBackUp.setText("Back up Data...");
        jMenuItemBackUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBackUpActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemBackUp);

        jMenuItemRestore.setText("Restore from Backup...");
        jMenuItemRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRestoreActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemRestore);
        jMenu1.add(jSeparator4);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem4.setText("Add Patient...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Add Outpatient Visit...");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItemActivation.setText("Activation");
        jMenuItemActivation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemActivationActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemActivation);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        jMenuItem1.setText("About");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPaneTabs)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPatientActionPerformed
        onAddPatient();
    }//GEN-LAST:event_jButtonAddPatientActionPerformed

    private void jButtonAddOutpatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddOutpatientActionPerformed
        onAddOutpatient();
    }//GEN-LAST:event_jButtonAddOutpatientActionPerformed

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed
        onSettings();
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jButtonChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangePasswordActionPerformed
        onChangePassword();
    }//GEN-LAST:event_jButtonChangePasswordActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        onAbout();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        onAddOutpatient();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        onAddPatient();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed
        onSettings();
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        onExit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemBackUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBackUpActionPerformed
        onBackUp();
    }//GEN-LAST:event_jMenuItemBackUpActionPerformed

    private void jMenuItemRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRestoreActionPerformed
        onRestoreFromBackup();
    }//GEN-LAST:event_jMenuItemRestoreActionPerformed

    private void jMenuItemActivationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemActivationActionPerformed
        onActivation();
    }//GEN-LAST:event_jMenuItemActivationActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        SETTINGS_MANAGER.addListener(this);
        SETTINGS_MANAGER.addListener(panelDrugsHome);
        SETTINGS_MANAGER.addListener(panelPatientHome);
        PATIENT_LISTENER_MANAGER.addListener(panelPatientHome);
        VISIT_LISTENER_MANAGER.addListener(panelPatientHome);
        DRUG_EVENT_MANAGER.addListener(panelDrugsHome);
        NOTIFICATION_EVENT_MANAGER.addListener(this);

    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        SETTINGS_MANAGER.removeListener(this);
        SETTINGS_MANAGER.removeListener(panelDrugsHome);
        SETTINGS_MANAGER.removeListener(panelPatientHome);
        PATIENT_LISTENER_MANAGER.removeListener(panelPatientHome);
        VISIT_LISTENER_MANAGER.removeListener(panelPatientHome);
        DRUG_EVENT_MANAGER.removeListener(panelDrugsHome);
        NOTIFICATION_EVENT_MANAGER.removeListener(this);

    }//GEN-LAST:event_formWindowClosed

    private void jMenuItemSecurityQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSecurityQuestionsActionPerformed
        onSecurityQuestions();
    }//GEN-LAST:event_jMenuItemSecurityQuestionsActionPerformed

    private void jMenuItemChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangePasswordActionPerformed
        onChangePassword();
    }//GEN-LAST:event_jMenuItemChangePasswordActionPerformed

    private void onNotifications() {
        NotificationsListedEvent event = new NotificationsListedEvent();
        NOTIFICATION_EVENT_MANAGER.notifyNotificationsListed(event);
        ListNotifications dialog = new ListNotifications(this, false);
        Utils.closeDialogOnEscape(dialog);
        dialog.setLocation(buttonNotifications.getLocationOnScreen().x - 5 - dialog.getWidth() / 2, buttonNotifications.getLocationOnScreen().y + buttonNotifications.getHeight());
        dialog.setVisible(true);
    }

    private void onSecurityQuestions() {
        if (Activate.isActivated()) {
            SecurityQuestions dialog = new SecurityQuestions(this, true);
            Utils.closeDialogOnEscape(dialog);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            String message = "Please activate Keen Clinic in order to update security questions.\n";
            message += "To activate, select Help -> Activate";
            String title = "Activation Required";
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onActivation() {
        if (Activate.isActivated()) {
            JOptionPane.showMessageDialog(this, """
                                                Congratulations. You are using an activated version of Keen Clinic.
                                                Thank you for using Keen Clinic.""",
                    "Activated", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ActivationDialog dialog = new ActivationDialog(this, true);
            Utils.closeDialogOnEscape(dialog);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (Activate.isActivated()) {
                setTitle("Keen Clinic");
            }
        }

    }

    private void onRestoreFromBackup() {
        if (!featureDeniedDialog("""
                                 Restoring from backup is not available on the evaluation version of Keen Clinic.
                                 
                                 Please activate to access all features.""")) {
            RestoreBackup dialog = new RestoreBackup(this, true);
            dialog.setLocationRelativeTo(this);
            Utils.closeDialogOnEscape(dialog);
            dialog.setVisible(true);
            if (dialog.isRestored()) {
                Register r = new Register();
                r.setHeaderText("Restoring from backup requires updating your credentials.");
                r.disableLogin();
                r.isFromRestore();
                r.setVisible(true);
                r.requestFocus();
            }
        }
    }

    private void onBackUp() {
        if (!featureDeniedDialog("""
                                 Backing up data is not available on the evaluation version of Keen Clinic.
                                 
                                 Please activate to access all features.""")) {
            PromptPassword dialog = new PromptPassword(this, true);
            dialog.setLocationRelativeTo(this);
            Utils.closeDialogOnEscape(dialog);
            dialog.setVisible(true);
            if (dialog.isValidPassword()) {
                BackUp b = new BackUp(this, true);
                b.setLocationRelativeTo(this);
                Utils.closeDialogOnEscape(b);
                b.setVisible(true);
            }
        }

    }

    private void onExit() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void onAbout() {
        About about = new About(this, true);
        about.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(about);
        about.setVisible(true);
    }

    private void onChangePassword() {
        ChangePassword dialog = new ChangePassword(this, true);
        dialog.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(dialog);
        dialog.setVisible(true);
    }

    private void onSettings() {
        Settings settings = new Settings(this, true);
        settings.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(settings);
        settings.setVisible(true);
    }

    private void onAddPatient() {
        try {
            int count = Patient.countPatients();
            if (count >= 35 && !Activate.isActivated()) {
                Utils.featureDeniedDialog("""
                                          The evaluation version of Keen Clinic allows adding up to 35 patients.
                                          
                                          
                                          Please activate to access all features.""");
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(NewPatient.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "An error occurred. "
                    + "Something's not right. Please try again after a few moments.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        NewPatient dialog = new NewPatient(this, false);
        dialog.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(dialog);
        dialog.initWindow();
        dialog.setVisible(true);
        if (dialog.getPatientId() > 0) {
        }
    }

    private void onAddOutpatient() {

        NewOutpatient dialog = new NewOutpatient(this, false);
        dialog.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(dialog);
        dialog.initWindow();
        dialog.setVisible(true);

    }

    @Override
    public void onThemeChanged(ThemeChangedEvent event) {
        setToolbarIcons();
        setupTabIcons();
        setupMenubar();
    }

    @Override
    public void onDatabaseRestored(DatabaseRestoredEvent event) {
        //close all child dialogs of this frame
        for (Window w : getOwnedWindows()) {
            w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void onNotificationAdded(NotificationAddedEvent event) {
        Color color = getIconColor();
        buttonNotifications.setIcon(FontIcon.of(NOTIFICATION_NEW, iconSize, color));
    }

    @Override
    public void onNotificationsListed(NotificationsListedEvent event) {

    }

    @Override
    public void onNotificationViewed(NotificationViewedEvent event) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT IFNULL(COUNT(*),0) FROM notifications WHERE viewedAt IS NULL";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    Color color = getIconColor();
                    buttonNotifications.setIcon(FontIcon.of(NOTIFICATION, iconSize, color));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButtonAddOutpatient;
    private javax.swing.JButton jButtonAddPatient;
    private javax.swing.JButton jButtonChangePassword;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItemActivation;
    private javax.swing.JMenuItem jMenuItemBackUp;
    private javax.swing.JMenuItem jMenuItemChangePassword;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemRestore;
    private javax.swing.JMenuItem jMenuItemSecurityQuestions;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JPanel jPanelDrugs;
    private javax.swing.JPanel jPanelPatients;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPaneTabs;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}

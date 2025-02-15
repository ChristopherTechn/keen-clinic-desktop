package database;

import com.formdev.flatlaf.ui.FlatUIUtils;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import java.sql.*;
import javax.swing.JOptionPane;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Mustafa Mohamed
 */
public class BackUp extends javax.swing.JDialog {

    private File backupLocation;
    private final Logger LOGGER = Logger.getLogger(BackUp.class.getName());

    /**
     * Creates new form BackUp
     *
     * @param parent
     * @param modal
     */
    public BackUp(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jLabelFolder.setText("Backup location not set.");
        jButtonChangeFolder.setText("Select backup location...");
        jButtonBackup.setEnabled(false);

        jLabel1.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        jLabel2.putClientProperty("FlatLaf.styleClass", "semibold");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelFolder = new javax.swing.JLabel();
        jButtonChangeFolder = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonBackup = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Back up Data");
        setResizable(false);

        jLabel1.setText("Please back up your data in a secure location");

        jLabel2.setText("Backup location");

        jLabelFolder.setText("jLabel3");

        jButtonChangeFolder.setText("Change backup location...");
        jButtonChangeFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeFolderActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonBackup.setText("Back up now");
        jButtonBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonBackup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonChangeFolder)
                                .addGap(0, 345, Short.MAX_VALUE))
                            .addComponent(jLabelFolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabelFolder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonChangeFolder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonBackup))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonChangeFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeFolderActionPerformed
        onChangeLocation();
    }//GEN-LAST:event_jButtonChangeFolderActionPerformed

    private void jButtonBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackupActionPerformed
        onBackUpNow();
    }//GEN-LAST:event_jButtonBackupActionPerformed

    private void onChangeLocation() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(jButtonChangeFolder.getText());
        chooser.setApproveButtonText("Select");
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (backupLocation != null) {
            chooser.setCurrentDirectory(backupLocation);
        }
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            backupLocation = chooser.getSelectedFile();
            jButtonChangeFolder.setText("Change backup location...");
            jLabelFolder.setText(backupLocation.getAbsolutePath());
            jButtonBackup.setEnabled(true);
        }
    }

    private void onBackUpNow() {
        // to back up, we simply copy the database file and remove the password.
        String originalDatabasePath = Database.getDatabaseFilePath();
        String destinationDatabasePath = backupLocation.getAbsolutePath()
                + File.separator + "Backup" + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("d-MM-yy-HH-mm-ss")) + ".db";
        try {
            File originalDatabase = new File(originalDatabasePath);
            File destinationDatabase = new File(destinationDatabasePath);
            Files.copy(originalDatabase.toPath(), destinationDatabase.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + destinationDatabasePath)) {
                conn.setAutoCommit(false);
                String sql = "DELETE FROM `user`";
                conn.createStatement().execute(sql);
                conn.commit();
            }
            JOptionPane.showMessageDialog(this,
                    "Database backed up successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            close();
        } catch (HeadlessException | IOException | SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            try {
                // remove backup if it was created
                if (new File(destinationDatabasePath).exists()) {
                    new File(destinationDatabasePath).delete();
                }
            } catch (Exception ex2) {
                LOGGER.log(Level.SEVERE, null, ex2);
            }
            JOptionPane.showMessageDialog(this,
                    "An error occurred.\nThe backup did not succeed.",
                    "Backup Failed", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBackup;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChangeFolder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelFolder;
    // End of variables declaration//GEN-END:variables
}

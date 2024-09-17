package auth;

import database.Database;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Security Questions.
 *
 * @author Mustafa Mohamed
 */
public class SecurityQuestions extends javax.swing.JDialog {

    /**
     * Creates new form SecurityQuestions
     *
     * @param parent
     * @param modal
     */
    public SecurityQuestions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        jLabelTitle.putClientProperty("FlatLaf.styleClass", "h2");

        for (JLabel label : new JLabel[]{jLabelQuestion1, jLabelQuestion2, jLabelQuestion3}) {
            label.putClientProperty("FlatLaf.styleClass", "h3");
        }

        jTextFieldMotherMaidenName.putClientProperty("JTextField.placeholderText", "Mother's maiden name");
        jTextFieldSpouse.putClientProperty("JTextField.placeholderText", "City you met your spouse/significant other");
        jTextFieldSport.putClientProperty("JTextField.placeholderText", "Favorite sport");

        JTextField textFields[] = new JTextField[]{
            jTextFieldMotherMaidenName, jTextFieldSpouse, jTextFieldSport
        };
        for (var textField : textFields) {
            textField.putClientProperty("JTextField.selectAllOnFocusPolicy", "always");
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    textField.putClientProperty("JComponent.outline", textField.getText().isBlank() ? "error" : "");
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    textField.putClientProperty("JComponent.outline", textField.getText().isBlank() ? "error" : "");
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    textField.putClientProperty("JComponent.outline", textField.getText().isBlank() ? "error" : "");
                }
            });
        }
        
        getSecurityQuestions();

    }

    private boolean isValidInput() {
        JTextField textFields[] = new JTextField[]{
            jTextFieldMotherMaidenName, jTextFieldSpouse, jTextFieldSport
        };
        boolean isValid = true;
        for (var textField : textFields) {
            textField.putClientProperty("JComponent.outline", "");
            if (textField.getText().isBlank()) {
                textField.putClientProperty("JComponent.outline", "error");
                if (isValid) {
                    isValid = true;
                    textField.requestFocusInWindow();
                }
            }
        }
        return isValid;
    }

    private void onSave() {
        if (isValidInput()) {
            try {
                saveSecurityQuestions();
                JOptionPane.showMessageDialog(this, "Your security questions were updated successfully.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                close();
            } catch (SQLException ex) {
                Logger.getLogger(SecurityQuestions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                        "An error occurred while updating your security questions. "
                        + "Please try again later.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void getSecurityQuestions() {
        String sql = "SELECT mothersMaidenName, cityMetSpouse, favoriteSport FROM securityQuestions "
                + "WHERE userId = ? ";

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Login.LOGGED_IN_USER_ID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mothersMaidenName = rs.getString(1);
                String city = rs.getString(2);
                String sport = rs.getString(3);

                jTextFieldMotherMaidenName.setText(mothersMaidenName);
                jTextFieldSport.setText(sport);
                jTextFieldSpouse.setText(city);
            }
        } catch (SQLException ex) {

        }

    }

    private void saveSecurityQuestions() throws SQLException {
        // upsert the security questions for the logged in user
        String sql = "INSERT INTO securityQuestions (userId, mothersMaidenName, cityMetSpouse, favoriteSport) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT (userId) "
                + "DO "
                + "UPDATE SET mothersMaidenName = ?, cityMetSpouse= ?, favoriteSport = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Login.LOGGED_IN_USER_ID);
            stmt.setString(2, jTextFieldMotherMaidenName.getText().trim());
            stmt.setString(3, jTextFieldSpouse.getText().trim());
            stmt.setString(4, jTextFieldSport.getText().trim());

            stmt.setString(5, jTextFieldMotherMaidenName.getText().trim());
            stmt.setString(6, jTextFieldSpouse.getText().trim());
            stmt.setString(7, jTextFieldSport.getText().trim());

            int updated = stmt.executeUpdate();

            if (updated == 1) {
                conn.commit();
            } else {
                conn.rollback();
                throw new SQLException("Could not update security questions for user " + Login.LOGGED_IN_USER_ID);
            }
        }
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jLabelQuestion1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldMotherMaidenName = new javax.swing.JTextField();
        jLabelQuestion2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldSpouse = new javax.swing.JTextField();
        jLabelQuestion3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldSport = new javax.swing.JTextField();
        jButtonCancel = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Security Questions");
        setResizable(false);

        jLabelTitle.setText("Set your security questions in case you forget your password");

        jLabelQuestion1.setText("Question 1");

        jLabel3.setText("What is your mother's maiden name?");

        jLabelQuestion2.setText("Question 2");

        jLabel5.setText("In what city did you meet your spouse/significant other?");

        jLabelQuestion3.setText("Question 3");

        jLabel7.setText("What is your favorite sport?");

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTitle)
                            .addComponent(jLabelQuestion1)
                            .addComponent(jLabelQuestion2)
                            .addComponent(jLabelQuestion3)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5)
                                    .addComponent(jTextFieldMotherMaidenName, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(jTextFieldSpouse, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldSport, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 278, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTitle)
                .addGap(18, 18, 18)
                .addComponent(jLabelQuestion1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldMotherMaidenName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelQuestion2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldSpouse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelQuestion3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldSport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonSave))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        onSave();
    }//GEN-LAST:event_jButtonSaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelQuestion1;
    private javax.swing.JLabel jLabelQuestion2;
    private javax.swing.JLabel jLabelQuestion3;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JTextField jTextFieldMotherMaidenName;
    private javax.swing.JTextField jTextFieldSport;
    private javax.swing.JTextField jTextFieldSpouse;
    // End of variables declaration//GEN-END:variables
}

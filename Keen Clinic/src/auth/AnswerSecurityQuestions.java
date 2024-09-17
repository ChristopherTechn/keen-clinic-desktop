package auth;

import database.Database;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mustafa Mohamed
 */
public class AnswerSecurityQuestions extends javax.swing.JDialog {

    private final java.awt.Frame parent;

    /**
     * Creates new form AnswerSecurityQuestions
     *
     * @param parent
     * @param modal
     */
    public AnswerSecurityQuestions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;

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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonCancel = new javax.swing.JButton();
        jButtonSubmit = new javax.swing.JButton();
        jLabelQuestion1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldMotherMaidenName = new javax.swing.JTextField();
        jLabelQuestion2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldSpouse = new javax.swing.JTextField();
        jLabelQuestion3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldSport = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Answer Security Questions");

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonSubmit.setText("Submit");
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jLabelQuestion1.setText("Question 1");

        jLabel3.setText("What is your mother's maiden name?");

        jLabelQuestion2.setText("Question 2");

        jLabel5.setText("In what city did you meet your spouse/significant other?");

        jLabelQuestion3.setText("Question 3");

        jLabel7.setText("What is your favorite sport?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 546, Short.MAX_VALUE)
                        .addComponent(jButtonSubmit)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonSubmit))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        onSubmit();
    }//GEN-LAST:event_jButtonSubmitActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        onCancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void onCancel() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void onSubmit() {
        String mothersMaidenName = jTextFieldMotherMaidenName.getText();
        String city = jTextFieldSpouse.getText();
        String sport = jTextFieldSport.getText();

        String error = "";

        String texts[] = new String[]{mothersMaidenName, city, sport};
        for (String t : texts) {
            if (t.isBlank()) {
                if (error.isBlank()) {
                    error = "Please answer all security questions.";
                    break;
                }
            }
        }

        if (!error.isBlank()) {
            JOptionPane.showMessageDialog(this, error, "Answer all Questions", JOptionPane.WARNING_MESSAGE);
        } else {
            String sql = "SELECT username FROM securityQuestions "
                    + "INNER JOIN `user` ON securityQuestions.userId = `user`.id "
                    + "WHERE mothersMaidenName = ? AND cityMetSpouse = ? AND favoriteSport = ? ";
            try (Connection conn = Database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, mothersMaidenName);
                stmt.setString(2, city);
                stmt.setString(3, sport);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String username = rs.getString(1);
                    NewPasswordForgot dialog = new NewPasswordForgot(username, this.parent, true);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    boolean isReset = dialog.isPasswordReset();
                    if (isReset) {
                        close();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "The answers you provided are not correct. Try a different combination.", "Invalid Answers", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(AnswerSecurityQuestions.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                        "An error occurred while checking your answers to the questions. Please try again later.",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelQuestion1;
    private javax.swing.JLabel jLabelQuestion2;
    private javax.swing.JLabel jLabelQuestion3;
    private javax.swing.JTextField jTextFieldMotherMaidenName;
    private javax.swing.JTextField jTextFieldSport;
    private javax.swing.JTextField jTextFieldSpouse;
    // End of variables declaration//GEN-END:variables
}

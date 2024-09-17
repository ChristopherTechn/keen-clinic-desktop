package main.patient.patient;

import activation.Activate;
import database.AppProperties;
import java.sql.*;
import database.Database;
import static main.patient.event.GlobalEventManager.PATIENT_LISTENER_MANAGER;
import main.patient.event.PatientAddedEvent;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import utils.Utils;

/**
 *
 * @author Mustafa Mohamed
 */
public class NewPatient extends javax.swing.JDialog {

    private int patientId;

    public int getPatientId() {
        return patientId;
    }

    /**
     * Creates new form NewPatient
     *
     * @param parent
     * @param modal
     */
    public NewPatient(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jLabelStatus.setText("");
        //datePickerBirth.setPopupVerticalAlignment(VerticalAlignment.TOP);
        datePickerBirth.setDate(new Date());

        DocumentListener l = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setStatus("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setStatus("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setStatus("");
            }
        };
        for (JTextField t : new JTextField[]{jTextFieldAddress, jTextFieldFirstName, jTextFieldLastName, jTextFieldOtherNames, jTextFieldPhone}) {
            t.getDocument().addDocumentListener(l);
        }
        jTextAreaRemarks.getDocument().addDocumentListener(l);
        String promptTexts[] = {
            "Outpatient number",
            "First name",
            "Last name",
            "Other names",
            "Address",
            "Phone number"
        };
        int i = 0;
        for (JTextField t : new JTextField[]{jTextFieldOutpatientNumber, jTextFieldFirstName, jTextFieldLastName, jTextFieldOtherNames, jTextFieldAddress, jTextFieldPhone}) {
            t.putClientProperty("JTextField.placeholderText", promptTexts[i]);
            i++;
        }
        jCheckBoxBirth.setSelected(false);
        datePickerBirth.setEnabled(false);

    }

    public void initWindow() {
        int width = getWidth();
        int height = getHeight();
        int x = getLocation().x;
        int y = getLocation().y;

        width = Integer.parseInt(AppProperties.get("NewPatientWidth", String.valueOf(width)));
        height = Integer.parseInt(AppProperties.get("NewPatientHeight", String.valueOf(height)));
        x = Integer.parseInt(AppProperties.get("NewPatientX", String.valueOf(x)));
        y = Integer.parseInt(AppProperties.get("NewPatientY", String.valueOf(y)));

        setSize(width, height);
        setLocation(x, y);

    }

    private void saveWindowConfig() {
        int width = getWidth();
        int height = getHeight();
        int x = getLocation().x;
        int y = getLocation().y;
        AppProperties.put("NewPatientWidth", String.valueOf(width));
        AppProperties.put("NewPatientHeight", String.valueOf(height));
        AppProperties.put("NewPatientX", String.valueOf(x));
        AppProperties.put("NewPatientY", String.valueOf(y));
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void setStatus(String status) {
        jLabelStatus.setText(status);
    }

    private Patient getPatientFromFields() {
        Patient patient = new Patient();
        patient.outpatientNumber = jTextFieldOutpatientNumber.getText().trim();
        patient.lastName = jTextFieldLastName.getText().trim();
        patient.otherNames = jTextFieldOtherNames.getText().trim();
        patient.gender = jComboBoxGender.getSelectedIndex() < 0 ? null : jComboBoxGender.getSelectedIndex() == 0 ? "Male" : "Female";
        patient.dateOfBirth = jCheckBoxBirth.isSelected() && datePickerBirth.getDate() != null ? datePickerBirth.getDate()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        patient.address = jTextFieldAddress.getText().trim();
        patient.phoneNumber = jTextFieldPhone.getText().trim();
        patient.remarks = jTextAreaRemarks.getText().trim();
        patient.firstName = jTextFieldFirstName.getText().trim();
        return patient;
    }

    private void onSave() {
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
                    + "The patient could not be saved.", "Save Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // for now we will allow saving patient without providing any patient information: all fields are optional
        String sql = """
                     INSERT INTO `patient` (
                     firstName, lastName, otherNames, gender, dateOfBirth, 
                     phoneNumber, address, remarks, createdAt, outpatientNumber
                     )
                     VALUES(
                     ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """;
        boolean patientAdded = false;
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, jTextFieldFirstName.getText().trim());
            stmt.setString(2, jTextFieldLastName.getText().trim());
            stmt.setString(3, jTextFieldOtherNames.getText().trim());
            stmt.setString(4, jComboBoxGender.getSelectedIndex() < 0 ? null : jComboBoxGender.getSelectedIndex() == 0 ? "Male" : "Female");
            if (!jCheckBoxBirth.isSelected()) {
                stmt.setString(5, null);
            } else {
                stmt.setString(5, datePickerBirth.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ISO_DATE));
            }
            stmt.setString(6, jTextFieldPhone.getText().trim());
            stmt.setString(7, jTextFieldAddress.getText().trim());
            stmt.setString(8, jTextAreaRemarks.getText().trim());
            stmt.setString(9, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            stmt.setString(10, jTextFieldOutpatientNumber.getText().trim());
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    patientId = rs.getInt(1);
                }
                conn.commit();
                patientAdded = true;
            } else {
                conn.rollback();
                setStatus("An error occurred while saving the patient.");
            }
        } catch (Exception ex) {
            Logger.getLogger(NewPatient.class.getName()).log(Level.SEVERE, null, ex);
            setStatus("An error occurred while saving the patient.");
        } finally {
            Patient patient = getPatientFromFields();
            patient.id = patientId;
            PatientAddedEvent event = new PatientAddedEvent();
            event.patient = patient;
            PATIENT_LISTENER_MANAGER.notifyPatientAdded(event);

            JOptionPane.showMessageDialog(this, "Patient saved successfully.", "Patient Saved", JOptionPane.INFORMATION_MESSAGE);
            close();
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldFirstName = new javax.swing.JTextField();
        jTextFieldLastName = new javax.swing.JTextField();
        jTextFieldOtherNames = new javax.swing.JTextField();
        jComboBoxGender = new javax.swing.JComboBox<>();
        jTextFieldAddress = new javax.swing.JTextField();
        jTextFieldPhone = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaRemarks = new javax.swing.JTextArea();
        jButtonCancel = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jLabelStatus = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldOutpatientNumber = new javax.swing.JTextField();
        jCheckBoxBirth = new javax.swing.JCheckBox();
        datePickerBirth = new org.jdesktop.swingx.JXDatePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Patient");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("First name");

        jLabel2.setText("Last name");

        jLabel3.setText("Other names");

        jLabel4.setText("Gender");

        jLabel5.setText("Date of birth");

        jLabel6.setText("Address");

        jLabel7.setText("Phone number");

        jLabel8.setText("Remarks");

        jComboBoxGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        jTextAreaRemarks.setColumns(20);
        jTextAreaRemarks.setRows(5);
        jScrollPane1.setViewportView(jTextAreaRemarks);

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

        jLabelStatus.setText("jLabel9");

        jLabel9.setText("Outpatient number");

        jCheckBoxBirth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBirthActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonCancel))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                                            .addComponent(jCheckBoxBirth)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(datePickerBirth, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel9)
                                                .addComponent(jLabel1))
                                            .addGap(18, 18, 18)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jTextFieldOutpatientNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                                .addComponent(jComboBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jTextFieldFirstName)
                                                .addComponent(jTextFieldLastName)
                                                .addComponent(jTextFieldOtherNames)
                                                .addComponent(jTextFieldAddress)
                                                .addComponent(jTextFieldPhone)))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldOutpatientNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldOtherNames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jCheckBoxBirth)
                    .addComponent(datePickerBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelStatus, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonCancel)
                        .addComponent(jButtonSave)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        onSave();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jCheckBoxBirthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBirthActionPerformed
        datePickerBirth.setEnabled(jCheckBoxBirth.isSelected());
    }//GEN-LAST:event_jCheckBoxBirthActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        saveWindowConfig();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXDatePicker datePickerBirth;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxBirth;
    private javax.swing.JComboBox<String> jComboBoxGender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaRemarks;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldFirstName;
    private javax.swing.JTextField jTextFieldLastName;
    private javax.swing.JTextField jTextFieldOtherNames;
    private javax.swing.JTextField jTextFieldOutpatientNumber;
    private javax.swing.JTextField jTextFieldPhone;
    // End of variables declaration//GEN-END:variables
}

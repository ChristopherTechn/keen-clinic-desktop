package main.patient.visit.prescription;

import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import main.drug.Drug;
import main.drug.event.DrugAddedEvent;
import main.drug.event.DrugDeletedEvent;
import main.drug.event.DrugEventListener;
import main.drug.event.DrugUpdatedEvent;
import static main.drug.event.GlobalDrugEventManager.DRUG_EVENT_MANAGER;
import static main.patient.visit.prescription.GlobalPrescriptionEventManager.PRESCRIPTION_EVENT_MANAGER;
import models.ComboAutoComplete;

/**
 *
 * @author Mustafa
 */
public class NewPrescription extends javax.swing.JDialog implements DrugEventListener {

    private final int visitId;
    private final boolean saveToDb;
    private Prescription thePrescription;

    /**
     * Creates new form NewPrescription
     *
     * @param parent
     * @param modal
     * @param visitId
     * @param saveToDb
     */
    public NewPrescription(java.awt.Dialog parent, boolean modal, int visitId, boolean saveToDb) {
        super(parent, modal);
        initComponents();
        this.visitId = visitId;
        this.saveToDb = saveToDb;
        new ComboAutoComplete(jComboBoxDrug);
        getDrugs();
    }

    private int newOutpatientId;

    public void setNewOutpatientId(int id) {
        this.newOutpatientId = id;
    }

    private void transferDataFromWindow() {
        if (thePrescription == null) {
            thePrescription = new Prescription();
        }
        thePrescription.visitId = visitId;
        thePrescription.dosage = jTextFieldDosage.getText().trim();
        thePrescription.quantity = (int) jSpinnerQuantity.getValue();
        thePrescription.remarks = jTextAreaDescription.getText().trim();
        try {
            Drug d = Drug.getDrug(((JTextField) jComboBoxDrug.getEditor().getEditorComponent()).getText());
            thePrescription.drugId = d.id;
            thePrescription.drugName = d.name;
        } catch (SQLException ex) {
            Logger.getLogger(EditPrescription.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDrugs() {
        jComboBoxDrug.removeAllItems();
        try {
            List<Drug> drugs = Drug.getDrugs();
            for (Drug d : drugs) {
                jComboBoxDrug.addItem(d.name);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EditPrescription.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Prescription getPrescription() {
        return thePrescription;
    }

    private void onSave() {
        String drugName = ((JTextField) jComboBoxDrug.getEditor().getEditorComponent()).getText().trim();
        if (drugName.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a drug to prescribe", "Select Drug",
                    JOptionPane.WARNING_MESSAGE);
            jComboBoxDrug.requestFocusInWindow();
            return;
        }

        int quantity = (int) jSpinnerQuantity.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a quantity to prescribe", "Select Quantity",
                    JOptionPane.WARNING_MESSAGE);
            jSpinnerQuantity.requestFocusInWindow();
            return;
        }

        transferDataFromWindow();
        if (saveToDb) {
            try {
                thePrescription.save();
                JOptionPane.showMessageDialog(this,
                        "Prescription saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                close();
            } catch (SQLException ex) {
                Logger.getLogger(EditPrescription.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                        "An error occured while saving the prescription.", "Failed", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionTempAdded(new PrescriptionTempAddedEvent(newOutpatientId, thePrescription));
            close();
        }
    }

    private void onCancel() {
        close();
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void onDrugAdded(DrugAddedEvent event) {
        getDrugs();
        jComboBoxDrug.setSelectedIndex(-1);
    }

    @Override
    public void onDrugUpdated(DrugUpdatedEvent event) {
        getDrugs();
        jComboBoxDrug.setSelectedIndex(-1);
    }

    @Override
    public void onDrugDeleted(DrugDeletedEvent event) {
        getDrugs();
        jComboBoxDrug.setSelectedIndex(-1);
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
        jComboBoxDrug = new javax.swing.JComboBox<>();
        jSpinnerQuantity = new javax.swing.JSpinner();
        jTextFieldDosage = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();
        jButtonCancel = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Prescription");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Drug");

        jLabel2.setText("Quantity");

        jLabel3.setText("Dosage");

        jLabel4.setText("Remarks");

        jComboBoxDrug.setEditable(true);

        jSpinnerQuantity.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setRows(5);
        jScrollPane1.setViewportView(jTextAreaDescription);

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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldDosage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxDrug, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 133, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxDrug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinnerQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldDosage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        DRUG_EVENT_MANAGER.addListener(this);
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        DRUG_EVENT_MANAGER.removeListener(this);
    }//GEN-LAST:event_formWindowClosed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        onSave();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        onCancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox<String> jComboBoxDrug;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerQuantity;
    private javax.swing.JTextArea jTextAreaDescription;
    private javax.swing.JTextField jTextFieldDosage;
    // End of variables declaration//GEN-END:variables
}

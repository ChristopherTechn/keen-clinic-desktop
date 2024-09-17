package main.patient.visit;

import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import main.drug.event.DrugAddedEvent;
import main.drug.event.DrugDeletedEvent;
import main.drug.event.DrugEventListener;
import main.drug.event.DrugUpdatedEvent;
import main.patient.visit.prescription.PrescriptionAddedEvent;
import main.patient.visit.prescription.PrescriptionDeletedEvent;
import main.patient.visit.prescription.PrescriptionEventListener;
import main.patient.visit.prescription.PrescriptionTempAddedEvent;
import main.patient.visit.prescription.PrescriptionTempDeletedEvent;
import main.patient.visit.prescription.PrescriptionTempUpdatedEvent;
import main.patient.visit.prescription.PrescriptionUpdatedEvent;
import java.sql.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.drug.Drug;
import main.patient.visit.prescription.AllowNegativeShelfEvent;
import static main.patient.visit.prescription.GlobalPrescriptionEventManager.PRESCRIPTION_EVENT_MANAGER;

/**
 *
 * @author Mustafa
 */
public class DrugsOutOfShelf extends javax.swing.JDialog implements DrugEventListener, PrescriptionEventListener {

    private int newOutpatientId;
    private boolean canSafelyPrescribe;

    public boolean isCanSafelyPrescribe() {
        computeShelf();
        return canSafelyPrescribe;
    }

    public void setNewOutpatientId(int id) {
        newOutpatientId = id;
    }

    private final Map<String, Integer> prescriptionMap = new HashMap<>();

    /**
     * Creates new form DrugsOutOfShelf
     *
     * @param parent
     * @param modal
     */
    public DrugsOutOfShelf(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * Refresh the "drugs-out-of-shelf" list
     */
    private void computeShelf() {
        String text = "";

        for (Entry<String, Integer> entry : prescriptionMap.entrySet()) {
            try {
                int shelfQuantity = Drug.getDrug(entry.getKey()).shelfQuantity;
                if (shelfQuantity < entry.getValue()) {
                    text += String.format("%s. Requested: %d, %d on shelf",
                            entry.getKey(), entry.getValue(), shelfQuantity);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DrugsOutOfShelf.class.getName()).log(Level.SEVERE, null, ex);
                text += "Error\n";
            }
        }
        jTextAreaQuantities.setText(text);
        canSafelyPrescribe = text.length() == 0;
        if (canSafelyPrescribe) {
            close();
        }
    }

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void onDrugAdded(DrugAddedEvent event) {
        computeShelf();
    }

    @Override
    public void onDrugUpdated(DrugUpdatedEvent event) {
        computeShelf();
    }

    @Override
    public void onDrugDeleted(DrugDeletedEvent event) {
        computeShelf();
    }

    @Override
    public void onPrescriptionAdded(PrescriptionAddedEvent event) {
        computeShelf();
    }

    @Override
    public void onPrescriptionUpdated(PrescriptionUpdatedEvent event) {
        computeShelf();
    }

    @Override
    public void onPrescriptionDeleted(PrescriptionDeletedEvent event) {
        computeShelf();
    }

    @Override
    public void onPrescriptionTempAdded(PrescriptionTempAddedEvent event) {
        if (event.eventId == newOutpatientId) {
            prescriptionMap.put(event.prescription.drugName,
                    prescriptionMap.getOrDefault(event.prescription.drugName, 0) + event.prescription.quantity);
            computeShelf();
        }
    }

    @Override
    public void onPrescriptionTempUpdated(PrescriptionTempUpdatedEvent event) {
        if (event.eventId == newOutpatientId) {
            String drugName = event.prescription.drugName;
            int diff = event.prescription.quantity - event.oldPrescription.quantity;
            int current = prescriptionMap.getOrDefault(drugName, 0);
            prescriptionMap.put(event.prescription.drugName,
                    current + diff);
            computeShelf();
        }
    }

    @Override
    public void onPrescriptionTempDeleted(PrescriptionTempDeletedEvent event) {
        if (event.eventId == newOutpatientId) {
            String drugName = event.prescription.drugName;
            int currentQuantity = prescriptionMap.getOrDefault(drugName, 0);

            prescriptionMap.put(drugName, currentQuantity - event.prescription.quantity);

            computeShelf();
        }
    }

    @Override
    public void onAllowNegativeShelf(AllowNegativeShelfEvent event) {
        if (event.eventId == newOutpatientId) {
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
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaQuantities = new javax.swing.JTextArea();

        setTitle("Drugs");

        jLabel1.setText("The following drugs are not enough on shelf.");

        jButton1.setText("Go back to prescriptions");

        jLabel2.setText("You can update the shelf or the prescription without closing this window");

        jButton2.setText("Allow negative shelf quantities");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Click 'Allow negative shelf quantities' to continue prescribing");

        jTextAreaQuantities.setEditable(false);
        jTextAreaQuantities.setColumns(20);
        jTextAreaQuantities.setLineWrap(true);
        jTextAreaQuantities.setRows(5);
        jTextAreaQuantities.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextAreaQuantities);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        onAllowNegativeShelf();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void onAllowNegativeShelf() {
        PRESCRIPTION_EVENT_MANAGER.notifyAllowNegativeShelf(new AllowNegativeShelfEvent(newOutpatientId));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaQuantities;
    // End of variables declaration//GEN-END:variables

}

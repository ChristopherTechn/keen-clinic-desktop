package main.patient.visit;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import settings.Settings;
import main.patient.patient.Patient;

/**
 *
 * @author Mustafa Mohamed
 */
public class NewVisitSelectPatient extends javax.swing.JDialog {

    private final NewVisitSelectPatientTableModel tableModel = new NewVisitSelectPatientTableModel();
    private int patientId;

    public int getPatientId() {
        return patientId;
    }

    private void setTableHeight() {
        int fontSize = 12;
        try {
            fontSize = Settings.getFontSize();
        } catch (SQLException ex) {
            Logger.getLogger(NewVisitSelectPatient.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTablePatients.setRowHeight(fontSize * 3);
    }

    /**
     * Creates new form NewVisitSelectPatient
     *
     * @param parent
     * @param modal
     */
    public NewVisitSelectPatient(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTablePatients.setModel(tableModel);
        setTableHeight();
        DocumentListener l = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearchChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearchChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearchChanged();
            }
        };
        jTextFieldSearch.getDocument().addDocumentListener(l);
        getPatients();
        jTablePatients.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {
                    onUseSelected();
                }
            }
        });
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            // Check if user has done dragging the scroll bar
            if (!e.getValueIsAdjusting()) {
                JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
                int extent = scrollBar.getModel().getExtent();
                int maximum = scrollBar.getModel().getMaximum();
                if (extent + e.getValue() == maximum) {
                    offset += resultsPerPage;
                    getPatients();
                }
            }
        });
        jTextFieldSearch.requestFocusInWindow();
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
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jTextFieldSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePatients = new javax.swing.JTable();
        jButtonCancel = new javax.swing.JButton();
        jButtonUseSelected = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Visit - Select Patient");

        jLabel1.setText("Select a patient for the visit or");

        jXHyperlink1.setText("add a patient");

        jTextFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchActionPerformed(evt);
            }
        });

        jTablePatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Patient Name", "Gender", "Age", "Date of Birth", "Phone Number", "Address", "Remarks"
            }
        ));
        jTablePatients.setRowHeight(30);
        jTablePatients.setShowHorizontalLines(true);
        jTablePatients.setShowVerticalLines(true);
        jScrollPane1.setViewportView(jTablePatients);
        jTablePatients.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonUseSelected.setText("Use Selected Patient");
        jButtonUseSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUseSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jXHyperlink1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSearch))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonUseSelected)
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
                    .addComponent(jXHyperlink1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonUseSelected))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        patientId = 0;
        close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void jButtonUseSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUseSelectedActionPerformed
        onUseSelected();
    }//GEN-LAST:event_jButtonUseSelectedActionPerformed

    private void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void onSearchChanged() {
        offset = 0;
        tableModel.clearRowItems();
        getPatients();
    }

    private final String orderColumn = "firstName";
    private final int resultsPerPage = 100;
    private int offset = 0;

    private void getPatients() {
        try {
            List<Patient> patients = Patient.getPatients(jTextFieldSearch.getText().trim(), resultsPerPage, orderColumn, offset);
            tableModel.addRowItems(patients);
        } catch (SQLException ex) {
            Logger.getLogger(NewVisitSelectPatient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void onUseSelected() {
        if (jTablePatients.getSelectedRowCount() == 1) {
            int index = jTablePatients.convertRowIndexToModel(jTablePatients.getSelectedRow());
            Patient p = tableModel.getRowItem(index);
            patientId = p.id;
            close();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonUseSelected;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTablePatients;
    private javax.swing.JTextField jTextFieldSearch;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    // End of variables declaration//GEN-END:variables
}

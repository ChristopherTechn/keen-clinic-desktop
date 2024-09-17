package main.patient;

import activation.Activate;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import main.HomePatientModel;
import java.util.logging.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import main.DoubleClickPatient;
import main.Home;
import static main.patient.event.GlobalEventManager.PATIENT_LISTENER_MANAGER;
import settings.Settings;
import main.patient.event.PatientAddedEvent;
import main.patient.event.PatientDeletedEvent;
import main.patient.event.PatientListener;
import main.patient.event.PatientUpdatedEvent;
import main.patient.event.VisitAddedEvent;
import main.patient.event.VisitDeletedEvent;
import main.patient.event.VisitListener;
import main.patient.event.VisitUpdatedEvent;
import main.patient.patient.EditPatient;
import main.patient.patient.NewPatient;
import main.patient.patient.Patient;
import main.patient.visit.NewOutpatient;
import main.patient.visit.Outpatient;
import main.patient.visit.PatientVisitHistory;
import static org.kordamp.ikonli.carbonicons.CarbonIcons.RENEW;
import org.kordamp.ikonli.swing.FontIcon;
import settings.DatabaseRestoredEvent;
import settings.SettingsEventListener;
import utils.Utils;

/**
 *
 * @author Mustafa
 */
public class PatientHome extends javax.swing.JPanel implements PatientListener, VisitListener, SettingsEventListener {

    private final HomePatientModel patientModel = new HomePatientModel();

    /**
     * Creates new form PatientHome
     */
    public PatientHome() {
        initComponents();
        init();
    }

    private void init() {

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
        getPatients();
        DocumentListener l = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextChanged();
            }
        };
        jTextFieldSearch.getDocument().addDocumentListener(l);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        jTablePatients.getColumnModel().getColumn(0).setCellRenderer(render);
        DefaultTableCellRenderer renderAge = new DefaultTableCellRenderer();
        renderAge.setHorizontalAlignment(SwingConstants.CENTER);
        jTablePatients.getColumnModel().getColumn(4).setCellRenderer(renderAge);

        jTablePatients.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (row != -1 && mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    var doubleClick = Settings.getDoubleClickPatient();
                    if (doubleClick == DoubleClickPatient.EDIT_PATIENT) {
                        onEditPatient();
                    } else if (doubleClick == DoubleClickPatient.VISIT_HISTORY) {
                        onVisitHistory();
                    }

                }
            }
        });
        int fontSize = 12;
        jTablePatients.setRowHeight((int) (fontSize * 3));
        jTextFieldSearch.putClientProperty("JTextField.placeholderText", "Search patients");

        jTablePatients.setModel(patientModel);

        Color color = Color.BLACK;
        try {
            color = Settings.getIconsColor();
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
        jButtonRefresh.setText("");
        jButtonRefresh.setIcon(FontIcon.of(RENEW, 20, color));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenuPatient = new javax.swing.JPopupMenu();
        jMenuItemAddPatient = new javax.swing.JMenuItem();
        jMenuItemAddOutpatient = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemEditPatient = new javax.swing.JMenuItem();
        jMenuItemVisitHistory = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemDeletePatient = new javax.swing.JMenuItem();
        jTextFieldSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePatients = new javax.swing.JTable();
        jButtonRefresh = new javax.swing.JButton();
        jXLabelCount = new org.jdesktop.swingx.JXLabel();

        jPopupMenuPatient.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                jPopupMenuPatientPopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItemAddPatient.setText("Add Patient...");
        jMenuItemAddPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddPatientActionPerformed(evt);
            }
        });
        jPopupMenuPatient.add(jMenuItemAddPatient);

        jMenuItemAddOutpatient.setText("Add Outpatient Visit...");
        jMenuItemAddOutpatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddOutpatientActionPerformed(evt);
            }
        });
        jPopupMenuPatient.add(jMenuItemAddOutpatient);
        jPopupMenuPatient.add(jSeparator2);

        jMenuItemEditPatient.setText("Edit Patient...");
        jMenuItemEditPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditPatientActionPerformed(evt);
            }
        });
        jPopupMenuPatient.add(jMenuItemEditPatient);

        jMenuItemVisitHistory.setText("Patient Visit History");
        jMenuItemVisitHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemVisitHistoryActionPerformed(evt);
            }
        });
        jPopupMenuPatient.add(jMenuItemVisitHistory);
        jPopupMenuPatient.add(jSeparator3);

        jMenuItemDeletePatient.setText("Delete Selected Patients...");
        jMenuItemDeletePatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeletePatientActionPerformed(evt);
            }
        });
        jPopupMenuPatient.add(jMenuItemDeletePatient);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jTablePatients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "#", "Patient Name", "Gender", "Date of Birth", "Age", "Phone Number", "Address", "Last Visit Date", "Number of Visits"
            }
        ));
        jTablePatients.setComponentPopupMenu(jPopupMenuPatient);
        jTablePatients.setRowHeight(30);
        jTablePatients.setShowHorizontalLines(true);
        jTablePatients.setShowVerticalLines(true);
        jScrollPane1.setViewportView(jTablePatients);

        jButtonRefresh.setText("jButton1");
        jButtonRefresh.setToolTipText("Refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jXLabelCount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jXLabelCount.setText("Showing - of - patients");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jXLabelCount, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonRefresh))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jXLabelCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        refresh();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jMenuItemAddPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddPatientActionPerformed
        onAddPatient();
    }//GEN-LAST:event_jMenuItemAddPatientActionPerformed

    private void jMenuItemAddOutpatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddOutpatientActionPerformed
        onAddOutpatient();
    }//GEN-LAST:event_jMenuItemAddOutpatientActionPerformed

    private void jMenuItemEditPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditPatientActionPerformed
        onEditPatient();
    }//GEN-LAST:event_jMenuItemEditPatientActionPerformed

    private void jMenuItemVisitHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVisitHistoryActionPerformed
        onVisitHistory();
    }//GEN-LAST:event_jMenuItemVisitHistoryActionPerformed

    private void jMenuItemDeletePatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeletePatientActionPerformed
        onDeletePatients();
    }//GEN-LAST:event_jMenuItemDeletePatientActionPerformed

    private void jPopupMenuPatientPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_jPopupMenuPatientPopupMenuWillBecomeVisible
        int selected = jTablePatients.getSelectedRowCount();
        jMenuItemEditPatient.setEnabled(selected == 1);
        jMenuItemVisitHistory.setEnabled(selected == 1);
        jMenuItemDeletePatient.setEnabled(selected > 0);
    }//GEN-LAST:event_jPopupMenuPatientPopupMenuWillBecomeVisible

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

    }//GEN-LAST:event_formComponentHidden

    private void onVisitHistory() {
        if (jTablePatients.getSelectedRowCount() != 1) {
            return;
        }
        Patient p = patientModel.getRowItem(
                jTablePatients.convertColumnIndexToModel(jTablePatients.getSelectedRow()));
        PatientVisitHistory history = new PatientVisitHistory(Home.home, false, p.id);
        history.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(history);
        history.initWindow();
        history.setVisible(true);
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
        NewPatient dialog = new NewPatient(Home.home, false);
        dialog.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(dialog);
        dialog.initWindow();
        dialog.setVisible(true);
        if (dialog.getPatientId() > 0) {
        }
    }

    private void onAddOutpatient() {

        NewOutpatient dialog = new NewOutpatient(Home.home, false);
        dialog.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(dialog);
        dialog.initWindow();
        dialog.setVisible(true);

    }

    private void onTextChanged() {
        refresh();
    }

    private void onEditPatient() {
        if (jTablePatients.getSelectedRowCount() != 1) {
            return;
        }
        Patient h = patientModel.getRowItem(jTablePatients.convertRowIndexToModel(jTablePatients.getSelectedRow()));
        EditPatient edit = new EditPatient(Home.home, false, h.id);
        edit.setLocationRelativeTo(this);
        Utils.closeDialogOnEscape(edit);
        edit.initWindow();
        edit.setVisible(true);

    }

    private void refresh() {
        patientModel.clearRowItems();
        offset = 0;
        getPatients();
    }

    private final int resultsPerPage = 100;
    private int offset = 0;
    private final String orderColumn = "firstName";

    private void getPatients() {
        try {
            List<Patient> patients = Patient.getPatients(jTextFieldSearch.getText().trim(), resultsPerPage, orderColumn, offset);
            int totalCount = Patient.countPatients();

            patientModel.addRowItems(patients);
            int visibleCount = patientModel.size();
            NumberFormat format = NumberFormat.getNumberInstance();
            String text = String.format("Showing %s of %s patients",
                    format.format(visibleCount), format.format(totalCount));
            jXLabelCount.setText(text);
        } catch (SQLException ex) {
            Logger.getLogger(PatientHome.class.getName()).log(Level.SEVERE, null, ex);
            jXLabelCount.setText("Showing - of - patients");
        }
    }

    private void onDeletePatients() {
        if (jTablePatients.getSelectedRowCount() > 0) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected patients?",
                    "Delete Patients", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                List<Patient> patients = new ArrayList<>();
                for (int i : jTablePatients.getSelectedRows()) {
                    patients.add(patientModel.getRowItem(jTablePatients.convertRowIndexToModel(i)));
                }
                for (Patient p : patients) {
                    try {
                        Patient.deletePatient(p.id);
                        patientModel.removeRowItem(p);
                        PatientDeletedEvent event = new PatientDeletedEvent();
                        event.patient = p;
                        PATIENT_LISTENER_MANAGER.notifyPatientDeleted(event);
                    } catch (SQLException ex) {
                        Logger.getLogger(PatientHome.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public void onPatientAdded(PatientAddedEvent event) {
        if (event.patient != null) {
            Patient patient = event.patient;
            patientModel.addRowItem(patient);
            int index = patientModel.getIndex(patient);
            index = jTablePatients.convertRowIndexToView(index);
            if (index >= 0) {
                jTablePatients.scrollRectToVisible(jTablePatients.getCellRect(index, 0, true));
                jTablePatients.getSelectionModel().setSelectionInterval(index, index);
            }
        }
    }

    @Override
    public void onPatientDeleted(PatientDeletedEvent event) {
        Patient patient = event.patient;
        if (patient != null) {
            // it is easier to getPatients() than to remove the deleted patients
            getPatients();
        }
    }

    @Override
    public void onPatientUpdated(PatientUpdatedEvent event) {
        Patient oldPatient = event.oldPatient;
        Patient newPatient = event.newPatient;
        for (int i = 0; i < patientModel.size(); i++) {
            Patient p = patientModel.getRowItem(i);
            if (p.id == oldPatient.id) {
                p.address = newPatient.address;
                p.createdAt = newPatient.createdAt;
                p.dateOfBirth = newPatient.dateOfBirth;
                p.firstName = newPatient.firstName;
                p.gender = newPatient.gender;
                p.lastName = newPatient.lastName;
                p.otherNames = newPatient.otherNames;
                p.outpatientNumber = newPatient.outpatientNumber;
                p.phoneNumber = newPatient.phoneNumber;
                p.remarks = newPatient.remarks;

                int index = jTablePatients.convertColumnIndexToView(i);
                patientModel.fireTableRowsUpdated(index, index);
            }

        }
    }

    @Override
    public void onVisitAdded(VisitAddedEvent event) {
        Outpatient visit = event.visit;
        int patientId = visit.patientId;
        for (int i = 0; i < patientModel.size(); i++) {
            Patient patient = patientModel.getRowItem(i);
            if (patient.id == patientId) {
                if (patient.lastVisitDate == null) {
                    patient.lastVisitDate = visit.visitDate;
                } else {
                    if (patient.lastVisitDate.isBefore(visit.visitDate)) {
                        patient.lastVisitDate = visit.visitDate;
                    }
                }
                patient.visitCount += 1;
                int index = jTablePatients.convertRowIndexToView(i);
                patientModel.fireTableRowsUpdated(index, index);
                break;
            }
        }
    }

    @Override
    public void onVisitUpdated(VisitUpdatedEvent event) {

    }

    @Override
    public void onVisitDeleted(VisitDeletedEvent event) {
        Outpatient visit = event.visit;
        int patientId = visit.patientId;
        for (int i = 0; i < patientModel.size(); i++) {
            Patient p = patientModel.getRowItem(i);
            if (p.id == patientId) {
                int index = jTablePatients.convertRowIndexToView(i);
                try {
                    Patient updated = Patient.getPatient(patientId);
                    p.lastVisitDate = updated.lastVisitDate;
                    p.visitCount = updated.visitCount;
                    patientModel.fireTableRowsUpdated(index, index);
                } catch (SQLException ex) {
                    Logger.getLogger(PatientHome.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void onDatabaseRestored(DatabaseRestoredEvent event) {
        refresh();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JMenuItem jMenuItemAddOutpatient;
    private javax.swing.JMenuItem jMenuItemAddPatient;
    private javax.swing.JMenuItem jMenuItemDeletePatient;
    private javax.swing.JMenuItem jMenuItemEditPatient;
    private javax.swing.JMenuItem jMenuItemVisitHistory;
    private javax.swing.JPopupMenu jPopupMenuPatient;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTable jTablePatients;
    private javax.swing.JTextField jTextFieldSearch;
    private org.jdesktop.swingx.JXLabel jXLabelCount;
    // End of variables declaration//GEN-END:variables
}

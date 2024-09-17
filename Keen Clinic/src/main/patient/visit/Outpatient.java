package main.patient.visit;

import auth.Login;
import database.Database;
import java.time.LocalDateTime;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.drug.Drug;
import main.drug.event.DrugUpdatedEvent;
import static main.drug.event.GlobalDrugEventManager.DRUG_EVENT_MANAGER;
import static main.notification.GlobalNotificationEventManager.NOTIFICATION_EVENT_MANAGER;
import main.notification.Notification;
import main.notification.NotificationAddedEvent;
import static main.patient.event.GlobalEventManager.VISIT_LISTENER_MANAGER;
import main.patient.event.VisitAddedEvent;
import main.patient.event.VisitUpdatedEvent;
import static main.patient.visit.prescription.GlobalPrescriptionEventManager.PRESCRIPTION_EVENT_MANAGER;
import main.patient.visit.prescription.Prescription;
import main.patient.visit.prescription.PrescriptionAddedEvent;
import main.patient.visit.prescription.PrescriptionDeletedEvent;
import main.patient.visit.prescription.PrescriptionUpdatedEvent;

/**
 *
 * @author Mustafa Mohamed
 */
public class Outpatient {

    public int id;
    public int patientId;
    public String opdNumber;
    public LocalDateTime visitDate;
    public float weight;
    public float height;
    public String visualAcuity;
    public String complaints;
    public String physicalExam;
    public String labExam;
    public String outcome;
    public String diagnosis;
    public String treatment;
    public String remarks;
    public final List<Prescription> prescriptions = new ArrayList<>();

    public static List<Prescription> getPrescriptions(int visitId) throws SQLException {
        List<Prescription> p = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE outpatientId = ? AND deletedAt IS NULL";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Prescription pres = Prescription.extractPrescription(rs);
                pres.drugName = Drug.getDrug(pres.drugId, conn).name;
                p.add(pres);
            }
        }
        return p;
    }

    public void addPrescription(Prescription p) {
        prescriptions.add(p);
    }

    public static Outpatient getOutpatient(int visitId) throws SQLException {
        String sql = "SELECT * FROM outpatient WHERE id = ? ";
        Outpatient outpatient = null;
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                outpatient = extractOutpatient(rs);
            }
        }
        // this bit for getting the prescriptions needs to be outside the 
        // try-with-resources because getPrescriptions(int visitId) makes 
        // a second connection to the same database
        if (outpatient != null) {
            for (Prescription p : getPrescriptions(outpatient.id)) {
                outpatient.addPrescription(p);
            }
        }
        return outpatient;
    }

    private static Outpatient extractOutpatient(ResultSet rs) throws SQLException {
        Outpatient p = new Outpatient();
        p.id = rs.getInt("id");
        p.patientId = rs.getString("patientId") == null ? 0 : rs.getInt("patientId");
        p.opdNumber = rs.getString("opdNumber");
        p.visitDate = rs.getString("visitDate") == null ? null : LocalDateTime.parse(rs.getString("visitDate"), DateTimeFormatter.ISO_DATE_TIME);
        p.weight = rs.getString("weight") == null ? 0 : rs.getFloat("weight");
        p.height = rs.getString("height") == null ? 0 : rs.getFloat("height");
        p.visualAcuity = rs.getString("visualAcuity");
        p.complaints = rs.getString("complaints");
        p.physicalExam = rs.getString("physicalExam");
        p.labExam = rs.getString("labExam");
        p.outcome = rs.getString("outcome");
        p.diagnosis = rs.getString("diagnosis");
        p.treatment = rs.getString("treatment");
        p.remarks = rs.getString("remarks");
        return p;
    }

    public static List<Outpatient> getPatientVisits(int patientId) throws SQLException {
        List<Outpatient> visits = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM outpatient WHERE patientId = ? "
                    + "ORDER BY datetime(visitDate) DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                visits.add(extractOutpatient(rs));
            }
        }
        // expensive logic. Please improve!
        visits.forEach(visit -> {
            try {
                for (Prescription p : getPrescriptions(visit.id)) {
                    visit.addPrescription(p);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Outpatient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return visits;
    }

    public static boolean deleteOutpatient(int id) throws SQLException {
        // the outpatient table is related to the prescription table but fortunately
        // delete was set to cascade delete
        String sql = "DELETE FROM outpatient WHERE id = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 1) {
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        }
    }

    // we want to fire events for updated outpatient and prescriptions
    private static final List<Prescription> updatedPrescriptions = new ArrayList<>();
    private static final List<Prescription> insertedPrescriptions = new ArrayList<>();
    private static final List<Prescription> deletedPrescriptions = new ArrayList<>();
    private static final List<Drug> updatedDrugs = new ArrayList<>();

    public static boolean updateOutpatient(Outpatient visit) throws SQLException {
        updatedPrescriptions.clear();
        insertedPrescriptions.clear();
        deletedPrescriptions.clear();
        String sql = "UPDATE outpatient SET "
                + "patientId = ?,"
                + "opdNumber = ?, "
                + "visitDate = ?, "
                + "weight = ?,"
                + "height = ?,"
                + "visualAcuity = ?,"
                + "complaints = ?,"
                + "physicalExam = ?,"
                + "labExam = ?,"
                + "outcome = ?,"
                + "diagnosis = ?,"
                + "treatment = ?,"
                + "remarks = ? "
                + "WHERE id = ? ";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (visit.patientId <= 0) {
                stmt.setObject(1, null);
            } else {
                stmt.setInt(1, visit.patientId);
            }
            stmt.setString(2, visit.opdNumber);
            stmt.setString(3, visit.visitDate == null ? null : visit.visitDate.format(DateTimeFormatter.ISO_DATE_TIME));
            stmt.setFloat(4, visit.weight);
            stmt.setFloat(5, visit.height);
            stmt.setString(6, visit.visualAcuity);
            stmt.setString(7, visit.complaints);
            stmt.setString(8, visit.physicalExam);
            stmt.setString(9, visit.labExam);
            stmt.setString(10, visit.outcome);
            stmt.setString(11, visit.diagnosis);
            stmt.setString(12, visit.treatment);
            stmt.setString(13, visit.remarks);
            stmt.setInt(14, visit.id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                // we want to update the prescriptions too
                // updating the drugs on shelf is way too complex.
                // For instance if a prescription had 12 pills of X but was updated
                // to have 16 pills of x, it is way too much logic to determine
                // that the prescription involved adding 4 pills of X. For that
                // reason, updating a prescription will not touch the shelf.
                updatePrescriptions(visit, conn);

                conn.commit();
                firePrescriptionEvents();
                fireOutpatientEvent(visit);
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } finally {
            updatedPrescriptions.clear();
            insertedPrescriptions.clear();
            deletedPrescriptions.clear();
        }
    }

    private static void fireOutpatientEvent(Outpatient visit) {
        VisitUpdatedEvent event = new VisitUpdatedEvent();
        event.newVisit = visit;
        VISIT_LISTENER_MANAGER.notifyVisitUpdated(event);
    }

    private static void firePrescriptionEvents() {
        for (Prescription p : insertedPrescriptions) {
            PrescriptionAddedEvent event = new PrescriptionAddedEvent(p);
            PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionAdded(event);
        }
        for (Prescription p : updatedPrescriptions) {
            PrescriptionUpdatedEvent event = new PrescriptionUpdatedEvent(p);
            PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionUpdated(event);
        }
        for (Prescription p : deletedPrescriptions) {
            PrescriptionDeletedEvent event = new PrescriptionDeletedEvent(p);
            PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionDeleted(event);
        }
    }

    private static void fireDrugEvents() {
        for (Drug drug : updatedDrugs) {
            DrugUpdatedEvent event = new DrugUpdatedEvent(drug);
            DRUG_EVENT_MANAGER.notifyDrugUpdated(event);
        }
    }

    /**
     * Update the prescription for a visit. This method should not close, commit
     * or rollback the connection passed to it
     *
     * @param visit the visit
     * @param conn a connection to the database
     * @throws SQLException
     */
    private static void updatePrescriptions(Outpatient visit, Connection conn) throws SQLException {
        String insert = "INSERT INTO prescription (outpatientId, drugId, quantity,"
                + "dosage, remarks) VALUES(?, ?, ?, ?, ?)";
        String update = "UPDATE prescription SET outpatientId = ?, drugId = ?, "
                + "quantity = ?, dosage = ?, remarks = ? WHERE id = ?";
        PreparedStatement insertStmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement updateStmt = conn.prepareStatement(update);
        for (Prescription p : visit.prescriptions) {
            // update if existing, otherwise insert
            updateStmt.setInt(1, visit.id);
            updateStmt.setInt(2, p.drugId);
            updateStmt.setInt(3, p.quantity);
            updateStmt.setString(4, p.dosage);
            updateStmt.setString(5, p.remarks);
            updateStmt.setInt(6, p.id);
            int updated = updateStmt.executeUpdate();
            if (updated != 1) {
                insertStmt.setInt(1, visit.id);
                insertStmt.setInt(2, p.drugId);
                insertStmt.setInt(3, p.quantity);
                insertStmt.setString(4, p.dosage);
                insertStmt.setString(5, p.remarks);
                updated = insertStmt.executeUpdate();
                if (updated == 1) {
                    ResultSet rs = insertStmt.getGeneratedKeys();
                    if (rs.next()) {
                        p.id = rs.getInt(1);
                        p.visitId = visit.id;
                        insertedPrescriptions.add(p);
                    } else {
                        throw new SQLException("Could not get prescription id for inserted visit");
                    }
                } else {
                    throw new SQLException("");
                }
            } else {
                updatedPrescriptions.add(p);
            }
        }
        // delete any not in the list but exist in the database
        String delete = "UPDATE prescription SET deletedAt = ? WHERE id = ?";
        String select = "SELECT * FROM prescription WHERE "
                + " deletedAt IS NULL AND outpatientId = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(delete);
        PreparedStatement selectStmt = conn.prepareStatement(select);

        List<Prescription> toDelete = new ArrayList<>();

        selectStmt.setInt(1, visit.id);
        ResultSet prescriptionsRs = selectStmt.executeQuery();
        while (prescriptionsRs.next()) {
            Prescription p = Prescription.extractPrescription(prescriptionsRs);
            // if it does not exist in the visit's prescription list, delete it
            boolean exists = false;
            for (Prescription pr : visit.prescriptions) {
                if (p.id == pr.id) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                toDelete.add(p);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (Prescription p : toDelete) {
            deleteStmt.setString(1, now.format(DateTimeFormatter.ISO_DATE_TIME));
            deleteStmt.setInt(2, p.id);
            int updated = deleteStmt.executeUpdate();
            if (updated == 1) {
                deletedPrescriptions.add(p);
            } else {
                throw new SQLException("Could not delete prescription with id " + p.id);
            }
        }
    }

    /**
     * Insert an outpatient visit record.
     *
     * @param visit the visit
     * @return the id of the inserted outpatient visit
     * @throws SQLException
     */
    public static int saveOutpatient(Outpatient visit) throws SQLException {
        String sql = "INSERT INTO outpatient ("
                + "patientId, opdNumber, visitDate, weight, height, visualAcuity, "
                + "complaints, physicalExam, labExam, outcome, diagnosis, treatment, remarks)"
                + "VALUES("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
                + ")";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (visit.patientId <= 0) {
                stmt.setObject(1, null);
            } else {
                stmt.setInt(1, visit.patientId);
            }
            stmt.setString(2, visit.opdNumber);
            stmt.setString(3, visit.visitDate == null ? null : visit.visitDate.format(DateTimeFormatter.ISO_DATE_TIME));
            stmt.setFloat(4, visit.weight);
            stmt.setFloat(5, visit.height);
            stmt.setString(6, visit.visualAcuity);
            stmt.setString(7, visit.complaints);
            stmt.setString(8, visit.physicalExam);
            stmt.setString(9, visit.labExam);
            stmt.setString(10, visit.outcome);
            stmt.setString(11, visit.diagnosis);
            stmt.setString(12, visit.treatment);
            stmt.setString(13, visit.remarks);
            int updated = stmt.executeUpdate();
            if (updated == 1) {

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    visit.id = rs.getInt(1);
                    insertPrescriptions(visit, conn);

                    String notificationMessage = "The following drugs were removed from the shelf.\n";
                    List<String> drugNames = new ArrayList<>();
                    for (var p : insertedPrescriptions) {
                        drugNames.add(p.drugName + "(" + p.quantity + " units)");
                    }

                    notificationMessage += String.join(", ", drugNames);

                    String query = "INSERT INTO notifications (title, body, userId, createdAt) VALUES(?, ?, ?, ?)";
                    PreparedStatement drugsStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    drugsStmt.setString(1, "Drug-shelf updated");
                    drugsStmt.setString(2, notificationMessage);
                    drugsStmt.setInt(3, Login.LOGGED_IN_USER_ID);
                    drugsStmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                    drugsStmt.executeUpdate();

                    ResultSet drugsRs = drugsStmt.getGeneratedKeys();

                    Notification n = new Notification();
                    if (drugsRs.next()) {
                        n.id = drugsRs.getInt(1);
                    }
                    n.title = "Drug-shelf updated";
                    n.body = notificationMessage;
                    n.createdAt = LocalDateTime.now();

                    conn.commit();

                    NotificationAddedEvent notifAddedEvent = new NotificationAddedEvent();
                    notifAddedEvent.notification = n;
                    NOTIFICATION_EVENT_MANAGER.notifyNotificationAdded(notifAddedEvent);

                    VisitAddedEvent event = new VisitAddedEvent();
                    event.visit = visit;
                    VISIT_LISTENER_MANAGER.notifyVisitAdded(event);

                    for (Prescription p : insertedPrescriptions) {
                        PrescriptionAddedEvent e = new PrescriptionAddedEvent(p);
                        PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionAdded(e);
                        drugNames.add(e.prescription.drugName);
                    }

                    fireDrugEvents();
                    return visit.id;
                }
            }
            conn.rollback();
            return -1;
        } finally {
            insertedPrescriptions.clear();
            updatedDrugs.clear();
        }
    }

    /**
     * Insert a prescription for a visit. This method does not commit or
     * rollback or close the connection. It is up to the caller to do that.
     *
     * @param visit the visit
     * @param conn a connection to the database
     * @throws SQLException
     */
    private static void insertPrescriptions(Outpatient visit, Connection conn) throws SQLException {
        String insert = "INSERT INTO prescription (outpatientId, drugId, quantity,"
                + "dosage, remarks) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);

        String updateDrug = "UPDATE drug SET shelfQuantity = ? WHERE id = ?";
        PreparedStatement drugStmt = conn.prepareStatement(updateDrug);
        for (Prescription p : visit.prescriptions) {

            insertStmt.setInt(1, visit.id);
            insertStmt.setInt(2, p.drugId);
            insertStmt.setInt(3, p.quantity);
            insertStmt.setString(4, p.dosage);
            insertStmt.setString(5, p.remarks);
            int updated = insertStmt.executeUpdate();
            if (updated == 1) {
                ResultSet rs = insertStmt.getGeneratedKeys();
                if (rs.next()) {
                    p.id = rs.getInt(1);
                    p.visitId = visit.id;
                    insertedPrescriptions.add(p);
                    // update the drug quantities
                    Drug drug = Drug.getDrug(p.drugId, conn);
                    drugStmt.setInt(1, drug.shelfQuantity - p.quantity);
                    drugStmt.setInt(2, drug.id);
                    updated = drugStmt.executeUpdate();
                    if (updated == 1) {
                        drug.shelfQuantity -= p.quantity;
                        updatedDrugs.add(drug);
                    } else {
                        throw new SQLException("Could not update drug quantity for drug " + drug.id);
                    }

                } else {
                    throw new SQLException("Could not get prescription id for inserted visit");
                }
            } else {
                throw new SQLException("Could not save prescription with id " + p.id);
            }
        }
    }
}

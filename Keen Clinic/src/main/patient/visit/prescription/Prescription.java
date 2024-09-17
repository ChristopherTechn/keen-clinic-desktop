package main.patient.visit.prescription;

import database.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static main.patient.visit.prescription.GlobalPrescriptionEventManager.PRESCRIPTION_EVENT_MANAGER;
import utils.Utils;

/**
 *
 * @author Mustafa
 */
public class Prescription {

    public int id;
    public int drugId;
    public String drugName;
    public int quantity;
    public String dosage;
    public String remarks;
    public int visitId;
    /**
     * A temporary id used to identify this object before it is saved to the
     * database.
     */
    private final int tempId;

    public Prescription() {

        tempId = Utils.getUniqueId();
    }

    public int getTempId() {
        return tempId;
    }

    public String format() {
        String text = String.format("%d of %s - %s", quantity, drugName, dosage);
        return text;
    }

    public static Prescription extractPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.dosage = rs.getString("dosage");
        p.drugId = rs.getInt("drugId");
        p.id = rs.getInt("id");
        p.quantity = rs.getInt("quantity");
        p.remarks = rs.getString("remarks");
        p.visitId = rs.getInt("outpatientId");
        return p;
    }

    public static Prescription getPrescription(int id) throws SQLException {
        String sql = "SELECT * FROM prescription WHERE id = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Prescription p = extractPrescription(rs);
                return p;
            } else {
                return null;
            }
        }
    }

    public static List<Prescription> getPrescriptionsForVisit(int visitId) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM prescription WHERE outpatientId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prescriptions.add(extractPrescription(rs));
            }
        }
        return prescriptions;
    }

    public void save() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE prescription SET drugId = ?, quantity = ?, dosage = ?, "
                    + "remarks = ?, outpatientId = ? WHERE id = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, drugId);
            stmt.setInt(2, quantity);
            stmt.setString(3, dosage);
            stmt.setString(4, remarks);
            stmt.setInt(5, visitId);
            stmt.setInt(6, id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
                PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionUpdated(new PrescriptionUpdatedEvent(this));
            } else {
                conn.rollback();
                sql = "INSERT INTO prescription (drugId, quantity, dosage, remarks, "
                        + "outpatientId) VALUES(?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, drugId);
                stmt.setInt(2, quantity);
                stmt.setString(3, dosage);
                stmt.setString(4, remarks);
                stmt.setInt(5, visitId);
                updated = stmt.executeUpdate();
                if (updated == 1) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                    PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionAdded(new PrescriptionAddedEvent(this));
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new SQLException("Could not save/update visit");
                }
            }
        }
    }

    public void delete() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE prescription SET deletedAt = ? WHERE id = ?");
            stmt.setInt(1, id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
                PRESCRIPTION_EVENT_MANAGER.notifyPrescriptionDeleted(new PrescriptionDeletedEvent(this));
            } else {
                conn.rollback();
                throw new SQLException("Could not delete prescription " + id);
            }
        }
    }
}

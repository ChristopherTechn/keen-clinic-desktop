package main.patient.patient;

import database.Database;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mustafa Mohamed
 */
public class Patient {

    private static final Logger LOGGER = Logger.getLogger(Patient.class.getName());

    public int id;
    public String firstName;
    public String lastName;
    public String otherNames;
    public String gender;
    public LocalDate dateOfBirth;
    public String phoneNumber;
    public String address;
    public String remarks;
    public LocalDateTime createdAt;
    public String outpatientNumber;
    public int visitCount;
    public LocalDateTime lastVisitDate;

    public static int countPatients() throws SQLException {
        String sql = "SELECT COUNT(*) FROM patient WHERE deletedAt IS NULL";
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public static Patient getPatient(int id) throws SQLException {
        String sql = """
                     SELECT *,
                     (SELECT COUNT(*) FROM outpatient WHERE patientId = patient.id) AS visitCount,
                     (SELECT MAX(datetime(visitDate)) FROM outpatient WHERE patientId = patient.id) AS lastVisitDate 
                     FROM patient WHERE id = ?
                     """;
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Patient patient = new Patient();
                patient.id = rs.getInt(1);
                patient.firstName = rs.getString("firstName");
                patient.lastName = rs.getString("lastName");
                patient.otherNames = rs.getString("otherNames");
                patient.gender = rs.getString("gender");
                patient.dateOfBirth = rs.getString("dateOfBirth") == null ? null
                        : LocalDate.parse(rs.getString("dateOfBirth"), DateTimeFormatter.ISO_DATE);
                patient.phoneNumber = rs.getString("phoneNumber");
                patient.address = rs.getString("address");
                patient.remarks = rs.getString("remarks");
                patient.createdAt = LocalDateTime.parse(rs.getString("createdAt"), DateTimeFormatter.ISO_DATE_TIME);
                patient.outpatientNumber = rs.getString("outpatientNumber");
                patient.visitCount = rs.getInt("visitCount");
                patient.lastVisitDate = rs.getString("lastVisitDate") == null ? null : LocalDateTime.parse(rs.getString("lastVisitDate"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return patient;
            }
        }
        return null;
    }

    public static int updatePatient(Patient patient) throws SQLException {
        String sql = """
                     UPDATE patient SET 
                     firstName = ?, 
                     lastName = ?,
                     otherNames = ?,
                     gender = ?,
                     dateOfBirth = ?,
                     phoneNumber = ?,
                     address = ?,
                     remarks = ?,
                     outpatientNumber = ? 
                     WHERE id = ?
                     """;
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patient.firstName);
            stmt.setString(2, patient.lastName);
            stmt.setString(3, patient.otherNames);
            stmt.setString(4, patient.gender);
            stmt.setString(5, patient.dateOfBirth == null ? null : patient.dateOfBirth.format(DateTimeFormatter.ISO_DATE));
            stmt.setString(6, patient.phoneNumber);
            stmt.setString(7, patient.address);
            stmt.setString(8, patient.remarks);
            stmt.setString(9, patient.outpatientNumber);
            stmt.setInt(10, patient.id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
            }
            conn.rollback();
            return updated;
        }
    }

    public static boolean deletePatient(int id) throws SQLException {
        String sql = "UPDATE patient SET deletedAt = ? WHERE id = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            stmt.setInt(2, id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
            }
            conn.rollback();
            return updated == 1;
        }
    }

    public static List<Patient> getPatients(String search, int numRows, String orderColumn, int offset) throws SQLException {
        String sql = String.format("""
                                   SELECT *,
                                   (SELECT IFNULL(COUNT(*),0) FROM outpatient WHERE patientId = p.id) AS visitCount,
                                   (SELECT MAX(datetime(visitDate)) FROM outpatient WHERE patientId = p.id) AS lastVisitDate 
                                   FROM patient p
                                   WHERE p.deletedAt IS NULL AND 
                                   (p.firstName LIKE ? 
                                   OR p.lastName LIKE ? 
                                   OR p.otherNames LIKE ? 
                                   OR p.phoneNumber LIKE ? 
                                   OR p.address LIKE ? 
                                   OR p.remarks LIKE ? 
                                   OR ((p.firstName LIKE ? AND p.lastName LIKE ? AND p.otherNames LIKE ? )
                                   OR (p.firstName LIKE ? AND p.otherNames LIKE ? AND p.lastName LIKE ? )
                                   OR (p.lastName LIKE ? AND p.firstName LIKE ? AND p.otherNames LIKE ? )
                                   OR (p.lastName LIKE ? AND p.otherNames LIKE ? AND p.firstName LIKE ? )
                                   OR (p.otherNames LIKE ? AND p.firstName LIKE ? AND p.lastName LIKE ? )
                                   OR (p.otherNames LIKE ? AND p.lastName LIKE ? AND p.firstName LIKE ? ))
                                   OR p.outpatientNumber LIKE ? 
                                   )
                     ORDER BY %s
                     LIMIT %d OFFSET %d
                     """, orderColumn, numRows, offset);
        LOGGER.log(Level.INFO, "Getting patients: search: {0}, numRows: {1}, orderColumn: {2}, offset: {3}.",
                new Object[]{search, numRows, orderColumn, offset});
        LOGGER.log(Level.INFO, "{0}", sql);

        search = search.trim();
        String s = "%" + search + "%";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, s);
            }
            String firstName, lastName = "", otherNames = "";
            String names[] = search.replaceAll("\\s{2,}", "").split("\\s");
            firstName = names[0];
            if (names.length > 1) {
                lastName = names[1];
            }
            if (names.length > 2) {
                otherNames = names[2];
            }

            for (int i = 7; i < 24; i += 3) {
                stmt.setString(i, "%" + firstName + "%");
                stmt.setString(i + 1, "%" + lastName + "%");
                stmt.setString(i + 2, "%" + otherNames + "%");
            }
            stmt.setString(25, s);
            ResultSet rs = stmt.executeQuery();
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                Patient patient = new Patient();
                patient.id = rs.getInt("id");
                patient.firstName = rs.getString("firstName");
                patient.lastName = rs.getString("lastName");
                patient.otherNames = rs.getString("otherNames");
                patient.gender = rs.getString("gender");
                patient.dateOfBirth = rs.getString("dateOfBirth") == null ? null : LocalDate.parse(rs.getString("dateOfBirth"), DateTimeFormatter.ISO_DATE);
                patient.phoneNumber = rs.getString("phoneNumber");
                patient.address = rs.getString("address");
                patient.remarks = rs.getString("remarks");
                patient.createdAt = LocalDateTime.parse(rs.getString("createdAt"), DateTimeFormatter.ISO_DATE_TIME);
                patient.outpatientNumber = rs.getString("outpatientNumber");
                patient.visitCount = rs.getInt("visitCount");
                //2024-04-04 12:51:24
                patient.lastVisitDate = rs.getString("lastVisitDate") == null ? null : LocalDateTime.parse(rs.getString("lastVisitDate"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                patients.add(patient);
            }
            return patients;
        }
    }

    public String formatPatientName() {
        String name = "";
        if (firstName != null && !firstName.isBlank()) {
            name += firstName + " ";
        }
        if (lastName != null && !lastName.isBlank()) {
            name += lastName + " ";
        }
        if (otherNames != null && !otherNames.isBlank()) {
            name += otherNames + " ";
        }
        name = name.replaceAll("\\s{2,}", " ").trim();
        return name;
    }
}

package main.drug;

import database.Database;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import main.drug.event.DrugAddedEvent;
import main.drug.event.DrugDeletedEvent;
import main.drug.event.DrugUpdatedEvent;
import static main.drug.event.GlobalDrugEventManager.DRUG_EVENT_MANAGER;

/**
 * A drug.
 *
 * @author Mustafa
 */
public class Drug {

    public int id;
    public String name;
    public String description;
    public int shelfQuantity;
    public int minShelfQuantity;

    public static boolean isDrugExists(String name) throws SQLException {
        String sql = "SELECT IFNULL(COUNT(*),0) FROM drug WHERE `name` = ? AND deletedAt IS NULL";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * If this drug, exists, it will be updated, otherwise it will be saved.
     *
     * @throws java.sql.SQLException
     */
    public void save() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE drug SET `name` = ?, description = ?, shelfQuantity = ?,"
                    + "minShelfQuantity = ? WHERE id = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, shelfQuantity);
            stmt.setInt(4, minShelfQuantity);
            stmt.setInt(5, id);
            int updated = stmt.executeUpdate();
            if (updated != 1) {
                conn.rollback(); // rollback any updates that might have happened
                sql = "INSERT INTO drug (`name`, description, shelfQuantity, minShelfQuantity)"
                        + "VALUES(?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setInt(3, shelfQuantity);
                stmt.setInt(4, minShelfQuantity);
                updated = stmt.executeUpdate();
                if (updated == 1) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                    conn.commit();

                    DRUG_EVENT_MANAGER.notifyDrugAdded(new DrugAddedEvent(this));
                } else {
                    conn.rollback();
                    throw new SQLException("Error during insert");
                }
            } else {
                conn.commit();
                DRUG_EVENT_MANAGER.notifyDrugUpdated(new DrugUpdatedEvent(this));
            }
        }
    }

    public void delete() throws SQLException {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE drug SET deletedAt = ? WHERE id = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            stmt.setInt(2, id);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
                DRUG_EVENT_MANAGER.notifyDrugDeleted(new DrugDeletedEvent(this));
            } else {
                conn.rollback();
                throw new SQLException("Could not delete drug " + id);
            }
        }
    }

    public static Drug extractDrug(ResultSet rs) throws SQLException {
        Drug drug = new Drug();
        drug.description = rs.getString("description");
        drug.id = rs.getInt("id");
        drug.minShelfQuantity = rs.getInt("minShelfQuantity");
        drug.name = rs.getString("name");
        drug.shelfQuantity = rs.getInt("shelfQuantity");
        return drug;
    }

    public static Drug getDrug(int id) throws SQLException {
        String sql = "SELECT * FROM drug WHERE id = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractDrug(rs);
            } else {
                return null;
            }
        }
    }

    public static Drug getDrug(String name) throws SQLException {
        String sql = "SELECT * FROM drug WHERE name = ?";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractDrug(rs);
            } else {
                return null;
            }
        }
    }

    /**
     * Get a drug by name without opening a new connection to the database.
     *
     * @param name the name of the drug
     * @param conn a connection to the database
     * @return the Drug or null if the drug was not found
     * @throws SQLException
     */
    public static Drug getDrug(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM drug WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return extractDrug(rs);
        } else {
            return null;
        }

    }

    /**
     * Get a drug by id without opening a new connection to the database.
     *
     * @param id the drug id
     * @param conn a connection to the database
     * @return a Drug or null if the drug was not found
     */
    public static Drug getDrug(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM drug WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return extractDrug(rs);
        } else {
            return null;
        }

    }

    public static List<Drug> getDrugs() throws SQLException {
        String sql = "SELECT * FROM drug WHERE deletedAt IS NULL ORDER BY `name`, id";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Drug> drugs = new ArrayList<>();
            while (rs.next()) {
                drugs.add(extractDrug(rs));
            }
            return drugs;
        }
    }
}

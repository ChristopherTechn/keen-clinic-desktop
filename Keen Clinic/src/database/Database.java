package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Mustafa Mohamed
 */
public class Database {

    public static final String APP_DATA_FOLDER = System.getenv("APPDATA");
    public static final String APP_NAME = "Keen Clinic";
    private static final String DATABASE_FILE_NAME = "keen-clinic.db";
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static Connection connection;

    public static void createDatabaseIfNotExists() throws IOException, SQLException {
        String connectionString = getDatabaseFilePath();
        LOGGER.log(Level.INFO, "Connection string: {0}", connectionString);
        if (!new File(connectionString).exists()) {
            String folder = APP_DATA_FOLDER + File.separator + APP_NAME;
            String file = APP_DATA_FOLDER + File.separator + APP_NAME
                    + File.separator + DATABASE_FILE_NAME;
            if (!new File(folder).exists()) {
                new File(folder).mkdirs();
                LOGGER.log(Level.INFO, "Created {0}", folder);
            }
            new File(file).createNewFile();
            LOGGER.log(Level.INFO, "Created {0}", file);
            // Tables user, patient, outpatient and settings existed before we started using migrations
            String sqls[] = {
                """
            CREATE TABLE "user" (
            	"id"	INTEGER,
            	"username"	TEXT NOT NULL,
            	"password"	TEXT NOT NULL,
            	PRIMARY KEY("id")
            );
            """,
                """
            CREATE TABLE "patient" (
            	"id"	INTEGER NOT NULL,
            	"firstName"	TEXT DEFAULT NULL,
            	"lastName"	TEXT DEFAULT NULL,
            	"otherNames"	TEXT DEFAULT NULL,
            	"gender"	TEXT DEFAULT NULL,
            	"dateOfBirth"	TEXT DEFAULT NULL,
            	"phoneNumber"	TEXT DEFAULT NULL,
            	"address"	TEXT DEFAULT NULL,
            	"remarks"	TEXT DEFAULT NULL,
            	"createdAt"	TEXT DEFAULT NULL,
            	"deletedAt"	TEXT DEFAULT NULL,
            	PRIMARY KEY("id")
            );
            """,
                """
            CREATE TABLE "outpatient" (
            	"id"	INTEGER,
            	"patientId"	INTEGER,
            	"opdNumber"	TEXT DEFAULT NULL,
            	"visitDate"	TEXT DEFAULT NULL,
            	"weight"	REAL DEFAULT NULL,
            	"height"	REAL DEFAULT NULL,
            	"visualAcuity"	TEXT DEFAULT NULL,
            	"complaints"	TEXT DEFAULT NULL,
            	"physicalExam"	TEXT DEFAULT NULL,
            	"labExam"	TEXT DEFAULT NULL,
            	"outcome"	TEXT DEFAULT NULL,
            	"diagnosis"	TEXT DEFAULT NULL,
            	"treatment"	TEXT DEFAULT NULL,
            	"remarks"	TEXT DEFAULT NULL,
            	FOREIGN KEY("patientId") REFERENCES "patient"("id") ON UPDATE CASCADE ON DELETE CASCADE,
            	PRIMARY KEY("id")
            );
            """,
                """
                CREATE TABLE "settings" (
                	"id"	INTEGER,
                	"theme"	TEXT DEFAULT NULL,
                	"fontSize"	INTEGER DEFAULT NULL,
                	PRIMARY KEY("id")
                );
                """
            };

            try (Connection conn = getConnection()) {
                for (String sql : sqls) {
                    LOGGER.log(Level.INFO, "Executing {0}", sql);
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.execute();
                }
                conn.commit();
            }
        } else {
            LOGGER.log(Level.INFO, "Database file exixts. Assuming valid database schema.");
        }
    }

    /**
     *
     *
     * @return @throws IOException
     */
    public static String getDatabaseFilePath() {

        String connectionString = APP_DATA_FOLDER + File.separator + APP_NAME
                + File.separator + DATABASE_FILE_NAME;
        return connectionString;
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseFilePath() + "");
            connection.setAutoCommit(false); // force people to commit their own stuff.
        }
        return connection;
    }

    /**
     * Do not use.
     *
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private static List<String> _getAllMigrationFiles() throws URISyntaxException, IOException {
        Path path = new File(
                Database.class.getResource("/database/migrations/1-create-migrations-table.sql").toURI()
        ).getParentFile().toPath();
        var files = Files.walk(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(n -> n.endsWith(".sql"))
                .collect(Collectors.toList());

        // sort the migration files in order of name. Migration files start with a number
        Pattern pattern = Pattern.compile("^(%d+).*$");
        files.sort((String a, String b) -> {
            int first = -1, second = -1;
            Matcher matcherA = pattern.matcher(a);
            if (matcherA.find()) {
                first = Integer.parseInt(matcherA.group(1));
            }
            Matcher matcherB = pattern.matcher(b);
            if (matcherB.find()) {
                second = Integer.parseInt(matcherB.group(1));
            }
            if (first != -1 && second != -1) {
                return Integer.compare(first, second);
            }
            return a.compareTo(b);
        });
        return files;

    }

    /**
     * Get all migration files.
     *
     * @return
     */
    private static List<String> getAllMigrationFiles() {
        List<String> migrationFiles = new ArrayList<>();
        File migrationsFolder = new File("migrations");
        // Running jpackage app puts files inside app folder but attempts to resolve them using the root folder.
        // If the migrations folder does not exist in the root folder, check for it in the app folder.
        if (!migrationsFolder.exists()) {
            migrationsFolder = new File("app" + File.separator + "migrations");
        }
        if (migrationsFolder.exists() && migrationsFolder.isDirectory()) {
            for (File file : migrationsFolder.listFiles()) {
                migrationFiles.add(file.getName());
            }
            // sort the migration files in order of name. Migration files start with a number
            Pattern pattern = Pattern.compile("^(%d+).*$");
            migrationFiles.sort((String a, String b) -> {
                int first = -1, second = -1;
                Matcher matcherA = pattern.matcher(a);
                if (matcherA.find()) {
                    first = Integer.parseInt(matcherA.group(1));
                }
                Matcher matcherB = pattern.matcher(b);
                if (matcherB.find()) {
                    second = Integer.parseInt(matcherB.group(1));
                }
                if (first != -1 && second != -1) {
                    return Integer.compare(first, second);
                }
                return a.compareTo(b);
            });
        }
        return migrationFiles;
    }

    private static List<String> getExecutedMigrationFiles() throws SQLException {
        LOGGER.log(Level.INFO, "Getting executed migration files.");
        List<String> pendingMigrations = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='migrations'";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            if (rs.next()) {
                sql = "SELECT fileName FROM migrations ORDER BY fileName ASC ";
                ResultSet rs2 = conn.prepareStatement(sql).executeQuery();
                while (rs2.next()) {
                    pendingMigrations.add(rs2.getString(1));
                }
            } else {
                LOGGER.log(Level.INFO, "migrations table does not exist.");
            }

        }
        return pendingMigrations;
    }

    private static String getMigrationFileStatements(String file) throws FileNotFoundException, URISyntaxException {

        File f = new File("migrations" + File.separator + file);
        // if file does not exist, check for it in the app/migrations folder
        if (!f.exists()) {
            f = new File("app" + File.separator + "migrations" + File.separator + file);
        }
        String content = "";
        try (Scanner scanner = new Scanner(f)) {
            while (scanner.hasNextLine()) {
                content += scanner.nextLine();
            }
        }
        LOGGER.log(Level.INFO, "Migration file contents: {0}, {1}", new Object[]{file, content});
        return content;
    }

    private static boolean runMigration(String file) throws FileNotFoundException, SQLException, URISyntaxException {
        String content = getMigrationFileStatements(file);
        String sql = "INSERT INTO migrations (fileName) VALUES(?)";

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(content);
            stmt.execute();

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, file);
            int updated = stmt.executeUpdate();
            if (updated == 1) {
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        }
    }

    public static void runPendingMigrations() throws SQLException, FileNotFoundException, URISyntaxException, IOException {

        LOGGER.log(Level.INFO, "Running pending migrations.");
        List<String> allMigrations = getAllMigrationFiles();
        List<String> executedMigrations = getExecutedMigrationFiles();
        for (String executed : executedMigrations) {
            LOGGER.log(Level.INFO, "Already executed migration file {0}", executed);
            allMigrations.remove(executed);
        }
        LOGGER.log(Level.INFO, "Pending migrations: {0} : ", allMigrations.size());
        for (String m : allMigrations) {
            LOGGER.log(Level.INFO, "{0}", m);
        }
        for (String migration : allMigrations) {
            LOGGER.log(Level.INFO, "Executing SQL in migration file {0}", migration);
            if (runMigration(migration)) {
                LOGGER.log(Level.INFO, "Executed migration file {0}", migration);
            } else {
                LOGGER.log(Level.INFO, "Could not execute migration file {0}", migration);
                throw new SQLException(String.format("Could not execute migration file %s", migration));
            }
        }

    }

}

package database;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application properties. This class should be used for "small" properties
 * only. Do not store passwords and other sensitive items in this class.
 *
 * @author Mustafa Mohamed.
 */
public class AppProperties {

    private static final String PROPERTIES_FILE = Database.APP_DATA_FOLDER + File.separator + 
            Database.APP_NAME + File.separator + "KeenClinic.properties";

    private static void createFileIfNotExists() throws IOException {
        File file = new File(PROPERTIES_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static String getScreenResolutionString() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        return String.format("%dx%d", width, height);
    }

    /**
     * Save a property. The method fails silently if an error occurred.
     *
     * @param key the key
     * @param value the value
     */
    public static void put(String key, String value) {
        try {
            Properties appProps = new Properties();
            appProps.load(new FileInputStream(PROPERTIES_FILE));
            appProps.setProperty(key + getScreenResolutionString(), value);
            // probably expensive to save to file each time, but we don't anticipate many properties
            appProps.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException ex) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get a property.
     *
     * @param key the key
     * @param defaultValue the default value to return if the key does not
     * exist.
     * @return the property or defaultValue if the key does not exist.
     */
    public static String get(String key, String defaultValue) {
        Properties appProps = new Properties();
        try {
            createFileIfNotExists();
            appProps.load(new FileInputStream(PROPERTIES_FILE));
            return appProps.getProperty(key + getScreenResolutionString(), defaultValue);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }

        return defaultValue;
    }

    /**
     * Get a property
     *
     * @param key the key
     * @return the property or null if the property was not found.
     */
    public static String get(String key) {
        return get(key, null);
    }
    
    public static void clearAll(){
        try {
            Properties appProps = new Properties();
            appProps.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException ex) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

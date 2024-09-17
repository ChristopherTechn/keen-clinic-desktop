package settings;

/**
 *
 * @author Mustafa
 */
public interface SettingsEventListener {

    /**
     * Fired when them is changed.
     *
     * @param event the event data
     */
    default void onThemeChanged(ThemeChangedEvent event) {
    }

    /**
     * Fired when the database is restored from a backup.
     *
     * @param event
     */
    default void onDatabaseRestored(DatabaseRestoredEvent event) {
    }
}

package settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for settings events. It is recommended to have at most one instance
 * of this class.
 *
 * @author Mustafa
 */
public class SettingsEventManager {

    private final List<SettingsEventListener> listeners = new ArrayList<>();

    public void addListener(SettingsEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SettingsEventListener listener) {
        listeners.remove(listener);
    }

    public void notifyThemeChanged(ThemeChangedEvent event) {
        listeners.forEach(listener -> listener.onThemeChanged(event));
    }

    public void notifyDatabaseRestored(DatabaseRestoredEvent event) {
        listeners.forEach(listener -> listener.onDatabaseRestored(event));
    }
}

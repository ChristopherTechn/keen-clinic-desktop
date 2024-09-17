package settings;

/**
 * Since it is best to use one instance of SettingsManager, all parties
 * interested in listening on settings events should register as listeners using
 * this class.
 *
 * @author Mustafa
 */
public class GlobalSettingsEventManager {
    public static final SettingsEventManager SETTINGS_MANAGER = new SettingsEventManager();
}

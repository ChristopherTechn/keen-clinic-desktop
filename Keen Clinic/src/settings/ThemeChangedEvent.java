package settings;

/**
 * The app theme has been changed. Ideally, the app colors will change
 * automatically, but some stuff like icon colors will not change. Use this
 * event to notify listeners that a theme has been changed.
 *
 * @author Mustafa
 */
public class ThemeChangedEvent {

    public String newTheme;

    public ThemeChangedEvent(String newTheme) {
        this.newTheme = newTheme;
    }
}

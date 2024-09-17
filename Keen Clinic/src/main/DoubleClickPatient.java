package main;

/**
 *
 * @author Mustafa Mohamed
 */
public enum DoubleClickPatient {
    VISIT_HISTORY("Visit History"),
    EDIT_PATIENT("Edit Patient"),
    DO_NOTHING("Do nothing");

    private final String name;

    DoubleClickPatient(String name) {
        this.name = name;
    }

    public static DoubleClickPatient fromString(String name) {
        for (DoubleClickPatient v : values()) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}

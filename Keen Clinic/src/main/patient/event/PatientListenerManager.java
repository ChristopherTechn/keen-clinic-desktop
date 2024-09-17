package main.patient.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifies all listeners of a patient event. It is recommended to have one
 * instance of this class throughout the app.
 *
 * @author Mustafa
 */
public class PatientListenerManager {

    private final List<PatientListener> listeners = new ArrayList<>();

    public void addListener(PatientListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PatientListener listener) {
        listeners.remove(listener);
    }

    public void notifyPatientAdded(PatientAddedEvent event) {
        for (var listener : listeners) {
            listener.onPatientAdded(event);
        }
    }

    public void notifyPatientUpdated(PatientUpdatedEvent event) {
        for (var listener : listeners) {
            listener.onPatientUpdated(event);
        }
    }

    public void notifyPatientDeleted(PatientDeletedEvent event) {
        for (var listener : listeners) {
            listener.onPatientDeleted(event);
        }
    }

}

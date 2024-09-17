package main.patient.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifies all listeners of a visit event. It is recommended to have at most
 * one instance of this class throughout the app.
 *
 * @author Mustafa
 */
public class VisitListenerManager {

    private final List<VisitListener> listeners = new ArrayList<>();

    public void addListener(VisitListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VisitListener listener) {
        listeners.add(listener);
    }

    public void notifyVisitAdded(VisitAddedEvent event) {
        listeners.forEach(listener -> listener.onVisitAdded(event));
    }

    public void notifyVisitUpdated(VisitUpdatedEvent event) {
        listeners.forEach(listener -> listener.onVisitUpdated(event));
    }

    public void notifyVisitDeleted(VisitDeletedEvent event) {
        listeners.forEach(listener -> listener.onVisitDeleted(event));
    }
}

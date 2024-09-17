package main.drug.event;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mustafa
 */
public class DrugEventManager {

    private final List<DrugEventListener> listeners = new ArrayList<>();

    public void addListener(DrugEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(DrugEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void notifyDrugAdded(DrugAddedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onDrugAdded(event));
        }
    }

    public void notifyDrugDeleted(DrugDeletedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onDrugDeleted(event));
        }
    }

    public void notifyDrugUpdated(DrugUpdatedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onDrugUpdated(event));
        }
    }
}

package main.patient.visit.prescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for prescription events. It is recommended to have at most one
 * instance of this class in the entire application.
 *
 * @author Mustafa
 */
public class PrescriptionEventManager {

    private final List<PrescriptionEventListener> listeners = new ArrayList<>();

    public void addListener(PrescriptionEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(PrescriptionEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void notifyPrescriptionAdded(PrescriptionAddedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionAdded(event));
        }
    }

    public void notifyPrescriptionUpdated(PrescriptionUpdatedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionUpdated(event));
        }
    }

    public void notifyPrescriptionDeleted(PrescriptionDeletedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionDeleted(event));
        }
    }

    public void notifyPrescriptionTempAdded(PrescriptionTempAddedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionTempAdded(event));
        }
    }

    public void notifyPrescriptionTempUpdated(PrescriptionTempUpdatedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionTempUpdated(event));
        }
    }

    public void notifyPrescriptionTempDeleted(PrescriptionTempDeletedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onPrescriptionTempDeleted(event));
        }
    }

    public void notifyAllowNegativeShelf(AllowNegativeShelfEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> listener.onAllowNegativeShelf(event));
        }
    }
}

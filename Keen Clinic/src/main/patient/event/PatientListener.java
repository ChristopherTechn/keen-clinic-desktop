package main.patient.event;

/**
 *
 * @author Mustafa.
 */
public interface PatientListener {
    public void onPatientAdded(PatientAddedEvent event);
    public void onPatientDeleted(PatientDeletedEvent event);
    public void onPatientUpdated(PatientUpdatedEvent event);
}

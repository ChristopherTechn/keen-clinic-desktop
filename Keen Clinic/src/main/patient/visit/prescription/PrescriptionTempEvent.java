package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public abstract class PrescriptionTempEvent {

    public final int eventId;
    public final Prescription prescription;

    public PrescriptionTempEvent(final int eventId, final Prescription prescription) {
        this.eventId = eventId;
        this.prescription = prescription;
    }
}

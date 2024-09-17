package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionTempDeletedEvent extends PrescriptionTempEvent {

    public PrescriptionTempDeletedEvent(int eventId, Prescription prescription) {
        super(eventId, prescription);
    }

}

package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionTempAddedEvent extends PrescriptionTempEvent {

    public PrescriptionTempAddedEvent(int eventId, Prescription prescription) {
        super(eventId, prescription);
    }

}

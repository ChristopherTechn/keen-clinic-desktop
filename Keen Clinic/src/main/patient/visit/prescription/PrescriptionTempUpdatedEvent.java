package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionTempUpdatedEvent extends PrescriptionTempEvent {
    
    public Prescription oldPrescription;

    public PrescriptionTempUpdatedEvent(int eventId, Prescription prescription) {
        super(eventId, prescription);
    }

}

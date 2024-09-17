package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionUpdatedEvent {

    public final Prescription prescription;

    public PrescriptionUpdatedEvent(Prescription prescription) {
        this.prescription = prescription;

    }
}

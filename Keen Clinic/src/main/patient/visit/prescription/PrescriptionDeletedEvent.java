package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionDeletedEvent {

    public final Prescription prescription;

    /**
     *
     * @param prescription
     */
    public PrescriptionDeletedEvent(Prescription prescription) {
        this.prescription = prescription;

    }
}

package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public class PrescriptionAddedEvent {

    public Prescription prescription;

    public PrescriptionAddedEvent(Prescription p) {
        prescription = p;
    }

}

package main.patient.event;

import main.patient.patient.Patient;

/**
 *
 * @author Mustafa
 */
public class PatientUpdatedEvent {

    public Patient oldPatient;
    public Patient newPatient;
}

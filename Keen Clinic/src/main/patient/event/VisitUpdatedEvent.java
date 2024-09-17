package main.patient.event;

import main.patient.visit.Outpatient;

/**
 *
 * @author Mustafa
 */
public class VisitUpdatedEvent {
    public Outpatient oldVisit;
    public Outpatient newVisit;
}

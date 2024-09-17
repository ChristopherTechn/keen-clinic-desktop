package main.patient.event;

/**
 *
 * @author Mustafa
 */
public class GlobalEventManager {

    public static final PatientListenerManager PATIENT_LISTENER_MANAGER = new PatientListenerManager();
    public static final VisitListenerManager VISIT_LISTENER_MANAGER = new VisitListenerManager();
}

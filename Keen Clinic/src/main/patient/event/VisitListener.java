package main.patient.event;

/**
 *
 * @author Mustafa
 */
public interface VisitListener {
    public void onVisitAdded(VisitAddedEvent event);
    public void onVisitUpdated(VisitUpdatedEvent event);
    public void onVisitDeleted(VisitDeletedEvent event);
}

package main.drug.event;

/**
 *
 * @author Mustafa
 */
public interface DrugEventListener {

    public void onDrugAdded(DrugAddedEvent event);

    public void onDrugUpdated(DrugUpdatedEvent event);

    public void onDrugDeleted(DrugDeletedEvent event);
}

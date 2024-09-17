package main.drug.event;

/**
 * The global drug event manager. It is recommended to have at most one instance
 * of this class. Having multiple instances might result in events being fired
 * multiple times.
 *
 * @author Mustafa
 */
public class GlobalDrugEventManager {

    public static final DrugEventManager DRUG_EVENT_MANAGER = new DrugEventManager();
}

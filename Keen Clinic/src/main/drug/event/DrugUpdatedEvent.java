package main.drug.event;

import main.drug.Drug;

/**
 *
 * @author Mustafa
 */
public class DrugUpdatedEvent {

    public Drug drug;

    public DrugUpdatedEvent(Drug drug) {
        this.drug = drug;
    }

}

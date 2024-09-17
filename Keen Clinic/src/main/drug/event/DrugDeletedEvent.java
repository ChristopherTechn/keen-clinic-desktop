package main.drug.event;

import main.drug.Drug;

/**
 *
 * @author Mustafa
 */
public class DrugDeletedEvent {

    public Drug drug;

    public DrugDeletedEvent(Drug drug) {
        this.drug = drug;
    }
}

package main.drug.event;

import main.drug.Drug;

/**
 *
 * @author Mustafa
 */
public class DrugAddedEvent {
    public Drug drug;
    
    public DrugAddedEvent(Drug drug){
        this.drug = drug;
    }
}

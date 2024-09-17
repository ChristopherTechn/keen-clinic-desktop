package main.patient.visit.prescription;

/**
 *
 * @author Mustafa
 */
public interface PrescriptionEventListener {

    public void onPrescriptionAdded(PrescriptionAddedEvent event);

    public void onPrescriptionUpdated(PrescriptionUpdatedEvent event);

    public void onPrescriptionDeleted(PrescriptionDeletedEvent event);
    
    default void onPrescriptionTempAdded(PrescriptionTempAddedEvent event){
        
    }
    
    default void onPrescriptionTempUpdated(PrescriptionTempUpdatedEvent event){
        
    }
    
    default void onPrescriptionTempDeleted(PrescriptionTempDeletedEvent event){
        
    }
    
    default void onAllowNegativeShelf(AllowNegativeShelfEvent event){
        
    }
}

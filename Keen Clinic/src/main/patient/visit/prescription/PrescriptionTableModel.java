package main.patient.visit.prescription;

import models.MyAbstractTableModel;

/**
 *
 * @author Mustafa
 */
public class PrescriptionTableModel extends MyAbstractTableModel<Prescription> {

    private static final String columnNames[] = {
        "#", "Drug", "Quantity", "Dosage", "Remarks"
    };

    public PrescriptionTableModel() {
        super(columnNames);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Prescription p = getRowItem(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                rowIndex + 1;
            case 1 ->
                p.drugName;
            case 2 ->
                p.quantity;
            case 3 ->
                p.dosage;
            case 4 ->
                p.remarks;
            default ->
                null;
        };
    }

}

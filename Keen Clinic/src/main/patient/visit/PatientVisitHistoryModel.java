package main.patient.visit;

import java.time.format.DateTimeFormatter;
import models.MyAbstractTableModel;

/**
 *
 * @author Mustafa Mohamed
 */
public class PatientVisitHistoryModel extends MyAbstractTableModel<Outpatient> {

    private static final String columnNames[] = {
        "#", "OPD Number", "Visit Date", "Weight", "Height", "BMI", "Outcome",
        "Diagnosis", "Treatment", "Remarks"
    };

    public PatientVisitHistoryModel() {
        super(columnNames);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var o = getRowItem(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                rowIndex + 1;
            case 1 ->
                o.opdNumber;
            case 2 ->
                o.visitDate == null ? null : o.visitDate.format(DateTimeFormatter.ofPattern("d MM yyyy h:mm a"));
            case 3 ->
                o.weight;
            case 4 ->
                o.height;
            case 5 ->
                o.weight > 0 && o.height > 0 ? (o.weight / (o.height * o.height)) : null;
            case 6 ->
                o.outcome;
            case 7 ->
                o.diagnosis;
            case 8 ->
                o.treatment;
            case 9 ->
                o.remarks;
            default ->
                null;
        };
    }

}

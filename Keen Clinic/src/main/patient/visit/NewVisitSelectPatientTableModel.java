package main.patient.visit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import models.MyAbstractTableModel;
import main.patient.patient.Patient;

/**
 *
 * @author Mustafa Mohamed
 */
public class NewVisitSelectPatientTableModel extends MyAbstractTableModel<Patient> {

    private static final String columns[] = {
        "Outpatient #", "Patient Name", "Gender", "Age", "Date of Birth", "Phone Number", "Address", "Remarks"
    };

    public NewVisitSelectPatientTableModel() {
        super(columns);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var p = getRowItem(rowIndex);

        return switch (columnIndex) {
            case 0 ->
                p.outpatientNumber;
            case 1 ->
                p.formatPatientName();
            case 2 ->
                p.gender;
            case 3 ->
                p.dateOfBirth == null ? null : ChronoUnit.YEARS.between(p.dateOfBirth, LocalDate.now());
            case 4 ->
                p.dateOfBirth == null ? null : p.dateOfBirth.format(DateTimeFormatter.ISO_DATE);
            case 5 ->
                p.phoneNumber;
            case 6 ->
                p.address;
            case 7 ->
                p.remarks;
            default ->
                null;
        };
    }

}

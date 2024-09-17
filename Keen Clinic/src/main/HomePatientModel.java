package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import models.MyAbstractTableModel;
import main.patient.patient.Patient;

/**
 *
 * @author Mustafa Mohamed
 */
public class HomePatientModel extends MyAbstractTableModel<Patient> {

    public static final String columnNames[] = {
        "Outpatient #", "Patient Name", "Gender", "Date of Birth", "Age", "Phone Number",
        "Address", "Last Visit Date", "Number of Visits"
    };

    public HomePatientModel() {
        super(columnNames);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Patient p = getRowItem(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                p.outpatientNumber;
            case 1 ->
                p.formatPatientName();
            case 2 ->
                p.gender;
            case 3 ->
                p.dateOfBirth == null ? null : p.dateOfBirth.format(DateTimeFormatter.ISO_DATE);
            case 4 ->
                p.dateOfBirth == null ? null : ChronoUnit.YEARS.between(p.dateOfBirth, LocalDate.now());
            case 5 ->
                p.phoneNumber;
            case 6 ->
                p.address;
            case 7 ->
                p.lastVisitDate == null ? null : p.lastVisitDate.format(DateTimeFormatter.ofPattern("d MMM yyyy h:mm a"));
            case 8 ->
                p.visitCount;
            default ->
                null;
        };
    }

}

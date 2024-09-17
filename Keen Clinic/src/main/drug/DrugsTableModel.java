package main.drug;

import models.MyAbstractTableModel;

/**
 *
 * @author Mustafa
 */
public class DrugsTableModel extends MyAbstractTableModel<Drug> {

    private static final String[] columnNames = {
        "#", "Name", "Shelf quantity", "Min. shelf quantity", "Description"
    };

    public DrugsTableModel() {
        super(columnNames);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Drug drug = getRowItem(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                rowIndex + 1;
            case 1 ->
                drug.name;
            case 2 ->
                drug.shelfQuantity;
            case 3 ->
                drug.minShelfQuantity;
            case 4 ->
                drug.description;
            default ->
                null;
        };
    }

}

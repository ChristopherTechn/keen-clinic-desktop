package models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Mustafa Mohamed
 */
public abstract class MyAbstractTableModel<T> extends AbstractTableModel {

    private final ArrayList<T> rowItems = new ArrayList<>();
    private final String columnNames[];

    public MyAbstractTableModel(String columnNames[]) {
        super();
        this.columnNames = columnNames;
    }

    public int size() {
        return rowItems.size();
    }

    public List<T> getRowItems() {
        return rowItems;
    }

    /**
     * Get the index of an item in the list.
     *
     * @param item the item
     * @return the index or -1 if the index was not found
     */
    public int getIndex(T item) {
        for (int i = 0; i < rowItems.size(); i++) {
            if (rowItems.get(i).equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public void addRowItem(T rowItem) {
        rowItems.add(rowItem);
        int size = rowItems.size();
        this.fireTableRowsInserted(size - 1, size - 1);
    }

    public void insertRowItem(int index, T rowItem) {
        rowItems.add(index, rowItem);
        this.fireTableRowsInserted(index, index);
    }

    public void addRowItems(List<T> rowItems) {
        this.rowItems.addAll(rowItems);
        fireTableDataChanged();
    }

    public T getRowItem(int index) {
        return rowItems.get(index);
    }

    public void removeRowItem(T rowItem) {
        if (rowItems.contains(rowItem)) {
            int index = rowItems.indexOf(rowItem);
            if (index >= 0) {
                rowItems.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }
    }

    public void clearRowItems() {
        rowItems.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rowItems.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

}

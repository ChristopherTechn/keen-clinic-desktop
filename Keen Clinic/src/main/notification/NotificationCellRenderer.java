package main.notification;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Mustafa Mohamed
 */
public class NotificationCellRenderer extends DefaultTableCellRenderer {

    private final Font myBoldFont = new Font("Segoe UI bold", Font.BOLD, 12);
    private final Font myNormalFont = new Font("Segoe UI", Font.PLAIN, 12);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
            NotificationModel model = (NotificationModel) table.getModel();
            var item = model.getRowItem(row);
            if (item.viewedAt == null) {
                setFont(myBoldFont);
            } else {
                setFont(myNormalFont);
            }

        }
        return this;
    }

}

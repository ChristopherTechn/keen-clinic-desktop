package main.notification;

import java.time.format.DateTimeFormatter;
import models.MyAbstractTableModel;

/**
 *
 * @author DELL
 */
public class NotificationModel extends MyAbstractTableModel<Notification> {

    private static final String columnNames[] = {"#", "Title", "Message", "Date/Time"};

    public NotificationModel() {
        super(columnNames);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var notif = this.getRowItem(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                rowIndex + 1;
            case 1 ->
                notif.title;
            case 2 ->
                notif.body;
            case 3 ->
                notif.createdAt == null ? null : notif.createdAt.format(DateTimeFormatter.ofPattern("d MMM h:mm a"));
            default ->
                null;
        };
    }

}

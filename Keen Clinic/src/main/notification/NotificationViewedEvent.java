package main.notification;

/**
 * A notification has been viewed. Note that this class does not update the
 * database to reflect that the notification has been viewed. It is up to the
 * code that creates a NotificationViewedEvent to update the notification in the
 * database.
 *
 * @author Mustafa Mohamed
 */
public class NotificationViewedEvent extends NotificationEvent {

}

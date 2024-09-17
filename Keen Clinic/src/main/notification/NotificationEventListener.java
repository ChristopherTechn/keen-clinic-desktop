package main.notification;

/**
 * NotificationEventListener contains methods that classes implementing this
 * interface will use to handle notification events.
 *
 * @author Mustafa Mohamed
 */
public interface NotificationEventListener {

    /**
     * A new notification has been added.
     *
     * @param event the notification event
     */
    default void onNotificationAdded(NotificationAddedEvent event) {
    }

    /**
     * A notification has been viewed.
     *
     * @param event the notification event
     */
    default void onNotificationViewed(NotificationViewedEvent event) {
    }

    /**
     * Notifications have been listed by clicking the notifications button on
     * the menu bar.
     *
     * @param event the notifications listed event. Currently, this class is
     * empty.
     */
    default void onNotificationsListed(NotificationsListedEvent event) {

    }
}

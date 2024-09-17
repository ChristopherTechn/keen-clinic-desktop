package main.notification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * It is recommended to have one instance of this class in the entire
 * application and add listeners
 *
 * @author Mustafa Mohamed.
 */
public class NotificationEventManager {

    private final List<NotificationEventListener> listeners = new ArrayList<>();

    public void addListener(NotificationEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(NotificationEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeAllListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public void notifyNotificationAdded(NotificationAddedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> {
                listener.onNotificationAdded(event);
            });
        }
    }

    public void notifyNotificationViewed(NotificationViewedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> {
                listener.onNotificationViewed(event);
            });
        }
    }

    public void notifyNotificationsListed(NotificationsListedEvent event) {
        synchronized (listeners) {
            listeners.forEach(listener -> {
                listener.onNotificationsListed(event);
            });
        }
    }
}

package main.notification;

import java.time.LocalDateTime;

/**
 * A notification as displayed on the application top-right or bottom-right
 * corner.
 *
 * @author Mustafa Mohamed
 */
public class Notification {

    /**
     * A unique identifier of this notification.
     */
    public int id;

    /**
     * The title of the notification.
     */
    public String title;
    /**
     * The actual notification message.
     */
    public String body;
    /**
     * The time that the notification was viewed.
     */
    public LocalDateTime viewedAt;
    /**
     * The intended recipient of the notification.
     */
    public int userId;
    /**
     * The time that the notification was created.
     */
    public LocalDateTime createdAt;
}

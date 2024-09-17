package main.notification;

import auth.Login;
import database.Database;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import static main.notification.GlobalNotificationEventManager.NOTIFICATION_EVENT_MANAGER;

/**
 *
 * @author Mustafa Mohamed.
 */
public class ListNotifications extends javax.swing.JDialog implements NotificationEventListener {

    private final NotificationModel modelNotifications = new NotificationModel();

    /**
     * Creates new form ListNotifications
     *
     * @param parent
     * @param modal
     */
    public ListNotifications(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setupTable();
        jLabelSelect.putClientProperty("FlatLaf.styleClass", "h4");
        getNotifications();
    }

    private void setupTable() {
        jTableNotifications.setModel(modelNotifications);
        jTableNotifications.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            updateNotificationMessageField();
        });
        jTableNotifications.getColumnModel().getColumn(0).setWidth(10);
        jTableNotifications.getColumnModel().getColumn(0).setPreferredWidth(10);
        NotificationCellRenderer numberRenderer = new NotificationCellRenderer();
        jTableNotifications.getColumnModel().getColumn(0).setCellRenderer(numberRenderer);

        NotificationCellRenderer titleRenderer = new NotificationCellRenderer();
        jTableNotifications.getColumnModel().getColumn(1).setCellRenderer(titleRenderer);

        NotificationCellRenderer bodyRenderer = new NotificationCellRenderer();
        jTableNotifications.getColumnModel().getColumn(2).setCellRenderer(bodyRenderer);

        NotificationCellRenderer dateRenderer = new NotificationCellRenderer();
        jTableNotifications.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);
    }

    private void updateNotificationMessageField() {
        int selectedCount = jTableNotifications.getSelectedRowCount();
        if (selectedCount != 1) {
            jTextAreaMessage.setText("");
        } else {
            int selectedRow = jTableNotifications.getSelectedRow();
            var item = modelNotifications.getRowItem(jTableNotifications.convertRowIndexToModel(selectedRow));
            jTextAreaMessage.setText(item.body);
            if (item.viewedAt == null) {
                // mark the notification as viewed
                try (Connection conn = Database.getConnection()) {
                    LocalDateTime now = LocalDateTime.now();
                    String sql = "UPDATE notifications SET viewedAt = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, now.format(DateTimeFormatter.ISO_DATE_TIME));
                    stmt.setInt(2, item.id);
                    int updated = stmt.executeUpdate();
                    if (updated == 1) {
                        conn.commit();
                        item.viewedAt = now;
                        modelNotifications.fireTableRowsUpdated(selectedRow, selectedRow);
                        NotificationViewedEvent event = new NotificationViewedEvent();
                        event.notification = item;
                        NOTIFICATION_EVENT_MANAGER.notifyNotificationViewed(event);
                    } else {
                        conn.rollback();
                        throw new SQLException("Could not mark notification as read: " + item.id);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ListNotifications.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    private void getNotifications() {
        String sql = "SELECT * FROM notifications WHERE userId = ? OR userId IS NULL ORDER BY datetime(createdAt) DESC";
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Login.LOGGED_IN_USER_ID);
            ResultSet rs = stmt.executeQuery();
            modelNotifications.clearRowItems();
            while (rs.next()) {
                Notification n = new Notification();
                n.body = rs.getString("body");
                n.createdAt = LocalDateTime.parse(rs.getString("createdAt"), DateTimeFormatter.ISO_DATE_TIME);
                n.viewedAt = rs.getString("viewedAt") != null ? LocalDateTime.parse(rs.getString("viewedAt"), DateTimeFormatter.ISO_DATE_TIME) : null;
                n.id = rs.getInt(1);
                n.title = rs.getString("title");
                if (rs.getObject("userId") != null) {
                    n.userId = rs.getInt("userId");
                }
                modelNotifications.addRowItem(n);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListNotifications.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void onComponentShown() {
        NOTIFICATION_EVENT_MANAGER.addListener(this);
    }

    private void onComponentHidden() {
        NOTIFICATION_EVENT_MANAGER.removeListener(this);
    }

    @Override
    public void onNotificationViewed(NotificationViewedEvent event) {
        Notification notif = event.notification;
        if (notif != null) {

        }
    }

    @Override
    public void onNotificationAdded(NotificationAddedEvent event) {
        Notification notif = event.notification;
        if (notif != null) {
            modelNotifications.insertRowItem(0, notif);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTableNotifications = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaMessage = new javax.swing.JTextArea();
        jLabelSelect = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Notifications");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jTableNotifications.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Title", "Message", "Date/Time"
            }
        ));
        jTableNotifications.setRowHeight(30);
        jTableNotifications.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableNotifications.setShowVerticalLines(true);
        jScrollPane2.setViewportView(jTableNotifications);

        jTextAreaMessage.setEditable(false);
        jTextAreaMessage.setColumns(20);
        jTextAreaMessage.setLineWrap(true);
        jTextAreaMessage.setRows(5);
        jScrollPane3.setViewportView(jTextAreaMessage);

        jLabelSelect.setText("Select a notification in the list above to display it below");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelSelect)
                        .addGap(0, 56, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabelSelect)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        onComponentShown();
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        onComponentHidden();
    }//GEN-LAST:event_formComponentHidden


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelSelect;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableNotifications;
    private javax.swing.JTextArea jTextAreaMessage;
    // End of variables declaration//GEN-END:variables
}

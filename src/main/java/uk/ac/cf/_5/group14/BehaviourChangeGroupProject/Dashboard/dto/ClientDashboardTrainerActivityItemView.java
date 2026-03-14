package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

public class ClientDashboardTrainerActivityItemView {

    private final String icon;
    private final String title;
    private final String body;
    private final String meta;
    private final String href;
    private final Long notificationId;
    private final boolean unread;
    private final boolean canMarkRead;
    private final boolean canDismiss;

    public ClientDashboardTrainerActivityItemView(String icon,
                                                  String title,
                                                  String body,
                                                  String meta,
                                                  String href,
                                                  Long notificationId,
                                                  boolean unread,
                                                  boolean canMarkRead,
                                                  boolean canDismiss) {
        this.icon = icon;
        this.title = title;
        this.body = body;
        this.meta = meta;
        this.href = href;
        this.notificationId = notificationId;
        this.unread = unread;
        this.canMarkRead = canMarkRead;
        this.canDismiss = canDismiss;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getMeta() {
        return meta;
    }

    public String getHref() {
        return href;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public boolean isUnread() {
        return unread;
    }

    public boolean isCanMarkRead() {
        return canMarkRead;
    }

    public boolean isCanDismiss() {
        return canDismiss;
    }
}

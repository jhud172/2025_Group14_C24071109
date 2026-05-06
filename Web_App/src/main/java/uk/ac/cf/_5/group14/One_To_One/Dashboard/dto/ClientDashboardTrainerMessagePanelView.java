package uk.ac.cf._5.group14.One_To_One.Dashboard.dto;

import java.util.List;

public class ClientDashboardTrainerMessagePanelView {

    private final Long threadId;
    private final boolean canSend;
    private final boolean hasMore;
    private final String emptyTitle;
    private final String emptyBody;
    private final List<ClientDashboardTrainerMessageView> messages;

    public ClientDashboardTrainerMessagePanelView(Long threadId,
                                                  boolean canSend,
                                                  boolean hasMore,
                                                  String emptyTitle,
                                                  String emptyBody,
                                                  List<ClientDashboardTrainerMessageView> messages) {
        this.threadId = threadId;
        this.canSend = canSend;
        this.hasMore = hasMore;
        this.emptyTitle = emptyTitle;
        this.emptyBody = emptyBody;
        this.messages = messages;
    }

    public Long getThreadId() {
        return threadId;
    }

    public boolean isCanSend() {
        return canSend;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public String getEmptyTitle() {
        return emptyTitle;
    }

    public String getEmptyBody() {
        return emptyBody;
    }

    public List<ClientDashboardTrainerMessageView> getMessages() {
        return messages;
    }
}

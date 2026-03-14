package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

public class ClientDashboardTrainerMessageView {

    private final Long id;
    private final boolean mine;
    private final String body;
    private final String createdLabel;

    public ClientDashboardTrainerMessageView(Long id,
                                             boolean mine,
                                             String body,
                                             String createdLabel) {
        this.id = id;
        this.mine = mine;
        this.body = body;
        this.createdLabel = createdLabel;
    }

    public Long getId() {
        return id;
    }

    public boolean isMine() {
        return mine;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedLabel() {
        return createdLabel;
    }
}

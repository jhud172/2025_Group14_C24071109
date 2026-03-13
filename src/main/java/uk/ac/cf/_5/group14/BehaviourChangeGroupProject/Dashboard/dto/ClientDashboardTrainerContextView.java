package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

public class ClientDashboardTrainerContextView {

    private final String label;
    private final String title;
    private final String body;
    private final String meta;

    public ClientDashboardTrainerContextView(String label, String title, String body, String meta) {
        this.label = label;
        this.title = title;
        this.body = body;
        this.meta = meta;
    }

    public String getLabel() {
        return label;
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
}

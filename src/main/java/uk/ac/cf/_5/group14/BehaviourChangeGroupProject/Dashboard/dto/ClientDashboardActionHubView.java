package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto;

import java.util.List;

public class ClientDashboardActionHubView {

    private final String title;
    private final String detail;
    private final boolean hasAnyAction;
    private final String emptyTitle;
    private final String emptyBody;
    private final ClientDashboardActionUpcomingView upcoming;
    private final ClientDashboardActionCardView primaryCard;
    private final List<ClientDashboardActionCardView> secondaryCards;
    private final List<ClientDashboardActionCardView> allCards;

    public ClientDashboardActionHubView(String title,
                                        String detail,
                                        boolean hasAnyAction,
                                        String emptyTitle,
                                        String emptyBody,
                                        ClientDashboardActionUpcomingView upcoming,
                                        ClientDashboardActionCardView primaryCard,
                                        List<ClientDashboardActionCardView> secondaryCards,
                                        List<ClientDashboardActionCardView> allCards) {
        this.title = title;
        this.detail = detail;
        this.hasAnyAction = hasAnyAction;
        this.emptyTitle = emptyTitle;
        this.emptyBody = emptyBody;
        this.upcoming = upcoming;
        this.primaryCard = primaryCard;
        this.secondaryCards = secondaryCards;
        this.allCards = allCards;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isHasAnyAction() {
        return hasAnyAction;
    }

    public String getEmptyTitle() {
        return emptyTitle;
    }

    public String getEmptyBody() {
        return emptyBody;
    }

    public ClientDashboardActionUpcomingView getUpcoming() {
        return upcoming;
    }

    public ClientDashboardActionCardView getPrimaryCard() {
        return primaryCard;
    }

    public List<ClientDashboardActionCardView> getSecondaryCards() {
        return secondaryCards;
    }

    public List<ClientDashboardActionCardView> getAllCards() {
        return allCards;
    }
}

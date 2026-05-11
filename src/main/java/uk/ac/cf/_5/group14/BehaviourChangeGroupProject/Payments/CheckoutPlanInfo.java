package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Payments;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.PlatformBilling.PlatformPlan;

import java.util.List;

public class CheckoutPlanInfo {

    private final PlatformPlan plan;
    private final String displayName;
    private final String priceLabel;
    private final String badge;
    private final List<String> benefits;

    public CheckoutPlanInfo(PlatformPlan plan, String displayName, String priceLabel, String badge, List<String> benefits) {
        this.plan = plan;
        this.displayName = displayName;
        this.priceLabel = priceLabel;
        this.badge = badge;
        this.benefits = benefits;
    }

    public PlatformPlan getPlan() {
        return plan;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPriceLabel() {
        return priceLabel;
    }

    public String getBadge() {
        return badge;
    }

    public List<String> getBenefits() {
        return benefits;
    }
}

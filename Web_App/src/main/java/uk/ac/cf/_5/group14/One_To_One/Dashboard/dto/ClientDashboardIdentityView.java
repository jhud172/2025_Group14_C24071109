package uk.ac.cf._5.group14.One_To_One.Dashboard.dto;

public class ClientDashboardIdentityView {

    private final String displayName;
    private final String username;
    private final String imageUrl;
    private final String initial;
    private final String bannerTheme;
    private final String ringStyle;
    private final String cardBackStyle;
    private final boolean premium;
    private final String premiumTooltip;

    public ClientDashboardIdentityView(String displayName,
                                       String username,
                                       String imageUrl,
                                       String initial,
                                       String bannerTheme,
                                       String ringStyle,
                                       String cardBackStyle,
                                       boolean premium,
                                       String premiumTooltip) {
        this.displayName = displayName;
        this.username = username;
        this.imageUrl = imageUrl;
        this.initial = initial;
        this.bannerTheme = bannerTheme;
        this.ringStyle = ringStyle;
        this.cardBackStyle = cardBackStyle;
        this.premium = premium;
        this.premiumTooltip = premiumTooltip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getInitial() {
        return initial;
    }

    public String getBannerTheme() {
        return bannerTheme;
    }

    public String getRingStyle() {
        return ringStyle;
    }

    public String getCardBackStyle() {
        return cardBackStyle;
    }

    public boolean isPremium() {
        return premium;
    }

    public String getPremiumTooltip() {
        return premiumTooltip;
    }
}

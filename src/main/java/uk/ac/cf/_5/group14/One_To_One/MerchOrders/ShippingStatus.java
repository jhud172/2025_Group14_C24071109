package uk.ac.cf._5.group14.One_To_One.MerchOrders;

public enum ShippingStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("In Transit"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    FAILED_DELIVERY("Delivery Failed"),
    CANCELLED("Cancelled"),
    RETURNED("Returned");

    private final String displayName;

    ShippingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

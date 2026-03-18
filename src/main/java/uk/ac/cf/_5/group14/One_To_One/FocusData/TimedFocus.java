package uk.ac.cf._5.group14.One_To_One.FocusData;

public record TimedFocus(String label) {

    public static TimedFocus defaultFocus() {
        return new TimedFocus("General");
    }
}

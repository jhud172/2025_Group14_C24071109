package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData;

public record TimedFocus(String label) {

    public static TimedFocus defaultFocus() {
        return new TimedFocus("General");
    }
}

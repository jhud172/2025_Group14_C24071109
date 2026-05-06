package uk.ac.cf._5.group14.One_To_One.FocusData;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalTime;

@Service
public class TimedFocusService {

    private final Clock clock;

    public TimedFocusService(ObjectProvider<Clock> clockProvider) {
        this.clock = clockProvider.getIfAvailable(Clock::systemDefaultZone);
    }

    public TimedFocus getTimedFocus() {
        return getTimedFocus(LocalTime.now(clock));
    }

    public TimedFocus getTimedFocus(LocalTime now) {
        if (now == null) {
            return TimedFocus.defaultFocus();
        }

        LocalTime morningStart = LocalTime.of(5, 0);
        LocalTime middayStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(17, 0);
        LocalTime nightStart = LocalTime.of(21, 0);

        if (!now.isBefore(morningStart) && now.isBefore(middayStart)) {
            return new TimedFocus("Morning");
        }
        if (!now.isBefore(middayStart) && now.isBefore(eveningStart)) {
            return new TimedFocus("Midday");
        }
        if (!now.isBefore(eveningStart) && now.isBefore(nightStart)) {
            return new TimedFocus("Evening");
        }

        return new TimedFocus("Night");
    }
}

package uk.ac.cf._5.group14.One_To_One.FocusTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import uk.ac.cf._5.group14.One_To_One.FocusData.TimedFocusService;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimedFocusServiceTest {

    private static ObjectProvider<java.time.Clock> noClockProvided() {
        return new ObjectProvider<>() {
            @Override
            public java.time.Clock getObject(Object... args) {
                throw new UnsupportedOperationException();
            }

            @Override
            public java.time.Clock getObject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public java.time.Clock getIfAvailable() {
                return null;
            }

            @Override
            public java.time.Clock getIfAvailable(java.util.function.Supplier<java.time.Clock> defaultSupplier) {
                return defaultSupplier.get();
            }

            @Override
            public java.time.Clock getIfUnique() {
                return null;
            }

            @Override
            public java.time.Clock getIfUnique(java.util.function.Supplier<java.time.Clock> defaultSupplier) {
                return defaultSupplier.get();
            }

            @Override
            public java.util.stream.Stream<java.time.Clock> orderedStream() {
                return java.util.stream.Stream.empty();
            }

            @Override
            public java.util.stream.Stream<java.time.Clock> stream() {
                return java.util.stream.Stream.empty();
            }
        };
    }

    @Test
    void determinesMorningBetween0500And1159() {
        TimedFocusService service = new TimedFocusService(noClockProvided());
        assertThat(service.getTimedFocus(LocalTime.of(5, 0)).label()).isEqualTo("Morning");
        assertThat(service.getTimedFocus(LocalTime.of(11, 59)).label()).isEqualTo("Morning");
    }

    @Test
    void determinesMiddayBetween1200And1659() {
        TimedFocusService service = new TimedFocusService(noClockProvided());
        assertThat(service.getTimedFocus(LocalTime.of(12, 0)).label()).isEqualTo("Midday");
        assertThat(service.getTimedFocus(LocalTime.of(16, 59)).label()).isEqualTo("Midday");
    }

    @Test
    void determinesEveningBetween1700And2059() {
        TimedFocusService service = new TimedFocusService(noClockProvided());
        assertThat(service.getTimedFocus(LocalTime.of(17, 0)).label()).isEqualTo("Evening");
        assertThat(service.getTimedFocus(LocalTime.of(20, 59)).label()).isEqualTo("Evening");
    }

    @Test
    void determinesNightOtherwise() {
        TimedFocusService service = new TimedFocusService(noClockProvided());
        assertThat(service.getTimedFocus(LocalTime.of(0, 0)).label()).isEqualTo("Night");
        assertThat(service.getTimedFocus(LocalTime.of(4, 59)).label()).isEqualTo("Night");
        assertThat(service.getTimedFocus(LocalTime.of(21, 0)).label()).isEqualTo("Night");
        assertThat(service.getTimedFocus(LocalTime.of(23, 59)).label()).isEqualTo("Night");
    }
}

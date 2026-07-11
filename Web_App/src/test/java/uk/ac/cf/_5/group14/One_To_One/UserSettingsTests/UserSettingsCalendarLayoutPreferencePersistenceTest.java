package uk.ac.cf._5.group14.One_To_One.UserSettingsTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.UserSettings.CalendarTaskLayoutPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.CalendarTaskOrderingPreference;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettings;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsRepository;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.One_To_One.UserSettings.UserSettingsServiceImpl;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(UserSettingsServiceImpl.class)
class UserSettingsCalendarLayoutPreferencePersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserSettingsService userSettingsService;

    @Test
    void updateCalendarPreferencesPersistsLayout() {
        User user = userRepository.findByUsername("demo").orElseThrow();

        UserSettings settings = userSettingsService.getOrCreate(user);
        assertThat(settings.getCalendarTaskLayout()).isEqualTo(CalendarTaskLayoutPreference.COMBINED_LIST);

        userSettingsService.updateCalendarPreferences(user, CalendarTaskOrderingPreference.CHRONOLOGICAL, CalendarTaskLayoutPreference.SEPARATED_BY_CATEGORY);

        UserSettings reloaded = userSettingsRepository.findById(user.getId()).orElseThrow();
        assertThat(reloaded.getCalendarTaskLayout()).isEqualTo(CalendarTaskLayoutPreference.SEPARATED_BY_CATEGORY);
    }
}

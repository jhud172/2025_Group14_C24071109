package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettingsTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(UserSettingsServiceImpl.class)
class UserSettingsTrainerSharingPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserSettingsService userSettingsService;

    @Test
    void updateTrainerSharingPersistsPreferences() {
        User user = userRepository.findByUsername("user").orElseThrow();

        UserSettings settings = userSettingsService.getOrCreate(user);
        assertThat(settings.isShareRecoverySignals()).isFalse();
        assertThat(settings.isShareNutritionSignals()).isFalse();
        assertThat(settings.isShareSleepSignals()).isFalse();
        assertThat(settings.isShareFatigueSignals()).isFalse();
        assertThat(settings.isShareWeightTrend()).isFalse();

        userSettingsService.updateTrainerSharing(user, true, true, false, true, true);

        UserSettings reloaded = userSettingsRepository.findById(user.getId()).orElseThrow();
        assertThat(reloaded.isShareRecoverySignals()).isTrue();
        assertThat(reloaded.isShareNutritionSignals()).isTrue();
        assertThat(reloaded.isShareSleepSignals()).isFalse();
        assertThat(reloaded.isShareFatigueSignals()).isTrue();
        assertThat(reloaded.isShareWeightTrend()).isTrue();
    }
}

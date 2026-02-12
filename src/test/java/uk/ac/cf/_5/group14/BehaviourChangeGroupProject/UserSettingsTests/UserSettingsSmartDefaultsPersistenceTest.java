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
class UserSettingsSmartDefaultsPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserSettingsService userSettingsService;

    @Test
    void updateSmartDefaultsPersistsValues() {
        User user = userRepository.findByUsername("user").orElseThrow();

        userSettingsService.updateSmartDefaults(
                user,
                4,
                6,
                10,
                true,
                true,
                false,
                false,
                false,
                true,
                2200,
                160,
                200,
                70
        );

        UserSettings reloaded = userSettingsRepository.findById(user.getId()).orElseThrow();
        assertThat(reloaded.getDefaultSets()).isEqualTo(4);
        assertThat(reloaded.getDefaultRepMin()).isEqualTo(6);
        assertThat(reloaded.getDefaultRepMax()).isEqualTo(10);
        assertThat(reloaded.isPreferredEquipmentBodyweight()).isTrue();
        assertThat(reloaded.isPreferredEquipmentDumbbell()).isTrue();
        assertThat(reloaded.isPreferredEquipmentBarbell()).isFalse();
        assertThat(reloaded.isPreferredEquipmentMachine()).isFalse();
        assertThat(reloaded.isPreferredEquipmentBands()).isFalse();
        assertThat(reloaded.isPreferredEquipmentKettlebell()).isTrue();
        assertThat(reloaded.getMacroTargetCalories()).isEqualTo(2200);
        assertThat(reloaded.getMacroTargetProtein()).isEqualTo(160);
        assertThat(reloaded.getMacroTargetCarbs()).isEqualTo(200);
        assertThat(reloaded.getMacroTargetFat()).isEqualTo(70);
    }
}

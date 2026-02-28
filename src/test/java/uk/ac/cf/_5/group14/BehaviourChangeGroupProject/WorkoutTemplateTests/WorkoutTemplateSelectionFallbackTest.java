package uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplateTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettings;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserSettings.UserSettingsRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate.TemplateLayoutType;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate.WorkoutTemplate;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate.WorkoutUiTemplateRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.WorkoutTemplate.WorkoutTemplateServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutTemplateSelectionFallbackTest {

    @Mock
    private WorkoutUiTemplateRepository templateRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutTemplateServiceImpl templateService;

    private User testUser;
    private WorkoutTemplate globalDefault;
    private WorkoutTemplate userDefault;
    private WorkoutTemplate preferredTemplate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        globalDefault = new WorkoutTemplate();
        globalDefault.setId(10L);
        globalDefault.setName("Flow (Default)");
        globalDefault.setLayoutType(TemplateLayoutType.FLOW);
        globalDefault.setDefault(true);

        userDefault = new WorkoutTemplate();
        userDefault.setId(20L);
        userDefault.setName("My Template");
        userDefault.setLayoutType(TemplateLayoutType.PROFESSIONAL);
        userDefault.setDefault(true);

        preferredTemplate = new WorkoutTemplate();
        preferredTemplate.setId(30L);
        preferredTemplate.setName("Preferred Template");
        preferredTemplate.setLayoutType(TemplateLayoutType.CUSTOM);
    }

    @Test
    void getDefaultTemplateForUser_usesPreferredFromSettings_whenSet() {
        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(30L);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(templateRepository.findById(30L)).thenReturn(Optional.of(preferredTemplate));

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertEquals(30L, result.getId());
        assertEquals("Preferred Template", result.getName());
        verify(templateRepository, never()).findFirstByUserIsNullAndIsDefaultTrue();
    }

    @Test
    void getDefaultTemplateForUser_fallsBackToUserDefault_whenPreferredNotSet() {
        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(null);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(templateRepository.findFirstByUserAndIsDefaultTrue(testUser)).thenReturn(Optional.of(userDefault));

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertEquals(20L, result.getId());
        assertEquals("My Template", result.getName());
        verify(templateRepository, never()).findFirstByUserIsNullAndIsDefaultTrue();
    }

    @Test
    void getDefaultTemplateForUser_fallsBackToGlobalDefault_whenNoUserDefault() {
        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(null);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(templateRepository.findFirstByUserAndIsDefaultTrue(testUser)).thenReturn(Optional.empty());
        when(templateRepository.findFirstByUserIsNullAndIsDefaultTrue()).thenReturn(Optional.of(globalDefault));

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertEquals(10L, result.getId());
        assertEquals("Flow (Default)", result.getName());
    }

    @Test
    void getDefaultTemplateForUser_usesFirstGlobal_whenNoGlobalDefault() {
        WorkoutTemplate firstGlobal = new WorkoutTemplate();
        firstGlobal.setId(11L);
        firstGlobal.setName("Professional");
        firstGlobal.setLayoutType(TemplateLayoutType.PROFESSIONAL);

        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(null);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(templateRepository.findFirstByUserAndIsDefaultTrue(testUser)).thenReturn(Optional.empty());
        when(templateRepository.findFirstByUserIsNullAndIsDefaultTrue()).thenReturn(Optional.empty());
        when(templateRepository.findByUserIsNullOrderByName()).thenReturn(List.of(firstGlobal));

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertEquals(11L, result.getId());
        assertEquals("Professional", result.getName());
    }

    @Test
    void getDefaultTemplateForUser_returnsInMemoryFallback_whenNoGlobalsExist() {
        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(null);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(templateRepository.findFirstByUserAndIsDefaultTrue(testUser)).thenReturn(Optional.empty());
        when(templateRepository.findFirstByUserIsNullAndIsDefaultTrue()).thenReturn(Optional.empty());
        when(templateRepository.findByUserIsNullOrderByName()).thenReturn(Collections.emptyList());

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertNotNull(result);
        assertEquals("Flow (Default)", result.getName());
        assertEquals(TemplateLayoutType.FLOW, result.getLayoutType());
        assertTrue(result.isDefault());
    }

    @Test
    void getDefaultTemplateForUser_fallsBackToGlobalDefault_whenPreferredIdNotFound() {
        UserSettings settings = new UserSettings();
        settings.setPreferredWorkoutTemplateId(999L);

        when(userSettingsRepository.findById(1L)).thenReturn(Optional.of(settings));
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(templateRepository.findFirstByUserAndIsDefaultTrue(testUser)).thenReturn(Optional.empty());
        when(templateRepository.findFirstByUserIsNullAndIsDefaultTrue()).thenReturn(Optional.of(globalDefault));

        WorkoutTemplate result = templateService.getDefaultTemplateForUser(1L);

        assertEquals(10L, result.getId());
        assertEquals("Flow (Default)", result.getName());
    }
}

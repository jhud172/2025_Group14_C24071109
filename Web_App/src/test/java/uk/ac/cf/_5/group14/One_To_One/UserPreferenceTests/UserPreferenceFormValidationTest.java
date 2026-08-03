package uk.ac.cf._5.group14.One_To_One.UserPreferenceTests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference.UserPreferenceForm;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserPreferenceFormValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private UserPreferenceForm validForm() {
        UserPreferenceForm form = new UserPreferenceForm();
        form.setLanguage("en");
        form.setTheme("SYSTEM");
        form.setDefaultSets(3);
        form.setDefaultRepMin(8);
        form.setDefaultRepMax(12);
        return form;
    }

    @Test
    void validFormShouldHaveNoViolations() {
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(validForm());
        assertTrue(violations.isEmpty());
    }

    @Test
    void defaultSetsAboveMaxShouldFail() {
        UserPreferenceForm form = validForm();
        form.setDefaultSets(21);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("defaultSets")));
    }

    @Test
    void defaultSetsBelowMinShouldFail() {
        UserPreferenceForm form = validForm();
        form.setDefaultSets(0);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("defaultSets")));
    }

    @Test
    void defaultRepMinAboveMaxShouldFail() {
        UserPreferenceForm form = validForm();
        form.setDefaultRepMin(31);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("defaultRepMin")));
    }

    @Test
    void defaultRepMaxAboveMaxShouldFail() {
        UserPreferenceForm form = validForm();
        form.setDefaultRepMax(51);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("defaultRepMax")));
    }

    @Test
    void macroCaloriesAboveMaxShouldFail() {
        UserPreferenceForm form = validForm();
        form.setMacroTargetCalories(20001);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("macroTargetCalories")));
    }

    @Test
    void macroProteinBelowZeroShouldFail() {
        UserPreferenceForm form = validForm();
        form.setMacroTargetProtein(-1);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("macroTargetProtein")));
    }

    @Test
    void nullMacrosShouldBeValid() {
        UserPreferenceForm form = validForm();
        form.setMacroTargetCalories(null);
        form.setMacroTargetProtein(null);
        form.setMacroTargetCarbs(null);
        form.setMacroTargetFat(null);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidLanguageShouldFail() {
        UserPreferenceForm form = validForm();
        form.setLanguage("unknown");
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("language")));
    }

    @Test
    void everySupportedLanguageShouldBeValid() {
        for (String language : new String[]{"en", "cy", "es", "fr", "de", "it", "pt", "pl", "nl", "zh", "ja", "ko", "ar", "hi"}) {
            UserPreferenceForm form = validForm();
            form.setLanguage(language);
            assertTrue(validator.validate(form).isEmpty(), language + " should be accepted");
        }
    }

    @Test
    void invalidThemeShouldFail() {
        UserPreferenceForm form = validForm();
        form.setTheme("BLUE");
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("theme")));
    }

    @Test
    void nullDefaultsShouldBeValid() {
        UserPreferenceForm form = validForm();
        form.setDefaultSets(null);
        form.setDefaultRepMin(null);
        form.setDefaultRepMax(null);
        Set<ConstraintViolation<UserPreferenceForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty());
    }
}

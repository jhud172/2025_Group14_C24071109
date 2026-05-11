package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordConfirmable> {

    @Override
    public boolean isValid(PasswordConfirmable value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String password = value.getPassword();
        String confirm = value.getConfirmPassword();

        if (password == null || confirm == null) return true;

        boolean matches = password.equals(confirm);
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return matches;
    }
}

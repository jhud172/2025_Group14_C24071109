package uk.ac.cf._5.group14.One_To_One.Users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@PasswordMatches
public class ResetPasswordForm implements PasswordConfirmable {

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "Password must include upper, lower, number, and special character"
    )
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

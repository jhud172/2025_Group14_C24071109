package uk.ac.cf._5.group14.One_To_One.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForgotPasswordForm {

    @NotBlank(message = "Please enter a valid email address.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

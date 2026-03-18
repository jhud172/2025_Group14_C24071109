package uk.ac.cf._5.group14.One_To_One.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatches
public class GymSignupForm implements PasswordConfirmable {

    @NotBlank(message = "Please enter a gym name.")
    @Size(max = 120, message = "Gym name cannot exceed 120 characters")
    private String gymName;

    @NotBlank(message = "Please enter a valid admin email address.")
    @Email(message = "Please enter a valid admin email address.")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String adminEmail;

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    @NotBlank(message = "Please enter the gym address.")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @NotBlank(message = "Please enter the gym city.")
    @Size(max = 120, message = "City cannot exceed 120 characters")
    private String city;

    @NotBlank(message = "Please provide a contact name.")
    @Size(max = 120, message = "Contact name cannot exceed 120 characters")
    private String contactName;

    @NotBlank(message = "Please provide a contact number.")
    @Size(max = 40, message = "Contact number cannot exceed 40 characters")
    private String contactPhone;

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}

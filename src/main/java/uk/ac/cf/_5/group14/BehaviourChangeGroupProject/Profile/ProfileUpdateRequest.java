package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String phoneCountry = "GB";
    private String bio;
    private String dateOfBirth;
    private boolean removeProfileImage;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneCountry() {
        return phoneCountry != null ? phoneCountry : "GB";
    }

    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry != null ? phoneCountry : "GB";
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isRemoveProfileImage() {
        return removeProfileImage;
    }

    public void setRemoveProfileImage(boolean removeProfileImage) {
        this.removeProfileImage = removeProfileImage;
    }
}

package uk.ac.cf._5.group14.One_To_One.Security;

public class LoginRequestDetails {
    private final String loginType;
    private final String trainerCode;
    private final String gymSecretCode;

    public LoginRequestDetails(String loginType, String trainerCode, String gymSecretCode) {
        this.loginType = loginType;
        this.trainerCode = trainerCode;
        this.gymSecretCode = gymSecretCode;
    }

    public String getLoginType() {
        return loginType;
    }

    public String getTrainerCode() {
        return trainerCode;
    }

    public String getGymSecretCode() {
        return gymSecretCode;
    }
}

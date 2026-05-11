package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTool {

    public static void main(String[] args) {
        String raw = (args.length > 0 && args[0] != null && !args[0].isBlank()) ? args[0] : "Demo123!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(raw));
    }
}

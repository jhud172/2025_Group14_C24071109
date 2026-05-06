package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;

final class TestSecurityUsers {

    private TestSecurityUsers() {
    }

    static UserRequestPostProcessor client() {
        return SecurityMockMvcRequestPostProcessors.user("demo").roles("USER", "CLIENT");
    }

    static UserRequestPostProcessor trainer() {
        return SecurityMockMvcRequestPostProcessors.user("trainer_demo").roles("USER", "TRAINER");
    }

    static UserRequestPostProcessor gymAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("gymadmin_demo").roles("USER", "GYM_ADMIN");
    }

    static UserRequestPostProcessor platformAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("admin_demo").roles("USER", "PLATFORM_ADMIN");
    }

    static UserRequestPostProcessor superAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("superadmin_demo").roles("USER", "SUPER_ADMIN");
    }
}

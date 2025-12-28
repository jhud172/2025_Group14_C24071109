package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void userConstructorAndGettersTest() {
        User user = new User("test@example.com", "John", "Doe", "johndoe", "password123");

        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isSubscriptionStatus()).isFalse();
    }

    @Test
    void settersTest() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        assertThat(user.getFullName()).isEqualTo("Jane Smith");
    }
}

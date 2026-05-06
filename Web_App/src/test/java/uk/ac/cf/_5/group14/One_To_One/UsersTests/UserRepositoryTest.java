package uk.ac.cf._5.group14.One_To_One.UsersTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsernameToReturnUser() {

        User user = new User("alex@test.com", "Alex", "Smith", "alex123", "pass");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByUsername("alex123");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alex@test.com");
    }

    @Test
    void existsByEmailToReturnTrue() {
        User user = new User("sam@test.com", "Sam", "Smith", "sam123", "pass");
        entityManager.persist(user);

        boolean exists = userRepository.existsByEmail("sam@test.com");

        assertThat(exists).isTrue();
    }
}
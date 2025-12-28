//package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UsersTests;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
//import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    public void testFindUserByIdReturnsUser(){
//
//        String firstName = "John";
//        String lastName = "Johnson";
//
//        // Create a test user
//        User testUser = new User();
//        testUser.setFirst_name(firstName);
//        testUser.setLast_name(lastName);
//
//        // Save the test user, then call findUserById on the generated id
//        User persistedUser = entityManager.persist(testUser);
//        Optional<User> found = userRepository.findUserById(persistedUser.getId());
//
//        // Assertions
//        assertThat(found).isPresent();
//        assertThat(found.get().getFirst_name()).isEqualTo(firstName);
//        assertThat(found.get().getLast_name()).isEqualTo(lastName);
//
//    }
//
//}

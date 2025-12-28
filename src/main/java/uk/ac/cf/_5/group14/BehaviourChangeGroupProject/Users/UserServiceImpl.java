//package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public Optional<User> findUserById(Long id) {
//        return userRepository.findUserById(id);
//    }
//
//    @Override
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username);   // ⭐ WORKS
//    }
//
//    public User getAuthenticatedUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        return userRepository.findByUsername(username);
//    }
//}

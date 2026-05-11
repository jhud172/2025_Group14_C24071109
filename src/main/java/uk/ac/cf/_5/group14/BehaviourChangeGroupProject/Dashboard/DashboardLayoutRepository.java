package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

public interface DashboardLayoutRepository extends JpaRepository<DashboardLayout, Long> {
    List<DashboardLayout> findByUserOrderBySortIndexAsc(User user);
    Optional<DashboardLayout> findByUserAndModuleKey(User user, String moduleKey);
}

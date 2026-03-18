package uk.ac.cf._5.group14.One_To_One.QuickActions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuickActionDefinitionRepository extends JpaRepository<QuickActionDefinition, Long> {

    long countByUser(User user);

    long countByUserAndActiveTrue(User user);

    long countByUserAndType(User user, QuickActionType type);

    List<QuickActionDefinition> findByUserOrderBySortOrderAsc(User user);

    Optional<QuickActionDefinition> findByIdAndUser(Long id, User user);
}

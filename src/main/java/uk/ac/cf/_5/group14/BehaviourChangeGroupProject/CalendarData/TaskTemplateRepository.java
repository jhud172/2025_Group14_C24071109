package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

    List<TaskTemplate> findByUserOrderByTitleAsc(User user);

    List<TaskTemplate> findByUserAndFavouriteTrueOrderByTitleAsc(User user);

    @Query("select t from TaskTemplate t where t.user = :user and t.lastUsedAt is not null order by t.lastUsedAt desc")
    List<TaskTemplate> findRecentByUser(@Param("user") User user, Pageable pageable);

    Optional<TaskTemplate> findTop1ByUserAndTitleIgnoreCase(User user, String title);
}

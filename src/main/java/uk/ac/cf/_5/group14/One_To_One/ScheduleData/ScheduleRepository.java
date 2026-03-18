package uk.ac.cf._5.group14.One_To_One.ScheduleData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {

    List<Schedule> findByUser(User user);

    Optional<Schedule> findByUserAndNameIgnoreCase(User user, String name);
}

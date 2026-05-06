package uk.ac.cf._5.group14.One_To_One.HealthConditions;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserHealthConditionService {
    List<UserHealthCondition> getConditions(User user);
    List<UserHealthCondition> getConditionsByType(User user, HealthConditionType type);
    UserHealthCondition addPermanentCondition(User user, String name);
    UserHealthCondition addTimedCondition(User user, String name, LocalDate startDate, int durationDays);
    Optional<UserHealthCondition> findByIdForUser(Long id, User user);
    void deleteCondition(User user, Long id);
    UserHealthCondition markRecovered(User user, Long id);
    UserHealthCondition extendTimedCondition(User user, Long id, int extraDays);
    List<UserHealthCondition> findExpiredTimedConditions(LocalDate date);
    UserHealthCondition save(UserHealthCondition condition);
}

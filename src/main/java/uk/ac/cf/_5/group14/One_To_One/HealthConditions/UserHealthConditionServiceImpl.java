package uk.ac.cf._5.group14.One_To_One.HealthConditions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserHealthConditionServiceImpl implements UserHealthConditionService {

    private final UserHealthConditionRepository repository;

    public UserHealthConditionServiceImpl(UserHealthConditionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserHealthCondition> getConditions(User user) {
        if (user == null || user.getId() == null) {
            return List.of();
        }
        return repository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserHealthCondition> getConditionsByType(User user, HealthConditionType type) {
        if (user == null || user.getId() == null) {
            return List.of();
        }
        if (type == null) {
            return repository.findByUserIdOrderByCreatedAtDesc(user.getId());
        }
        return repository.findByUserIdAndConditionTypeOrderByCreatedAtDesc(user.getId(), type);
    }

    @Override
    @Transactional
    public UserHealthCondition addPermanentCondition(User user, String name) {
        if (user == null || user.getId() == null || name == null || name.isBlank()) {
            return null;
        }
        UserHealthCondition condition = new UserHealthCondition();
        condition.setUser(user);
        condition.setName(name.trim());
        condition.setConditionType(HealthConditionType.PERMANENT);
        condition.setStatus(HealthConditionStatus.ACTIVE);
        return repository.save(condition);
    }

    @Override
    @Transactional
    public UserHealthCondition addTimedCondition(User user, String name, LocalDate startDate, int durationDays) {
        if (user == null || user.getId() == null || name == null || name.isBlank()) {
            return null;
        }
        if (durationDays <= 0) {
            durationDays = 1;
        }
        LocalDate safeStart = startDate != null ? startDate : LocalDate.now();
        UserHealthCondition condition = new UserHealthCondition();
        condition.setUser(user);
        condition.setName(name.trim());
        condition.setConditionType(HealthConditionType.TIMED);
        condition.setStatus(HealthConditionStatus.ACTIVE);
        condition.setStartDate(safeStart);
        condition.setDurationDays(durationDays);
        condition.setEndDate(safeStart.plusDays(durationDays));
        return repository.save(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserHealthCondition> findByIdForUser(Long id, User user) {
        if (id == null || user == null || user.getId() == null) {
            return Optional.empty();
        }
        return repository.findById(id)
                .filter(condition -> condition.getUser() != null && user.getId().equals(condition.getUser().getId()));
    }

    @Override
    @Transactional
    public void deleteCondition(User user, Long id) {
        findByIdForUser(id, user).ifPresent(repository::delete);
    }

    @Override
    @Transactional
    public UserHealthCondition markRecovered(User user, Long id) {
        Optional<UserHealthCondition> condition = findByIdForUser(id, user);
        if (condition.isEmpty()) {
            return null;
        }
        UserHealthCondition existing = condition.get();
        existing.setStatus(HealthConditionStatus.RECOVERED);
        return repository.save(existing);
    }

    @Override
    @Transactional
    public UserHealthCondition extendTimedCondition(User user, Long id, int extraDays) {
        Optional<UserHealthCondition> condition = findByIdForUser(id, user);
        if (condition.isEmpty()) {
            return null;
        }
        UserHealthCondition existing = condition.get();
        if (existing.getConditionType() != HealthConditionType.TIMED) {
            return existing;
        }
        if (extraDays <= 0) {
            extraDays = 1;
        }
        LocalDate endDate = existing.getEndDate() != null ? existing.getEndDate() : LocalDate.now();
        existing.setEndDate(endDate.plusDays(extraDays));
        existing.setDurationDays(existing.getDurationDays() != null ? existing.getDurationDays() + extraDays : extraDays);
        existing.setStatus(HealthConditionStatus.ACTIVE);
        return repository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserHealthCondition> findExpiredTimedConditions(LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return repository.findByConditionTypeAndStatusAndEndDateBefore(HealthConditionType.TIMED, HealthConditionStatus.ACTIVE, target);
    }

    @Override
    @Transactional
    public UserHealthCondition save(UserHealthCondition condition) {
        return repository.save(condition);
    }
}

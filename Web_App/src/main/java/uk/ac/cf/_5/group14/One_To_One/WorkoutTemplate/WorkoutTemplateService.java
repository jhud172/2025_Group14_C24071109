package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;
import java.util.Optional;

public interface WorkoutTemplateService {

    WorkoutTemplate create(WorkoutTemplate template);

    WorkoutTemplate update(WorkoutTemplate template);

    void delete(Long id);

    Optional<WorkoutTemplate> findById(Long id);

    List<WorkoutTemplate> findByUser(User user);

    List<WorkoutTemplate> findGlobalTemplates();

    WorkoutTemplate getDefaultTemplateForUser(Long userId);
}

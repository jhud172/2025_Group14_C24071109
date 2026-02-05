package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class TaskTemplateService {

    private final TaskTemplateRepository repository;

    public TaskTemplateService(TaskTemplateRepository repository) {
        this.repository = repository;
    }

    public List<TaskTemplate> listAll(User user) {
        if (user == null) return Collections.emptyList();
        return repository.findByUserOrderByTitleAsc(user);
    }

    public List<TaskTemplate> listFavourites(User user) {
        if (user == null) return Collections.emptyList();
        return repository.findByUserAndFavouriteTrueOrderByTitleAsc(user);
    }

    public List<TaskTemplate> listRecents(User user, int limit) {
        if (user == null || limit <= 0) return Collections.emptyList();
        return repository.findRecentByUser(user, PageRequest.of(0, limit));
    }

    @Transactional
    public TaskTemplate upsertFromTask(User user, String title, String notes, boolean exercise) {
        if (user == null) return null;
        String t = title == null ? "" : title.trim();
        if (t.isBlank()) return null;

        TaskTemplate template = repository.findTop1ByUserAndTitleIgnoreCase(user, t)
                .orElseGet(() -> {
                    TaskTemplate nt = new TaskTemplate();
                    nt.setUser(user);
                    nt.setTitle(t);
                    return nt;
                });

        template.setNotes(notes);
        template.setExercise(exercise);
        template.setLastUsedAt(LocalDateTime.now());

        return repository.save(template);
    }
}

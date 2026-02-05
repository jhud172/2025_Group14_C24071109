package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class WorkoutBuilderServiceImpl implements WorkoutBuilderService {

    private final WorkoutTemplateRepository templateRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutSetLogRepository setLogRepository;

    public WorkoutBuilderServiceImpl(WorkoutTemplateRepository templateRepository,
                                     WorkoutSessionRepository sessionRepository,
                                     WorkoutSetLogRepository setLogRepository) {
        this.templateRepository = templateRepository;
        this.sessionRepository = sessionRepository;
        this.setLogRepository = setLogRepository;
    }

    @Override
    public List<WorkoutTemplate> listTemplates(User user) {
        return templateRepository.findByOwnerUserOrderByUpdatedAtDesc(user);
    }

    @Override
    public WorkoutTemplate createTemplate(User user, WorkoutTemplateForm form) {
        WorkoutTemplate template = new WorkoutTemplate();
        template.setOwnerUser(user);
        template.setOwnerRole(user.getRole() == null ? Role.CLIENT : user.getRole());
        applyTemplateForm(template, form);
        return templateRepository.save(template);
    }

    @Override
    public WorkoutTemplate updateTemplate(User user, Long templateId, WorkoutTemplateForm form) {
        WorkoutTemplate template = getTemplate(user, templateId);
        applyTemplateForm(template, form);
        return templateRepository.save(template);
    }

    @Override
    public WorkoutTemplate getTemplate(User user, Long templateId) {
        return templateRepository.findByIdAndOwnerUser(templateId, user)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
    }

    @Override
    public void deleteTemplate(User user, Long templateId) {
        WorkoutTemplate template = getTemplate(user, templateId);
        templateRepository.delete(template);
    }

    @Override
    public WorkoutSession startSession(User user, Long templateId) {
        WorkoutTemplate template = getTemplate(user, templateId);
        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setTemplate(template);
        session.setNameSnapshot(template.getName());
        session.setStartedAt(LocalDateTime.now());
        session.setCompleted(false);
        session.setTotalVolume(0.0);

        List<WorkoutExercise> ordered = new ArrayList<>(template.getExercises());
        ordered.sort(Comparator.comparingInt(WorkoutExercise::getOrderIndex));
        for (WorkoutExercise exercise : ordered) {
            int targetSets = Math.max(1, exercise.getSets());
            for (int i = 1; i <= targetSets; i++) {
                WorkoutSetLog log = new WorkoutSetLog();
                log.setSession(session);
                log.setExerciseName(exercise.getExerciseName());
                log.setExerciseOrder(exercise.getOrderIndex());
                log.setSetNumber(i);
                log.setTargetReps(exercise.getReps());
                log.setRestSeconds(exercise.getRestSeconds());
                log.setNotes(exercise.getNotes());
                session.getSetLogs().add(log);
            }
        }

        return sessionRepository.save(session);
    }

    @Override
    public WorkoutSession getSession(User user, Long sessionId) {
        return sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    @Override
    public WorkoutSetLog updateSet(User user, Long sessionId, Long setId, WorkoutSetUpdateRequest request) {
        WorkoutSession session = getSession(user, sessionId);
        WorkoutSetLog setLog = setLogRepository.findByIdAndSession(setId, session)
                .orElseThrow(() -> new IllegalArgumentException("Set not found"));

        if (request == null) {
            return setLog;
        }

        if (request.getWeight() != null) {
            setLog.setWeight(request.getWeight());
        }
        if (request.getReps() != null) {
            setLog.setReps(request.getReps());
        }
        if (request.getNotes() != null) {
            setLog.setNotes(request.getNotes());
        }
        if (request.getCompleted() != null) {
            setLog.setCompleted(request.getCompleted());
        }

        setLogRepository.save(setLog);
        rollupSession(session);
        return setLog;
    }

    public void applyTemplateForm(WorkoutTemplate template, WorkoutTemplateForm form) {
        template.setName(normalizeName(form.getName()));
        template.setDescription(trimToNull(form.getDescription()));

        template.getExercises().clear();
        int order = 0;
        if (form.getExercises() == null) {
            return;
        }
        for (WorkoutExerciseForm row : form.getExercises()) {
            String resolvedName = trimToNull(row.getExerciseName());
            if (resolvedName == null) {
                continue;
            }

            WorkoutExercise exercise = new WorkoutExercise();
            exercise.setTemplate(template);
            exercise.setExerciseName(resolvedName);
            exercise.setExerciseId(row.getExerciseId());
            exercise.setCustomExerciseId(row.getCustomExerciseId());
            exercise.setSets(safeInt(row.getSets(), 3));
            exercise.setReps(safeInt(row.getReps(), 10));
            exercise.setRestSeconds(safeInt(row.getRestSeconds(), 60));
            exercise.setNotes(trimToNull(row.getNotes()));
            exercise.setOrderIndex(order++);
            template.getExercises().add(exercise);
        }
    }

    private void rollupSession(WorkoutSession session) {
        boolean allCompleted = !session.getSetLogs().isEmpty() && session.getSetLogs().stream().allMatch(WorkoutSetLog::isCompleted);
        session.setCompleted(allCompleted);
        if (allCompleted && session.getCompletedAt() == null) {
            session.setCompletedAt(LocalDateTime.now());
        }

        double volume = 0.0;
        for (WorkoutSetLog log : session.getSetLogs()) {
            if (!log.isCompleted()) {
                continue;
            }
            if (log.getWeight() != null && log.getReps() != null) {
                volume += log.getWeight() * log.getReps();
            }
        }
        session.setTotalVolume(volume);
        sessionRepository.save(session);
    }

    private String normalizeName(String name) {
        String trimmed = trimToNull(name);
        return trimmed != null ? trimmed : "Untitled workout";
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private int safeInt(Integer value, int fallback) {
        if (value == null || value < 1) {
            return fallback;
        }
        return value;
    }

}

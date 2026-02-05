package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerAssignments;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Security.AccessGuard;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts.WorkoutTemplate;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts.WorkoutTemplateRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TrainerAssignmentServiceImpl implements TrainerAssignmentService {

    private final AssignedWorkoutRepository assignedWorkoutRepository;
    private final AssignedScheduleRepository assignedScheduleRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ScheduleService scheduleService;
    private final AccessGuard accessGuard;

    public TrainerAssignmentServiceImpl(AssignedWorkoutRepository assignedWorkoutRepository,
                                        AssignedScheduleRepository assignedScheduleRepository,
                                        WorkoutTemplateRepository workoutTemplateRepository,
                                        ScheduleService scheduleService,
                                        AccessGuard accessGuard) {
        this.assignedWorkoutRepository = assignedWorkoutRepository;
        this.assignedScheduleRepository = assignedScheduleRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.scheduleService = scheduleService;
        this.accessGuard = accessGuard;
    }

    @Override
    public AssignedWorkout assignWorkout(User trainer, Long clientId, Long templateId, String trainerNotes) {
        requireTrainer(trainer);
        accessGuard.requireTrainerAccessClient(trainer.getId(), clientId);

        WorkoutTemplate template = workoutTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Workout template not found"));
        if (!template.getOwnerUser().getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Workout template not owned by trainer");
        }

        AssignedWorkout assigned = new AssignedWorkout();
        assigned.setTrainerUserId(trainer.getId());
        assigned.setClientUserId(clientId);
        assigned.setWorkoutTemplate(template);
        assigned.setTrainerNotes(trainerNotes);
        return assignedWorkoutRepository.save(assigned);
    }

    @Override
    public AssignedSchedule assignSchedule(User trainer, Long clientId, Long scheduleId, String trainerNotes) {
        requireTrainer(trainer);
        accessGuard.requireTrainerAccessClient(trainer.getId(), clientId);

        Schedule schedule = scheduleService.findById(scheduleId);
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule not found");
        }
        if (schedule.getUser() == null || !schedule.getUser().getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Schedule not owned by trainer");
        }

        AssignedSchedule assigned = new AssignedSchedule();
        assigned.setTrainerUserId(trainer.getId());
        assigned.setClientUserId(clientId);
        assigned.setSchedule(schedule);
        assigned.setTrainerNotes(trainerNotes);
        return assignedScheduleRepository.save(assigned);
    }

    @Override
    public List<AssignedWorkout> listWorkoutsForClient(Long clientId) {
        return assignedWorkoutRepository.findByClientUserIdOrderByAssignedAtDesc(clientId);
    }

    @Override
    public List<AssignedSchedule> listSchedulesForClient(Long clientId) {
        return assignedScheduleRepository.findByClientUserIdOrderByAssignedAtDesc(clientId);
    }

    @Override
    public List<AssignedWorkout> listWorkoutsForTrainerClient(Long trainerId, Long clientId) {
        return assignedWorkoutRepository.findByTrainerUserIdAndClientUserIdOrderByAssignedAtDesc(trainerId, clientId);
    }

    @Override
    public List<AssignedSchedule> listSchedulesForTrainerClient(Long trainerId, Long clientId) {
        return assignedScheduleRepository.findByTrainerUserIdAndClientUserIdOrderByAssignedAtDesc(trainerId, clientId);
    }

    @Override
    public AssignedWorkout updateClientWorkout(Long clientId, Long assignmentId, String clientNotes, String clientFeedback, boolean completed) {
        AssignedWorkout assigned = assignedWorkoutRepository.findByIdAndClientUserId(assignmentId, clientId)
                .orElseThrow(() -> new AccessDeniedException("Assignment not found"));
        assigned.setClientNotes(trimToNull(clientNotes));
        assigned.setClientFeedback(trimToNull(clientFeedback));
        assigned.setCompleted(completed);
        if (completed && assigned.getCompletedAt() == null) {
            assigned.setCompletedAt(LocalDateTime.now());
        }
        if (!completed) {
            assigned.setCompletedAt(null);
        }
        return assignedWorkoutRepository.save(assigned);
    }

    @Override
    public TrainerAdherenceStats getClientAdherence(Long trainerId, Long clientId) {
        List<AssignedWorkout> workouts = listWorkoutsForTrainerClient(trainerId, clientId);
        int total = workouts.size();
        int completed = (int) workouts.stream().filter(AssignedWorkout::isCompleted).count();
        double rate = total == 0 ? 0.0 : (double) completed / (double) total;
        return new TrainerAdherenceStats(total, completed, rate);
    }

    private void requireTrainer(User trainer) {
        if (trainer == null || trainer.getRole() != Role.TRAINER) {
            throw new AccessDeniedException("Trainer role required");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}

package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;

import java.util.*;

@Service
public class TrainerLibraryService {

    public static final String ERROR_TRAINER_NOT_VERIFIED = "TRAINER_NOT_VERIFIED";

    private final TrainerLibraryExerciseRepository exerciseRepository;
    private final TrainerLibraryExerciseNoteRepository exerciseNoteRepository;
    private final TrainerLibraryWorkoutTemplateRepository workoutTemplateRepository;
    private final TrainerLibraryWorkoutItemRepository workoutItemRepository;
    private final TrainerLibraryWorkoutNoteRepository workoutNoteRepository;
    private final TrainerLibraryProgrammeTemplateRepository programmeTemplateRepository;
    private final TrainerLibraryProgrammeDayRepository programmeDayRepository;
    private final TrainerLibraryProgrammeNoteRepository programmeNoteRepository;
    private final TrainerLibrarySharedTemplateRepository sharedTemplateRepository;
    private final TrainerClientLinkRepository trainerClientLinkRepository;
    private final UserRepository userRepository;

    public TrainerLibraryService(TrainerLibraryExerciseRepository exerciseRepository,
                                TrainerLibraryExerciseNoteRepository exerciseNoteRepository,
                                TrainerLibraryWorkoutTemplateRepository workoutTemplateRepository,
                                TrainerLibraryWorkoutItemRepository workoutItemRepository,
                                TrainerLibraryWorkoutNoteRepository workoutNoteRepository,
                                TrainerLibraryProgrammeTemplateRepository programmeTemplateRepository,
                                TrainerLibraryProgrammeDayRepository programmeDayRepository,
                                TrainerLibraryProgrammeNoteRepository programmeNoteRepository,
                                TrainerLibrarySharedTemplateRepository sharedTemplateRepository,
                                TrainerClientLinkRepository trainerClientLinkRepository,
                                UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseNoteRepository = exerciseNoteRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.workoutItemRepository = workoutItemRepository;
        this.workoutNoteRepository = workoutNoteRepository;
        this.programmeTemplateRepository = programmeTemplateRepository;
        this.programmeDayRepository = programmeDayRepository;
        this.programmeNoteRepository = programmeNoteRepository;
        this.sharedTemplateRepository = sharedTemplateRepository;
        this.trainerClientLinkRepository = trainerClientLinkRepository;
        this.userRepository = userRepository;
    }

    public List<TrainerLibraryExercise> listExercises(Long trainerId) {
        requireVerifiedTrainer(trainerId);
        return exerciseRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }

    public List<TrainerLibraryWorkoutTemplate> listWorkouts(Long trainerId) {
        requireVerifiedTrainer(trainerId);
        return workoutTemplateRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }

    public List<TrainerLibraryProgrammeTemplate> listProgrammes(Long trainerId) {
        requireVerifiedTrainer(trainerId);
        return programmeTemplateRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId);
    }

    @Transactional
    public TrainerLibraryExercise createExercise(Long trainerId, TrainerLibraryExerciseForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryExercise exercise = new TrainerLibraryExercise(trainerId);
        applyExerciseForm(exercise, form);
        exercise = exerciseRepository.save(exercise);
        replaceExerciseNotes(exercise.getId(), form.getNotesText());
        return exercise;
    }

    @Transactional
    public TrainerLibraryExercise updateExercise(Long trainerId, Long exerciseId, TrainerLibraryExerciseForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryExercise exercise = exerciseRepository.findByIdAndTrainerId(exerciseId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        applyExerciseForm(exercise, form);
        exercise = exerciseRepository.save(exercise);
        replaceExerciseNotes(exercise.getId(), form.getNotesText());
        return exercise;
    }

    public TrainerLibraryExercise getExerciseOwned(Long trainerId, Long exerciseId) {
        requireVerifiedTrainer(trainerId);
        return exerciseRepository.findByIdAndTrainerId(exerciseId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
    }

    public List<TrainerLibraryExerciseNote> getExerciseNotes(Long exerciseId) {
        return exerciseNoteRepository.findByExerciseIdOrderByIdAsc(exerciseId);
    }

    @Transactional
    public void deleteExercise(Long trainerId, Long exerciseId) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryExercise exercise = exerciseRepository.findByIdAndTrainerId(exerciseId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        exerciseRepository.delete(exercise);
    }

    @Transactional
    public TrainerLibraryWorkoutTemplate createWorkout(Long trainerId, TrainerLibraryWorkoutTemplateForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryWorkoutTemplate wt = new TrainerLibraryWorkoutTemplate(trainerId);
        wt.setTitle(form.getTitle());
        wt.setSummary(form.getSummary());
        wt = workoutTemplateRepository.save(wt);
        replaceWorkoutNotes(wt.getId(), form.getNotesText());
        return wt;
    }

    @Transactional
    public TrainerLibraryWorkoutTemplate updateWorkout(Long trainerId, Long workoutId, TrainerLibraryWorkoutTemplateForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryWorkoutTemplate wt = workoutTemplateRepository.findByIdAndTrainerId(workoutId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        wt.setTitle(form.getTitle());
        wt.setSummary(form.getSummary());
        wt = workoutTemplateRepository.save(wt);
        replaceWorkoutNotes(wt.getId(), form.getNotesText());
        return wt;
    }

    public TrainerLibraryWorkoutTemplate getWorkoutOwned(Long trainerId, Long workoutId) {
        requireVerifiedTrainer(trainerId);
        return workoutTemplateRepository.findByIdAndTrainerId(workoutId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
    }

    public List<TrainerLibraryWorkoutItem> getWorkoutItems(Long workoutId) {
        return workoutItemRepository.findByWorkoutIdOrderByOrderIndexAsc(workoutId);
    }

    public List<TrainerLibraryWorkoutNote> getWorkoutNotes(Long workoutId) {
        return workoutNoteRepository.findByWorkoutIdOrderByIdAsc(workoutId);
    }

    @Transactional
    public TrainerLibraryWorkoutItem addWorkoutItem(Long trainerId, Long workoutId, TrainerLibraryWorkoutItemForm form) {
        requireVerifiedTrainer(trainerId);
        getWorkoutOwned(trainerId, workoutId);

        TrainerLibraryExercise exercise = exerciseRepository.findById(form.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        if (!Objects.equals(exercise.getTrainerId(), trainerId)) {
            throw new AccessDeniedException("Exercise not owned");
        }

        TrainerLibraryWorkoutItem item = new TrainerLibraryWorkoutItem();
        item.setWorkoutId(workoutId);
        item.setExerciseId(form.getExerciseId());
        item.setSets(form.getSets());
        item.setReps(form.getReps());
        item.setRestSeconds(form.getRestSeconds());
        item.setRpe(form.getRpe());
        item.setOrderIndex(form.getOrderIndex());
        return workoutItemRepository.save(item);
    }

    @Transactional
    public void deleteWorkoutItem(Long trainerId, Long workoutId, Long itemId) {
        requireVerifiedTrainer(trainerId);
        getWorkoutOwned(trainerId, workoutId);
        TrainerLibraryWorkoutItem item = workoutItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!Objects.equals(item.getWorkoutId(), workoutId)) {
            throw new AccessDeniedException("Wrong workout");
        }
        workoutItemRepository.delete(item);
    }

    @Transactional
    public void deleteWorkout(Long trainerId, Long workoutId) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryWorkoutTemplate wt = workoutTemplateRepository.findByIdAndTrainerId(workoutId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        workoutTemplateRepository.delete(wt);
    }

    @Transactional
    public TrainerLibraryProgrammeTemplate createProgramme(Long trainerId, TrainerLibraryProgrammeTemplateForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryProgrammeTemplate pt = new TrainerLibraryProgrammeTemplate(trainerId);
        pt.setTitle(form.getTitle());
        pt.setWeeks(form.getWeeks());
        pt = programmeTemplateRepository.save(pt);
        replaceProgrammeNotes(pt.getId(), form.getNotesText());
        return pt;
    }

    @Transactional
    public TrainerLibraryProgrammeTemplate updateProgramme(Long trainerId, Long programmeId, TrainerLibraryProgrammeTemplateForm form) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryProgrammeTemplate pt = programmeTemplateRepository.findByIdAndTrainerId(programmeId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        pt.setTitle(form.getTitle());
        pt.setWeeks(form.getWeeks());
        pt = programmeTemplateRepository.save(pt);
        replaceProgrammeNotes(pt.getId(), form.getNotesText());
        return pt;
    }

    public TrainerLibraryProgrammeTemplate getProgrammeOwned(Long trainerId, Long programmeId) {
        requireVerifiedTrainer(trainerId);
        return programmeTemplateRepository.findByIdAndTrainerId(programmeId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
    }

    public List<TrainerLibraryProgrammeDay> getProgrammeDays(Long programmeId) {
        return programmeDayRepository.findByProgrammeIdOrderByOrderIndexAsc(programmeId);
    }

    public List<TrainerLibraryProgrammeNote> getProgrammeNotes(Long programmeId) {
        return programmeNoteRepository.findByProgrammeIdOrderByIdAsc(programmeId);
    }

    @Transactional
    public TrainerLibraryProgrammeDay addProgrammeDay(Long trainerId, Long programmeId, TrainerLibraryProgrammeDayForm form) {
        requireVerifiedTrainer(trainerId);
        getProgrammeOwned(trainerId, programmeId);
        TrainerLibraryWorkoutTemplate workout = workoutTemplateRepository.findById(form.getWorkoutId())
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
        if (!Objects.equals(workout.getTrainerId(), trainerId)) {
            throw new AccessDeniedException("Workout not owned");
        }

        TrainerLibraryProgrammeDay day = new TrainerLibraryProgrammeDay();
        day.setProgrammeId(programmeId);
        day.setDayOfWeek(form.getDayOfWeek());
        day.setWorkoutId(form.getWorkoutId());
        day.setOrderIndex(form.getOrderIndex());
        return programmeDayRepository.save(day);
    }

    @Transactional
    public void deleteProgrammeDay(Long trainerId, Long programmeId, Long dayId) {
        requireVerifiedTrainer(trainerId);
        getProgrammeOwned(trainerId, programmeId);
        TrainerLibraryProgrammeDay day = programmeDayRepository.findById(dayId)
                .orElseThrow(() -> new IllegalArgumentException("Day not found"));
        if (!Objects.equals(day.getProgrammeId(), programmeId)) {
            throw new AccessDeniedException("Wrong programme");
        }
        programmeDayRepository.delete(day);
    }

    @Transactional
    public void deleteProgramme(Long trainerId, Long programmeId) {
        requireVerifiedTrainer(trainerId);
        TrainerLibraryProgrammeTemplate pt = programmeTemplateRepository.findByIdAndTrainerId(programmeId, trainerId)
                .orElseThrow(() -> new AccessDeniedException("Not owner"));
        programmeTemplateRepository.delete(pt);
    }

    @Transactional
    public void shareTemplate(Long trainerId, TrainerLibraryShareForm form) {
        requireVerifiedTrainer(trainerId);
        boolean activeLink = trainerClientLinkRepository
                .existsByTrainerUserIdAndClientUserIdAndStatus(trainerId, form.getClientId(), TrainerClientLinkStatus.ACTIVE);
        if (!activeLink) {
            throw new AccessDeniedException("Client not ACTIVE linked");
        }

        requireTemplateOwned(trainerId, form.getTemplateType(), form.getTemplateId());

        Optional<TrainerLibrarySharedTemplate> existing = sharedTemplateRepository
                .findByClientIdAndTrainerIdAndTemplateTypeAndTemplateId(form.getClientId(), trainerId, form.getTemplateType(), form.getTemplateId());
        if (existing.isPresent()) {
            return;
        }

        TrainerLibrarySharedTemplate shared = new TrainerLibrarySharedTemplate(trainerId, form.getClientId(), form.getTemplateType(), form.getTemplateId());
        sharedTemplateRepository.save(shared);
    }

    public Optional<TrainerClientLink> getActiveLinkForClient(Long clientId) {
        return trainerClientLinkRepository.findFirstByClientUserIdAndStatusOrderByUpdatedAtDesc(clientId, TrainerClientLinkStatus.ACTIVE);
    }

    public List<TrainerLibraryAssignedWorkoutView> getAssignedWorkoutsForClient(Long clientId) {
        Optional<TrainerClientLink> active = getActiveLinkForClient(clientId);
        if (active.isEmpty()) {
            return List.of();
        }
        Long trainerId = active.get().getTrainerUserId();

        List<TrainerLibrarySharedTemplate> shares = sharedTemplateRepository.findByClientIdAndTrainerIdOrderBySharedAtDesc(clientId, trainerId);
        List<Long> workoutIds = shares.stream()
                .filter(s -> s.getTemplateType() == TrainerLibraryTemplateType.WORKOUT)
                .map(TrainerLibrarySharedTemplate::getTemplateId)
                .distinct()
                .toList();

        List<TrainerLibraryAssignedWorkoutView> result = new ArrayList<>();
        for (Long workoutId : workoutIds) {
            TrainerLibraryWorkoutTemplate workout = workoutTemplateRepository.findByIdAndTrainerId(workoutId, trainerId)
                    .orElse(null);
            if (workout == null) {
                continue;
            }
            List<TrainerLibraryWorkoutItem> items = workoutItemRepository.findByWorkoutIdOrderByOrderIndexAsc(workoutId);
            List<TrainerLibraryWorkoutNote> notes = workoutNoteRepository.findByWorkoutIdOrderByIdAsc(workoutId);

            Set<Long> exerciseIds = new HashSet<>();
            for (TrainerLibraryWorkoutItem item : items) {
                exerciseIds.add(item.getExerciseId());
            }

            Map<Long, TrainerLibraryExercise> exercisesById = new HashMap<>();
            if (!exerciseIds.isEmpty()) {
                for (TrainerLibraryExercise ex : exerciseRepository.findAllById(exerciseIds)) {
                    exercisesById.put(ex.getId(), ex);
                }
            }

            result.add(new TrainerLibraryAssignedWorkoutView(workout, items, notes, exercisesById));
        }
        return result;
    }

    public List<TrainerLibraryAssignedProgrammeView> getAssignedProgrammesForClient(Long clientId) {
        Optional<TrainerClientLink> active = getActiveLinkForClient(clientId);
        if (active.isEmpty()) {
            return List.of();
        }
        Long trainerId = active.get().getTrainerUserId();

        List<TrainerLibrarySharedTemplate> shares = sharedTemplateRepository.findByClientIdAndTrainerIdOrderBySharedAtDesc(clientId, trainerId);
        List<Long> programmeIds = shares.stream()
                .filter(s -> s.getTemplateType() == TrainerLibraryTemplateType.PROGRAMME)
                .map(TrainerLibrarySharedTemplate::getTemplateId)
                .distinct()
                .toList();

        List<TrainerLibraryAssignedProgrammeView> result = new ArrayList<>();
        for (Long programmeId : programmeIds) {
            TrainerLibraryProgrammeTemplate programme = programmeTemplateRepository.findByIdAndTrainerId(programmeId, trainerId)
                    .orElse(null);
            if (programme == null) {
                continue;
            }
            List<TrainerLibraryProgrammeDay> days = programmeDayRepository.findByProgrammeIdOrderByOrderIndexAsc(programmeId);
            List<TrainerLibraryProgrammeNote> notes = programmeNoteRepository.findByProgrammeIdOrderByIdAsc(programmeId);

            Set<Long> workoutIds = new HashSet<>();
            for (TrainerLibraryProgrammeDay day : days) {
                workoutIds.add(day.getWorkoutId());
            }
            Map<Long, TrainerLibraryWorkoutTemplate> workoutsById = new HashMap<>();
            if (!workoutIds.isEmpty()) {
                for (TrainerLibraryWorkoutTemplate w : workoutTemplateRepository.findAllById(workoutIds)) {
                    workoutsById.put(w.getId(), w);
                }
            }

            result.add(new TrainerLibraryAssignedProgrammeView(programme, days, notes, workoutsById));
        }
        return result;
    }

    private void applyExerciseForm(TrainerLibraryExercise exercise, TrainerLibraryExerciseForm form) {
        exercise.setName(form.getName());
        exercise.setDescription(form.getDescription());
        exercise.setPrimaryMuscles(form.getPrimaryMuscles());
        exercise.setEquipment(form.getEquipment());
        exercise.setDifficulty(form.getDifficulty());
        exercise.setVideoUrl(form.getVideoUrl());
    }

    @Transactional
    protected void replaceExerciseNotes(Long exerciseId, String notesText) {
        exerciseNoteRepository.deleteByExerciseId(exerciseId);
        for (String line : splitNotes(notesText)) {
            exerciseNoteRepository.save(new TrainerLibraryExerciseNote(exerciseId, line));
        }
    }

    @Transactional
    protected void replaceWorkoutNotes(Long workoutId, String notesText) {
        workoutNoteRepository.deleteByWorkoutId(workoutId);
        for (String line : splitNotes(notesText)) {
            workoutNoteRepository.save(new TrainerLibraryWorkoutNote(workoutId, line));
        }
    }

    @Transactional
    protected void replaceProgrammeNotes(Long programmeId, String notesText) {
        programmeNoteRepository.deleteByProgrammeId(programmeId);
        for (String line : splitNotes(notesText)) {
            programmeNoteRepository.save(new TrainerLibraryProgrammeNote(programmeId, line));
        }
    }

    private List<String> splitNotes(String notesText) {
        if (notesText == null || notesText.isBlank()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        for (String raw : notesText.split("\\r?\\n")) {
            String line = raw.trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    private void requireTemplateOwned(Long trainerId, TrainerLibraryTemplateType type, Long templateId) {
        switch (type) {
            case EXERCISE -> {
                if (exerciseRepository.findByIdAndTrainerId(templateId, trainerId).isEmpty()) {
                    throw new AccessDeniedException("Not owner");
                }
            }
            case WORKOUT -> {
                if (workoutTemplateRepository.findByIdAndTrainerId(templateId, trainerId).isEmpty()) {
                    throw new AccessDeniedException("Not owner");
                }
            }
            case PROGRAMME -> {
                if (programmeTemplateRepository.findByIdAndTrainerId(templateId, trainerId).isEmpty()) {
                    throw new AccessDeniedException("Not owner");
                }
            }
            default -> throw new IllegalArgumentException("Unknown type");
        }
    }

    private void requireVerifiedTrainer(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new AccessDeniedException("Trainer not found"));
        if (trainer.getRole() != Role.TRAINER) {
            throw new AccessDeniedException("User is not a trainer");
        }
        if (!trainer.isTrainerVerified() || !trainer.isEnabled()) {
            throw new AccessDeniedException(ERROR_TRAINER_NOT_VERIFIED);
        }
    }
}

package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleApplied;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ApplyScheduleActionHandler implements CoachActionHandler<ApplyScheduleActionPayload> {

    private static final int MAX_WEEKS = 12;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.UK);

    private final ScheduleRepository scheduleRepository;
    private final ScheduleOccurrenceService scheduleOccurrenceService;
    private final ScheduleAppliedRepository scheduleAppliedRepository;
    private final TrainerClientLinkRepository trainerClientLinkRepository;

    public ApplyScheduleActionHandler(ScheduleRepository scheduleRepository,
                                      ScheduleOccurrenceService scheduleOccurrenceService,
                                      ScheduleAppliedRepository scheduleAppliedRepository,
                                      TrainerClientLinkRepository trainerClientLinkRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleOccurrenceService = scheduleOccurrenceService;
        this.scheduleAppliedRepository = scheduleAppliedRepository;
        this.trainerClientLinkRepository = trainerClientLinkRepository;
    }

    @Override
    public CoachActionType type() {
        return CoachActionType.APPLY_SCHEDULE;
    }

    @Override
    public List<String> validate(ApplyScheduleActionPayload payload, User user) {
        List<String> errors = new ArrayList<>();
        if (user == null || user.getId() == null) {
            errors.add("User is required.");
        }
        if (payload == null) {
            errors.add("Missing schedule details.");
            return errors;
        }
        if (payload.scheduleName() == null || payload.scheduleName().isBlank()) {
            errors.add("Schedule name is required.");
        }
        if (payload.startDate() == null) {
            errors.add("Start date is required.");
        }
        if (payload.durationWeeks() < 1 || payload.durationWeeks() > MAX_WEEKS) {
            errors.add("Duration must be between 1 and " + MAX_WEEKS + " weeks.");
        }
        return errors;
    }

    @Override
    @Transactional
    public CoachActionExecution execute(ApplyScheduleActionPayload payload, User user) {
        String name = payload.scheduleName().trim();
        Schedule schedule = findAccessibleSchedule(user, name);
        if (schedule == null) {
            return new CoachActionExecution(false, "", "Schedule not found or not accessible.");
        }

        LocalDate start = payload.startDate();
        LocalDate end = start.plusWeeks(payload.durationWeeks()).minusDays(1);

        ScheduleApplied applied = new ScheduleApplied();
        applied.setSchedule(schedule);
        applied.setUser(user);
        applied.setDateApplied(start);
        applied.setDurationWeeks(payload.durationWeeks());
        applied.setShownOnCalendar(true);
        applied.setRequiresLogging(false);
        scheduleAppliedRepository.save(applied);

        scheduleOccurrenceService.generateOccurrencesForSchedule(schedule, user, start, end, 1);

        String reply = "Schedule applied starting " + start.format(DATE_FMT) + " for " + payload.durationWeeks() + " week" + (payload.durationWeeks() == 1 ? "" : "s") + ". Want to adjust anything?";
        return new CoachActionExecution(true, reply, null);
    }

    private Schedule findAccessibleSchedule(User user, String name) {
        if (user == null || name == null || name.isBlank()) {
            return null;
        }
        Schedule own = scheduleRepository.findByUserAndNameIgnoreCase(user, name).orElse(null);
        if (own != null) {
            return own;
        }
        Optional<TrainerClientLink> activeLink = trainerClientLinkRepository
                .findFirstByClientUserIdAndStatusOrderByUpdatedAtDesc(user.getId(), TrainerClientLinkStatus.ACTIVE);
        if (activeLink.isEmpty()) {
            return null;
        }
        Long trainerId = activeLink.get().getTrainerUserId();
        User trainer = new User();
        trainer.setId(trainerId);
        return scheduleRepository.findByUserAndNameIgnoreCase(trainer, name).orElse(null);
    }
}

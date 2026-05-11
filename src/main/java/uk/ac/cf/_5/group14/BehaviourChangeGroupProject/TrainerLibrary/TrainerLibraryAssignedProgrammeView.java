package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import java.util.List;
import java.util.Map;

public class TrainerLibraryAssignedProgrammeView {

    private final TrainerLibraryProgrammeTemplate programme;
    private final List<TrainerLibraryProgrammeDay> days;
    private final List<TrainerLibraryProgrammeNote> notes;
    private final Map<Long, TrainerLibraryWorkoutTemplate> workoutsById;

    public TrainerLibraryAssignedProgrammeView(TrainerLibraryProgrammeTemplate programme,
                                              List<TrainerLibraryProgrammeDay> days,
                                              List<TrainerLibraryProgrammeNote> notes,
                                              Map<Long, TrainerLibraryWorkoutTemplate> workoutsById) {
        this.programme = programme;
        this.days = days;
        this.notes = notes;
        this.workoutsById = workoutsById;
    }

    public TrainerLibraryProgrammeTemplate getProgramme() {
        return programme;
    }

    public List<TrainerLibraryProgrammeDay> getDays() {
        return days;
    }

    public List<TrainerLibraryProgrammeNote> getNotes() {
        return notes;
    }

    public Map<Long, TrainerLibraryWorkoutTemplate> getWorkoutsById() {
        return workoutsById;
    }
}

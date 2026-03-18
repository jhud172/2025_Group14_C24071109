package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLibraryProgrammeNoteRepository extends JpaRepository<TrainerLibraryProgrammeNote, Long> {
    List<TrainerLibraryProgrammeNote> findByProgrammeIdOrderByIdAsc(Long programmeId);

    void deleteByProgrammeId(Long programmeId);
}

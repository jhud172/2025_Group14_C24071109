package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLibraryProgrammeDayRepository extends JpaRepository<TrainerLibraryProgrammeDay, Long> {
    List<TrainerLibraryProgrammeDay> findByProgrammeIdOrderByOrderIndexAsc(Long programmeId);

    void deleteByProgrammeId(Long programmeId);
}

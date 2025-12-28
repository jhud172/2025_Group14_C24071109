package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    @Override
    List<Preference> findAll();

    @Override
    Optional<Preference> findById(Long id);

    @Override
    List<Preference> findAllById(Iterable<Long> longs);
}

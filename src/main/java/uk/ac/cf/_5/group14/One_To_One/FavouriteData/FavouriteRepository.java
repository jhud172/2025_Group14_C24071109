package uk.ac.cf._5.group14.One_To_One.FavouriteData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FavouriteRepository extends CrudRepository<Favourite, Long> {
    
    Optional<Favourite> findByUserIdAndExerciseId(Long userId, Long exerciseId);
    
    @Transactional
    void deleteByUserIdAndExerciseId(Long userId, Long exerciseId);
}

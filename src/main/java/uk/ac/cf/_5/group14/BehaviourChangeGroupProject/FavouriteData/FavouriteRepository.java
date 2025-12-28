package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData.Favourite;

@Repository
public interface FavouriteRepository extends CrudRepository<Favourite, Long> {

}

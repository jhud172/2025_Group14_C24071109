package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData;

import java.util.List;

public interface FavouriteService {
    List<Favourite> getFavouritesByUser(Long userId);
    void saveFavourite(Favourite favourite);
    void deleteFavourite(Long id);
}

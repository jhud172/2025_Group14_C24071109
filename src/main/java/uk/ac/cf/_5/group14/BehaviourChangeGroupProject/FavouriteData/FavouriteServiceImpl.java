package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavouriteServiceImpl implements FavouriteService {

    @Autowired
    private FavouriteRepository repo;

    @Override
    public List<Favourite> getFavouritesByUser(Long userId) {
        List<Favourite> list = new ArrayList<>();
        repo.findAll().forEach(fav -> {
            if (fav.getUserId().equals(userId)) {
                list.add(fav);
            }
        });
        return list;
    }

    @Override
    public void saveFavourite(Favourite favourite) {
        repo.save(favourite);
    }

    @Override
    public void deleteFavourite(Long id) {
        repo.deleteById(id);
    }
}

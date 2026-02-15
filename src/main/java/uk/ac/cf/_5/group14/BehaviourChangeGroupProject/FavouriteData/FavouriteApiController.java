package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FavouriteData;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteApiController {

    private final FavouriteService favouriteService;
    private final FavouriteRepository favouriteRepository;
    private final AuthHelper authHelper;

    public FavouriteApiController(FavouriteService favouriteService, 
                                  FavouriteRepository favouriteRepository,
                                  AuthHelper authHelper) {
        this.favouriteService = favouriteService;
        this.favouriteRepository = favouriteRepository;
        this.authHelper = authHelper;
    }

    @GetMapping
    public ResponseEntity<List<Favourite>> getFavourites() {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(favouriteService.getFavouritesByUser(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Favourite> addFavourite(@RequestBody Map<String, Long> body) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        Long exerciseId = body.get("exerciseId");
        Long customExerciseId = body.get("customExerciseId");

        if (exerciseId == null && customExerciseId == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if already favourited
        List<Favourite> existing = favouriteService.getFavouritesByUser(user.getId());
        boolean alreadyExists = existing.stream().anyMatch(f -> 
            (exerciseId != null && exerciseId.equals(f.getExerciseId())) ||
            (customExerciseId != null && customExerciseId.equals(f.getCustomExerciseId()))
        );

        if (alreadyExists) {
            return ResponseEntity.ok().build();
        }

        Favourite favourite = new Favourite();
        favourite.setUserId(user.getId());
        favourite.setExerciseId(exerciseId);
        favourite.setCustomExerciseId(customExerciseId);
        favouriteService.saveFavourite(favourite);

        return ResponseEntity.ok(favourite);
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> removeFavourite(@PathVariable Long exerciseId) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Find and delete the favourite
        List<Favourite> favourites = favouriteService.getFavouritesByUser(user.getId());
        favourites.stream()
            .filter(f -> exerciseId.equals(f.getExerciseId()))
            .findFirst()
            .ifPresent(f -> favouriteService.deleteFavourite(f.getId()));

        return ResponseEntity.ok().build();
    }
}

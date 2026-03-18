package uk.ac.cf._5.group14.One_To_One.Level;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

/**
 * Business logic for managing user points and levels.
 */
public interface LevelService {
    LevelProgress getProgress(User user);

    void addPoints(User user, int points);

    List<LevelProgress> getLeaderboard();
}

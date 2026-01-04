package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

/**
 * Implementation of point/level management.
 */
@Service
@Transactional
public class LevelServiceImpl implements LevelService {

    private final LevelProgressRepository progressRepository;

    public LevelServiceImpl(LevelProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public LevelProgress getProgress(User user) {
        return progressRepository.findByUser(user)
                .orElseGet(() -> {
                    LevelProgress lp = new LevelProgress();
                    lp.setUser(user);
                    return progressRepository.save(lp);
                });
    }

    @Override
    public void addPoints(User user, int points) {
        LevelProgress progress = getProgress(user);
        progress.setPoints(progress.getPoints() + points);
        int newLevel = 1 + (progress.getPoints() / 100);
        progress.setLevel(newLevel);
        progressRepository.save(progress);
    }

    @Override
    public List<LevelProgress> getLeaderboard() {
        return progressRepository.findAllByOrderByPointsDesc();
    }
}

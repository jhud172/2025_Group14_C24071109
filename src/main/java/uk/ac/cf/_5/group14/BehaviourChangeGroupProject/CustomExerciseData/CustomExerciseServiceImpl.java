package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CustomExerciseServiceImpl implements CustomExerciseService {

    @Autowired
    private CustomExerciseRepository repo;

    @Override
    public List<CustomExercise> getCustomExercisesByUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return repo.findByUserIdOrderByNameAsc(userId);
    }

    @Override
    public CustomExercise getCustomExerciseById(Long id, Long userId) {
        if (id == null || userId == null) {
            return null;
        }
        return repo.findByIdAndUserId(id, userId).orElse(null);
    }

    @Override
    public CustomExercise saveCustomExercise(CustomExercise exercise) {
        return repo.save(exercise);
    }

    @Override
    public CustomExercise updateCustomExercise(Long userId, Long id, CustomExerciseRequest request) {
        CustomExercise exercise = getCustomExerciseById(id, userId);
        if (exercise == null) {
            return null;
        }
        applyRequest(exercise, request);
        return repo.save(exercise);
    }

    @Override
    public void deleteCustomExercise(Long userId, Long id) {
        CustomExercise exercise = getCustomExerciseById(id, userId);
        if (exercise == null) {
            return;
        }
        repo.delete(exercise);
    }

    private void applyRequest(CustomExercise exercise, CustomExerciseRequest request) {
        if (exercise == null || request == null) {
            return;
        }
        exercise.setName(request.name());
        exercise.setDescription(request.description());
        exercise.setHowTo(request.howTo());
        exercise.setVideoUrl(request.videoUrl());
        exercise.setColorTag(request.colorTag());
    }
}

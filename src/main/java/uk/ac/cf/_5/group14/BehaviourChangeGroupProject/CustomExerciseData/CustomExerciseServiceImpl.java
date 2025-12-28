package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomExerciseServiceImpl implements CustomExerciseService {

    @Autowired
    private CustomExerciseRepository repo;

    @Override
    public List<CustomExercise> getCustomExercisesByUser(Long userId) {
        List<CustomExercise> list = new ArrayList<>();
        repo.findAll().forEach(ex -> {
            if (ex.getUserId().equals(userId)) {
                list.add(ex);
            }
        });
        return list;
    }

    @Override
    public CustomExercise getCustomExerciseById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void saveCustomExercise(CustomExercise exercise) {
        repo.save(exercise);
    }
}

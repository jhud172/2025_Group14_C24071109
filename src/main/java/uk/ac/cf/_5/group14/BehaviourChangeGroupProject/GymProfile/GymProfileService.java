package uk.ac.cf._5.group14.BehaviourChangeGroupProject.GymProfile;

import org.springframework.stereotype.Service;

@Service
public class GymProfileService {

    private final GymProfileRepository repository;

    public GymProfileService(GymProfileRepository repository) {
        this.repository = repository;
    }

    public GymProfile saveProfile(GymProfile profile) {
        return repository.save(profile);
    }
}

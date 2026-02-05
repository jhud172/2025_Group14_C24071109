package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile;

import java.util.Optional;

import org.springframework.stereotype.Service;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Service
public class TrainerProfileService {

    private final TrainerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SocialLinkValidator validator;

    public TrainerProfileService(TrainerProfileRepository profileRepository,
                                UserRepository userRepository,
                                SocialLinkValidator validator) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    /**
     * Get or create a trainer profile for a user.
     */
    public TrainerProfile getOrCreateProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.TRAINER && user.getRole() != Role.PLATFORM_ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Only trainers can have profiles");
        }

        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    TrainerProfile profile = new TrainerProfile(userId);
                    return profileRepository.save(profile);
                });
    }

    /**
     * Get trainer profile by user ID (returns empty if not found).
     */
    public Optional<TrainerProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    /**
     * Update trainer profile with validation.
     */
    public TrainerProfile updateProfile(Long userId, TrainerProfile updatedProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.TRAINER && user.getRole() != Role.PLATFORM_ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Only trainers can update profiles");
        }

        // Validate URLs
        String validationError = validator.validateProfile(updatedProfile);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        TrainerProfile profile = getOrCreateProfile(userId);
        
        // Update fields
        profile.setBio(updatedProfile.getBio());
        profile.setSpecializations(updatedProfile.getSpecializations());
        profile.setLocation(updatedProfile.getLocation());
        profile.setPrimaryGym(updatedProfile.getPrimaryGym());
        profile.setPricePerSession(updatedProfile.getPricePerSession());
        
        // Update social links
        profile.setInstagramUrl(updatedProfile.getInstagramUrl());
        profile.setTiktokUrl(updatedProfile.getTiktokUrl());
        profile.setYoutubeUrl(updatedProfile.getYoutubeUrl());
        profile.setLinkedInUrl(updatedProfile.getLinkedInUrl());
        profile.setWebsiteUrl(updatedProfile.getWebsiteUrl());
        
        // Update visibility flags
        profile.setShowInstagram(updatedProfile.getShowInstagram());
        profile.setShowTikTok(updatedProfile.getShowTikTok());
        profile.setShowYouTube(updatedProfile.getShowYouTube());
        profile.setShowLinkedIn(updatedProfile.getShowLinkedIn());
        profile.setShowWebsite(updatedProfile.getShowWebsite());

        return profileRepository.save(profile);
    }
}

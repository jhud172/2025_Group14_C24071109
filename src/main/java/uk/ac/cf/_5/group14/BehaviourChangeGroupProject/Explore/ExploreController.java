package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Explore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Reviews.TrainerReviewService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile.TrainerProfile;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Controller
public class ExploreController {

    private static final List<String> TAG_OPTIONS = List.of(
            "Strength",
            "Hypertrophy",
            "Fat Loss",
            "Rehab-friendly",
            "Mobility",
            "Sports Performance",
            "Nutrition",
            "Endurance"
    );

    private final UserRepository userRepository;
    private final TrainerProfileService trainerProfileService;
    private final TrainerReviewService trainerReviewService;

    public ExploreController(UserRepository userRepository,
                             TrainerProfileService trainerProfileService,
                             TrainerReviewService trainerReviewService) {
        this.userRepository = userRepository;
        this.trainerProfileService = trainerProfileService;
        this.trainerReviewService = trainerReviewService;
    }

    @GetMapping("/explore")
    public String explore(@RequestParam(value = "location", required = false) String location,
                          @RequestParam(value = "tags", required = false) List<String> tags,
                          @RequestParam(value = "minPrice", required = false) Integer minPrice,
                          @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                          @RequestParam(value = "minRating", required = false) Double minRating,
                          @RequestParam(value = "sort", required = false, defaultValue = "recommended") String sort,
                          Model model) {
        List<User> verifiedTrainers = userRepository.findByRoleAndTrainerVerifiedTrueAndEnabledTrue(Role.TRAINER);
        List<TrainerDirectoryCard> cards = verifiedTrainers.stream()
                .map(trainer -> buildCard(trainer))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String normalizedLocation = normalize(location);
        Set<String> selectedTags = normalizeTags(tags);

        List<TrainerDirectoryCard> filtered = cards.stream()
                .filter(card -> matchesLocation(card, normalizedLocation))
                .filter(card -> matchesTags(card, selectedTags))
                .filter(card -> matchesPrice(card, minPrice, maxPrice))
                .filter(card -> matchesRating(card, minRating))
                .sorted(resolveSort(sort))
                .collect(Collectors.toList());

        model.addAttribute("trainers", filtered);
        model.addAttribute("resultCount", filtered.size());
        model.addAttribute("availableTags", TAG_OPTIONS);
        model.addAttribute("selectedLocation", location == null ? "" : location.trim());
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("selectedMinRating", minRating);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("hasFilters", hasFilters(location, selectedTags, minPrice, maxPrice, minRating));
        return "explore/index";
    }

    private TrainerDirectoryCard buildCard(User trainer) {
        TrainerProfile profile = trainerProfileService.getProfileByUserId(trainer.getId()).orElse(null);
        double averageRating = trainerReviewService.getAverageRating(trainer.getId());
        long reviewCount = trainerReviewService.getReviewCount(trainer.getId());
        List<String> tags = splitTags(profile != null ? profile.getSpecializations() : null);
        return new TrainerDirectoryCard(trainer, profile, averageRating, reviewCount, tags);
    }

    private Comparator<TrainerDirectoryCard> resolveSort(String sort) {
        String normalized = sort == null ? "recommended" : sort.trim().toLowerCase(Locale.ROOT);
        Comparator<TrainerDirectoryCard> byName = Comparator.comparing(card -> card.getTrainer().getFullName(), String.CASE_INSENSITIVE_ORDER);
        switch (normalized) {
            case "rating":
                return Comparator.comparingDouble(TrainerDirectoryCard::getAverageRating).reversed()
                        .thenComparing(TrainerDirectoryCard::getReviewCount, Comparator.reverseOrder())
                        .thenComparing(byName);
            case "price_low":
                return Comparator.comparing(TrainerDirectoryCard::getPricePerSession,
                                Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(byName);
            case "price_high":
                return Comparator.comparing(TrainerDirectoryCard::getPricePerSession,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(byName);
            default:
                return Comparator.comparingDouble(TrainerDirectoryCard::getAverageRating).reversed()
                        .thenComparing(TrainerDirectoryCard::getReviewCount, Comparator.reverseOrder())
                        .thenComparing(byName);
        }
    }

    private boolean matchesLocation(TrainerDirectoryCard card, String normalizedLocation) {
        if (normalizedLocation == null || normalizedLocation.isBlank()) {
            return true;
        }
        String location = normalize(card.getLocation());
        return location != null && location.contains(normalizedLocation);
    }

    private boolean matchesTags(TrainerDirectoryCard card, Set<String> selectedTags) {
        if (selectedTags == null || selectedTags.isEmpty()) {
            return true;
        }
        Set<String> trainerTags = card.getTags().stream()
                .map(this::normalize)
                .filter(tag -> tag != null && !tag.isBlank())
                .collect(Collectors.toSet());
        if (trainerTags.isEmpty()) {
            return false;
        }
        return selectedTags.stream().anyMatch(trainerTags::contains);
    }

    private boolean matchesPrice(TrainerDirectoryCard card, Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return true;
        }
        Integer price = card.getPricePerSession();
        if (price == null) {
            return false;
        }
        if (minPrice != null && price < minPrice) {
            return false;
        }
        if (maxPrice != null && price > maxPrice) {
            return false;
        }
        return true;
    }

    private boolean matchesRating(TrainerDirectoryCard card, Double minRating) {
        if (minRating == null || minRating <= 0) {
            return true;
        }
        return card.getAverageRating() >= minRating;
    }

    private boolean hasFilters(String location, Set<String> tags, Integer minPrice, Integer maxPrice, Double minRating) {
        return (location != null && !location.trim().isEmpty())
                || (tags != null && !tags.isEmpty())
                || minPrice != null
                || maxPrice != null
                || (minRating != null && minRating > 0);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    private Set<String> normalizeTags(List<String> tags) {
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(this::normalize)
                .filter(tag -> tag != null && !tag.isBlank())
                .collect(Collectors.toSet());
    }

    private List<String> splitTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .collect(Collectors.toList());
    }

    public static class TrainerDirectoryCard {
        private final User trainer;
        private final TrainerProfile profile;
        private final double averageRating;
        private final long reviewCount;
        private final List<String> tags;

        public TrainerDirectoryCard(User trainer,
                                    TrainerProfile profile,
                                    double averageRating,
                                    long reviewCount,
                                    List<String> tags) {
            this.trainer = trainer;
            this.profile = profile;
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
            this.tags = tags == null ? new ArrayList<>() : tags;
        }

        public User getTrainer() {
            return trainer;
        }

        public TrainerProfile getProfile() {
            return profile;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public long getReviewCount() {
            return reviewCount;
        }

        public List<String> getTags() {
            return tags;
        }

        public Integer getPricePerSession() {
            return profile != null ? profile.getPricePerSession() : null;
        }

        public String getLocation() {
            return profile != null ? profile.getLocation() : null;
        }

        public String getPrimaryGym() {
            return profile != null ? profile.getPrimaryGym() : null;
        }

        public String getDisplayGym() {
            if (profile != null && profile.getPrimaryGym() != null && !profile.getPrimaryGym().isBlank()) {
                return profile.getPrimaryGym().trim();
            }
            if (trainer.getGymId() != null) {
                return "Gym #" + trainer.getGymId();
            }
            return "Independent";
        }

        public String getInitials() {
            String first = trainer.getFirstName() != null ? trainer.getFirstName().trim() : "";
            String last = trainer.getLastName() != null ? trainer.getLastName().trim() : "";
            String firstInitial = first.isEmpty() ? "" : first.substring(0, 1).toUpperCase(Locale.ROOT);
            String lastInitial = last.isEmpty() ? "" : last.substring(0, 1).toUpperCase(Locale.ROOT);
            String initials = (firstInitial + lastInitial).trim();
            return initials.isEmpty() ? "?" : initials;
        }
    }
}

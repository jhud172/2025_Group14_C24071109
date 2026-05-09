package uk.ac.cf._5.group14.One_To_One.ExerciseData;

public record ExerciseView(
        Long id,
        String category,
        String name,
        String description,
        String videoUrl,
        Integer difficulty,
        String type,
        String imageUrl
) {
    public static ExerciseView from(Exercise exercise) {
        return new ExerciseView(
                exercise.getId(),
                exercise.getCategory(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getVideoUrl(),
                exercise.getDifficulty(),
                exercise.getType(),
                exercise.getImageUrl()
        );
    }
}

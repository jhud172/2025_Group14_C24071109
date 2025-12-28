package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CustomExerciseData;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "custom_exercises")
@Getter
@Setter
public class CustomExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_url")
    private String videoUrl;

    private String type;

    @Column(name = "image_url")
    private String imageUrl;
}
package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSetLogRepository extends JpaRepository<WorkoutSetLog, Long> {
    Optional<WorkoutSetLog> findByIdAndSession(Long id, WorkoutSession session);

    @Query("""
        select max(l.weight) from WorkoutSetLog l
        join l.session s
        where s.user = :user
        and l.exerciseName = :exerciseName
        and l.completed = true
        and l.weight is not null
        and s.startedAt < :cutoff
    """)
    Double findBestWeightBefore(@Param("user") User user,
                                @Param("exerciseName") String exerciseName,
                                @Param("cutoff") LocalDateTime cutoff);

    @Query("""
        select max(l.reps) from WorkoutSetLog l
        join l.session s
        where s.user = :user
        and l.exerciseName = :exerciseName
        and l.completed = true
        and l.weight = :weight
        and l.reps is not null
        and s.startedAt < :cutoff
    """)
    Integer findBestRepsAtWeightBefore(@Param("user") User user,
                                       @Param("exerciseName") String exerciseName,
                                       @Param("weight") Double weight,
                                       @Param("cutoff") LocalDateTime cutoff);

    @Query("""
        select max(l.weight * l.reps) from WorkoutSetLog l
        join l.session s
        where s.user = :user
        and l.exerciseName = :exerciseName
        and l.completed = true
        and l.weight is not null
        and l.reps is not null
        and s.startedAt < :cutoff
    """)
    Double findBestVolumeBefore(@Param("user") User user,
                                @Param("exerciseName") String exerciseName,
                                @Param("cutoff") LocalDateTime cutoff);

    @Query("""
        select l from WorkoutSetLog l
        join l.session s
        where s.user = :user
        and l.exerciseName = :exerciseName
        and l.completed = true
        and s.startedAt < :cutoff
        order by s.startedAt desc, l.id desc
    """)
    List<WorkoutSetLog> findLastCompletedBefore(@Param("user") User user,
                                                @Param("exerciseName") String exerciseName,
                                                @Param("cutoff") LocalDateTime cutoff,
                                                Pageable pageable);
}

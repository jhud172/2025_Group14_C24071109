package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile.BMITrackerDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile.BloodPressureTrackerDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile.CholesterolTrackerDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile.WaistHeightRatioTrackerDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    Optional<HealthRecord> findTopByUserOrderByBaselineDateDescIdDesc(User user);

    @Query("select hr.bmi as BMI, " +
            "hr.baselineDate as dateRated " +
            "from HealthRecord hr " +
            "where hr.user = :user " +
            "and hr.baselineDate >= :dateBefore " +
            "order by hr.baselineDate asc ")
    List<BMITrackerDto> getBMIs(@Param("user") User user, @Param("dateBefore") LocalDateTime dateBefore);

    @Query("select hr.waistHeightRatio as waistHeightRatio, " +
            "hr.baselineDate as dateRated " +
            "from HealthRecord hr " +
            "where hr.user = :user " +
            "and hr.baselineDate >= :dateBefore " +
            "order by hr.baselineDate asc ")
    List<WaistHeightRatioTrackerDto> getWaistHeightRatios(@Param("user") User user, @Param("dateBefore") LocalDateTime dateBefore);

    @Query("select hr.cholesterol as cholesterol, " +
            "hr.baselineDate as dateRated " +
            "from HealthRecord hr " +
            "where hr.user = :user " +
            "and hr.baselineDate >= :dateBefore " +
            "order by hr.baselineDate asc ")
    List<CholesterolTrackerDto> getCholesterolMeasurements(@Param("user") User user, @Param("dateBefore") LocalDateTime dateBefore);

    @Query("select hr.systolicBloodPressure as systolicBP," +
            "hr.diastolicBloodPressure as diastolicBP, " +
            "hr.baselineDate as dateRated " +
            "from HealthRecord hr " +
            "where hr.user = :user " +
            "and hr.baselineDate >= :dateBefore " +
            "order by hr.baselineDate asc ")
    List<BloodPressureTrackerDto> getBPMeasurements(@Param("user") User user, @Param("dateBefore") LocalDateTime dateBefore);


    List<HealthRecord> findAllByUser(User user);
}

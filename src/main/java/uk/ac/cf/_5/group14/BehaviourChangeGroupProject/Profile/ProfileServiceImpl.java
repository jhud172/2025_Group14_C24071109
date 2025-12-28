package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLogRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ProfileServiceImpl implements ProfileService {

    private final ExerciseLogRepository exerciseLogRepository;
    private final HealthRecordRepository healthRecordRepository;

    public ProfileServiceImpl(ExerciseLogRepository exerciseLogRepository, HealthRecordRepository healthRecordRepository) {
        this.exerciseLogRepository = exerciseLogRepository;
        this.healthRecordRepository = healthRecordRepository;
    }

    public List<AverageMoodTrackerDto> getAverageMoodLast6Months(User user) {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return exerciseLogRepository.getAverageMoodDifference(user, sixMonthsAgo);
    }

    private List<String> getAverageMoodLabels(User user) {
        return getAverageMoodLast6Months(user).stream().map(AverageMoodTrackerDto::getMonth).toList();
    }

    private List<Double> getAverageMoodData(User user) {
        return getAverageMoodLast6Months(user).stream().map(AverageMoodTrackerDto::getMoodDifference).toList();
    }

    public List<AverageConfidenceTrackerDto> getAverageConfidenceLast6Months(User user) {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return exerciseLogRepository.getAverageConfidence(user, sixMonthsAgo);
    }

    private List<String> getAverageConfidenceLabels(User user) {
        return getAverageConfidenceLast6Months(user).stream().map(AverageConfidenceTrackerDto::getMonth).toList();
    }

    private List<Double> getAverageConfidenceData(User user) {
        return getAverageConfidenceLast6Months(user).stream().map(AverageConfidenceTrackerDto::getConfidence).toList();
    }

    public List<WaistHeightRatioTrackerDto> getWaistCircumferenceLast6Months(User user) {
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(6).atStartOfDay();
        return healthRecordRepository.getWaistHeightRatios(user, sixMonthsAgo);
    }

    private List<String> getWaistHeightRatioLabels(User user) {
        return getWaistCircumferenceLast6Months(user).stream().map(WaistHeightRatioTrackerDto::getMonthAndDay).toList();
    }

    private List<Double> getWaistHeightRatioData(User user) {
        return getWaistCircumferenceLast6Months(user).stream().map(WaistHeightRatioTrackerDto::getWaistHeightRatio).toList();
    }

    public List<BMITrackerDto> getBMILast6Months(User user) {
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(6).atStartOfDay();
        return healthRecordRepository.getBMIs(user, sixMonthsAgo);
    }

    private List<String> getBMILabels(User user) {
        return getBMILast6Months(user).stream().map(BMITrackerDto::getMonthAndDay).toList();
    }

    private List<Double> getBMIData(User user) {
        return getBMILast6Months(user).stream().map(BMITrackerDto::getBMI).toList();
    }

    public List<CholesterolTrackerDto> getCholesterolLast6Months(User user) {
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(6).atStartOfDay();
        return healthRecordRepository.getCholesterolMeasurements(user, sixMonthsAgo);
    }

    private List<String> getCholesterolLabels(User user) {
        return getCholesterolLast6Months(user).stream().map(CholesterolTrackerDto::getMonthAndDay).toList();
    }

    private List<Double> getCholesterolData(User user) {
        return getCholesterolLast6Months(user).stream().map(CholesterolTrackerDto::getCholesterol).toList();
    }


    public List<BloodPressureTrackerDto> getBloodPressureLast6Months(User user) {
        LocalDateTime sixMonthsAgo = LocalDate.now().minusMonths(6).atStartOfDay();
        return healthRecordRepository.getBPMeasurements(user, sixMonthsAgo);
    }

    private List<String> getBloodPressureLabels(User user) {
        return getBloodPressureLast6Months(user).stream().map(BloodPressureTrackerDto::getMonthAndDay).toList();
    }

    private List<Integer> getSystolicBPData(User user) {
        return getBloodPressureLast6Months(user).stream().map(BloodPressureTrackerDto::getSystolicBP).toList();
    }

    private List<Integer> getDiastolicBPData(User user) {
        return getBloodPressureLast6Months(user).stream().map(BloodPressureTrackerDto::getDiastolicBP).toList();
    }

    public ChartsDataDto getChartData(User user) {
        return new ChartsDataDto(
                getAverageMoodLabels(user),
                getAverageMoodData(user),
                getAverageConfidenceLabels(user),
                getAverageConfidenceData(user),
                getBMILabels(user),
                getBMIData(user),
                getWaistHeightRatioLabels(user),
                getWaistHeightRatioData(user),
                getCholesterolLabels(user),
                getCholesterolData(user),
                getBloodPressureLabels(user),
                getSystolicBPData(user),
                getDiastolicBPData(user)
        );
    }
}

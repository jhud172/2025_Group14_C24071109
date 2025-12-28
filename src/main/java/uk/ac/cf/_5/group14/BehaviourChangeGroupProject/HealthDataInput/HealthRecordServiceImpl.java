package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final PhysicalConditionService physicalConditionService;

    public HealthRecordServiceImpl(HealthRecordRepository healthRecordRepository, PhysicalConditionService physicalConditionService) {
        this.healthRecordRepository = healthRecordRepository;
        this.physicalConditionService = physicalConditionService;
    }

    @Override
    public HealthRecordForm createHealthRecordForm(User user) {
        HealthRecordForm emptyHealthRecordForm = new HealthRecordForm();
        emptyHealthRecordForm.setUser(user);
        emptyHealthRecordForm.setBaselineDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        return emptyHealthRecordForm;
    }

    public HealthRecord formToHealthRecordMapper(HealthRecordForm healthRecordForm) {
        return new HealthRecord(healthRecordForm.getId(), healthRecordForm.getUser(), healthRecordForm.getBaselineDate(), healthRecordForm.getSystolicBloodPressure(), healthRecordForm.getDiastolicBloodPressure(), healthRecordForm.getCholesterol(), healthRecordForm.getWeightKg(), healthRecordForm.getHeightCm(), healthRecordForm.getBmi(), healthRecordForm.getWaistCm(), healthRecordForm.getWaistHeightRatio(),  healthRecordForm.getActivityLevel(), idToPhysicalCondition(healthRecordForm));
    }

    public void addHealthRecord(HealthRecordForm healthRecordForm, User user) {
        HealthRecord healthRecord = formToHealthRecordMapper(healthRecordForm);
        healthRecord.setUser(user);
        healthRecord.setBmi(calculateBmi(healthRecordForm));
        healthRecord.setWaistHeightRatio(calculateWaistHeightRatio(healthRecordForm));

        healthRecordRepository.save(healthRecord);
    }

    private double calculateWaistHeightRatio(HealthRecordForm healthRecordForm) {
        double waistCm = healthRecordForm.getWaistCm();
        double heightCm = healthRecordForm.getHeightCm();
        double waistHeightRatio = waistCm / heightCm;
        return Math.round(waistHeightRatio * 100.0) / 100.0;
    }

    private double calculateBmi(HealthRecordForm healthRecordForm) {
        double weightKg = healthRecordForm.getWeightKg();
        double heightM = healthRecordForm.getHeightCm() / 100.0;
        double bmi = weightKg / (heightM * heightM);
        return Math.round(bmi * 100.0) / 100.0;
    }

    List<PhysicalCondition> idToPhysicalCondition(HealthRecordForm healthRecordForm) {
        List<Long> physicalConditionIds = healthRecordForm.getPhysicalConditions();
        return physicalConditionService.getPhysicalConditionsById(physicalConditionIds);
    }

    @Override
    public List<HealthRecord> getAllHealthRecords(User user) {
        return healthRecordRepository.findAllByUser((user));
    }

    @Override
    public HealthRecord getHealthRecordById(Long id) {
        return healthRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health record not found:ID " + id));
    }

    public HealthRecord getMostRecentHealthRecord(User user) {
        return healthRecordRepository.findTopByUserOrderByBaselineDateDescIdDesc(user)
                .orElse(null);
    }

}

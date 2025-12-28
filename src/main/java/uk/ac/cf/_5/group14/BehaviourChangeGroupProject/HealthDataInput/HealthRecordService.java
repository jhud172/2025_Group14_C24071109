package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface HealthRecordService {
    HealthRecordForm createHealthRecordForm(User user);

    List<HealthRecord> getAllHealthRecords(User user);

    HealthRecord getHealthRecordById(Long id);

    void addHealthRecord(HealthRecordForm healthRecordForm, User user);

    HealthRecord getMostRecentHealthRecord(User user);
}

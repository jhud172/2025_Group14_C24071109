package uk.ac.cf._5.group14.One_To_One.Health.BloodPressure;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BloodPressureService {

    private final BloodPressureReadingRepository repo;

    public BloodPressureService(BloodPressureReadingRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public BloodPressureReading save(BloodPressureReading reading) {
        if (reading.getReadingTime() == null && reading.getId() == null) {
            Optional<BloodPressureReading> existing = repo.findByUserAndReadingDateAndReadingTimeIsNull(
                    reading.getUser(), reading.getReadingDate());
            if (existing.isPresent()) {
                throw new IllegalStateException("A daily reading already exists for " + reading.getReadingDate() +
                        ". Add a time to log multiple readings in a day.");
            }
        }
        return repo.save(reading);
    }

    @Transactional
    public BloodPressureReading update(Long id, BloodPressureReading updated, User currentUser) {
        BloodPressureReading existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reading not found"));
        if (!existing.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Access denied");
        }
        existing.setReadingDate(updated.getReadingDate());
        existing.setReadingTime(updated.getReadingTime());
        existing.setSystolic(updated.getSystolic());
        existing.setDiastolic(updated.getDiastolic());
        existing.setPulse(updated.getPulse());
        existing.setArm(updated.getArm());
        existing.setPosition(updated.getPosition());
        existing.setNotes(updated.getNotes());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        BloodPressureReading reading = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reading not found"));
        if (!reading.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Access denied");
        }
        repo.delete(reading);
    }

    public List<BloodPressureReading> getRecent(User user) {
        return repo.findTop14ByUserOrderByReadingDateDescReadingTimeDesc(user);
    }

    public List<BloodPressureReading> getRange(User user, LocalDate from, LocalDate to) {
        return repo.findForRange(user, from, to);
    }

    public Optional<BloodPressureReading> findById(Long id) {
        return repo.findById(id);
    }

    public BpStats computeStats(List<BloodPressureReading> readings) {
        if (readings.isEmpty()) return new BpStats(0, 0, 0, 0, 0, 0, 0, 0);
        int sysSum = 0, diasSum = 0;
        int sysMin = Integer.MAX_VALUE, sysMax = Integer.MIN_VALUE;
        int diasMin = Integer.MAX_VALUE, diasMax = Integer.MIN_VALUE;
        for (BloodPressureReading r : readings) {
            sysSum += r.getSystolic();
            diasSum += r.getDiastolic();
            if (r.getSystolic() < sysMin) sysMin = r.getSystolic();
            if (r.getSystolic() > sysMax) sysMax = r.getSystolic();
            if (r.getDiastolic() < diasMin) diasMin = r.getDiastolic();
            if (r.getDiastolic() > diasMax) diasMax = r.getDiastolic();
        }
        int count = readings.size();
        long daysLogged = readings.stream().map(BloodPressureReading::getReadingDate).distinct().count();
        return new BpStats(sysSum / count, diasSum / count, sysMin, sysMax, diasMin, diasMax, count, (int) daysLogged);
    }

    /** Consecutive days streak ending today (or most recent logged day). */
    public int computeStreak(User user) {
        List<BloodPressureReading> all = repo.findByUserOrderByReadingDateDescReadingTimeDesc(user);
        if (all.isEmpty()) return 0;
        List<LocalDate> dates = all.stream()
                .map(BloodPressureReading::getReadingDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        LocalDate expected = LocalDate.now();
        if (!dates.get(0).equals(expected) && !dates.get(0).equals(expected.minusDays(1))) return 0;
        expected = dates.get(0);
        int streak = 0;
        for (LocalDate d : dates) {
            if (d.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    public record BpStats(int avgSystolic, int avgDiastolic,
                          int minSystolic, int maxSystolic,
                          int minDiastolic, int maxDiastolic,
                          int totalReadings, int daysLogged) {}
}

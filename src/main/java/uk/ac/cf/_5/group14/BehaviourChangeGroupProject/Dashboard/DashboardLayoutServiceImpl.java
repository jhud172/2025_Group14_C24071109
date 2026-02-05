package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.LayoutItemDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardLayoutServiceImpl implements DashboardLayoutService {

    private final DashboardLayoutRepository dashboardLayoutRepository;

    public DashboardLayoutServiceImpl(DashboardLayoutRepository dashboardLayoutRepository) {
        this.dashboardLayoutRepository = dashboardLayoutRepository;
    }

    @Override
    @Transactional
    public List<DashboardLayout> getOrCreateDefaultLayout(User user) {
        List<DashboardLayout> existing = dashboardLayoutRepository.findByUserOrderBySortIndexAsc(user);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<String> defaultKeys = List.of(
                DashboardModuleKey.QUICK_ACTIONS.getKey(),
                DashboardModuleKey.HEALTH_SUMMARY.getKey(),
                DashboardModuleKey.PHYSICAL_CONDITIONS.getKey(),
                DashboardModuleKey.EXERCISE_PREFERENCES.getKey(),
                DashboardModuleKey.RECENT_EXERCISE_LOGS.getKey(),
                DashboardModuleKey.HEALTH_RECORD_CHARTS.getKey(),
                DashboardModuleKey.EXERCISE_LOG_CHARTS.getKey()
        );

        List<DashboardLayout> rows = new ArrayList<>();
        for (int i = 0; i < defaultKeys.size(); i++) {
            DashboardLayout row = new DashboardLayout();
            row.setUser(user);
            row.setModuleKey(defaultKeys.get(i));
            row.setSortIndex(i);
            row.setEnabled(true);
            rows.add(row);
        }

        dashboardLayoutRepository.saveAll(rows);
        return dashboardLayoutRepository.findByUserOrderBySortIndexAsc(user);
    }

    @Override
    @Transactional
    public void saveLayout(User user, List<LayoutItemDto> items) {
        if (items == null) {
            return;
        }

        Map<String, LayoutItemDto> byKey = new LinkedHashMap<>();
        for (LayoutItemDto item : items) {
            if (item == null || item.getModuleKey() == null) {
                continue;
            }
            if (DashboardModuleKey.fromKey(item.getModuleKey()).isEmpty()) {
                continue;
            }
            byKey.put(item.getModuleKey(), item);
        }

        if (byKey.isEmpty()) {
            return;
        }

        List<DashboardLayout> existing = dashboardLayoutRepository.findByUserOrderBySortIndexAsc(user);
        Map<String, DashboardLayout> existingByKey = existing.stream()
                .collect(Collectors.toMap(DashboardLayout::getModuleKey, v -> v, (a, b) -> a));

        List<DashboardLayout> toSave = new ArrayList<>();
        for (LayoutItemDto item : byKey.values()) {
            DashboardLayout row = existingByKey.getOrDefault(item.getModuleKey(), new DashboardLayout());
            row.setUser(user);
            row.setModuleKey(item.getModuleKey());
            row.setSortIndex(item.getSortIndex());
            row.setEnabled(item.isEnabled());
            toSave.add(row);
        }

        dashboardLayoutRepository.saveAll(toSave);

        // Ensure defaults exist even if client didn't send all module keys
        getOrCreateDefaultLayout(user);
    }
}

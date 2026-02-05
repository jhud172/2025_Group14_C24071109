package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Dashboard.dto.LayoutItemDto;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface DashboardLayoutService {
    List<DashboardLayout> getOrCreateDefaultLayout(User user);
    void saveLayout(User user, List<LayoutItemDto> items);
}

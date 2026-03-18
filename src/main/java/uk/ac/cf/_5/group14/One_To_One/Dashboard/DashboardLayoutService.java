package uk.ac.cf._5.group14.One_To_One.Dashboard;

import uk.ac.cf._5.group14.One_To_One.Dashboard.dto.LayoutItemDto;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface DashboardLayoutService {
    List<DashboardLayout> getOrCreateDefaultLayout(User user);
    void saveLayout(User user, List<LayoutItemDto> items);
}

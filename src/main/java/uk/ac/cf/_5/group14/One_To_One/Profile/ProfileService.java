package uk.ac.cf._5.group14.One_To_One.Profile;

import uk.ac.cf._5.group14.One_To_One.Users.User;

public interface ProfileService {
    ChartsDataDto getChartData(User user);
}

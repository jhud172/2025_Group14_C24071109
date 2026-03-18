package uk.ac.cf._5.group14.One_To_One.Accountability;

import java.io.Serializable;
import java.util.Objects;

public class UserStreakKey implements Serializable {

    private Long userId;
    private StreakType streakType;

    public UserStreakKey() {
    }

    public UserStreakKey(Long userId, StreakType streakType) {
        this.userId = userId;
        this.streakType = streakType;
    }

    public Long getUserId() {
        return userId;
    }

    public StreakType getStreakType() {
        return streakType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStreakKey that = (UserStreakKey) o;
        return Objects.equals(userId, that.userId) && streakType == that.streakType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, streakType);
    }
}

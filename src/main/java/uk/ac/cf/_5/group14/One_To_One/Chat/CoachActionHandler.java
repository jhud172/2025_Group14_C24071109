package uk.ac.cf._5.group14.One_To_One.Chat;

import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

public interface CoachActionHandler<T> {

    CoachActionType type();

    List<String> validate(T payload, User user);

    CoachActionExecution execute(T payload, User user);
}

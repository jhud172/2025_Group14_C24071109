package uk.ac.cf._5.group14.One_To_One.QuickActionsTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.One_To_One.QuickActions.QuickActionDefinition;
import uk.ac.cf._5.group14.One_To_One.QuickActions.QuickActionDefinitionRepository;
import uk.ac.cf._5.group14.One_To_One.QuickActions.QuickActionService;
import uk.ac.cf._5.group14.One_To_One.QuickActions.QuickActionType;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuickActionServiceTest {

    @Test
    void setActiveBlocksWhenMaxReached() {
        QuickActionDefinitionRepository repository = mock(QuickActionDefinitionRepository.class);
        QuickActionService service = new QuickActionService(repository);

        User user = new User();
        user.setId(3L);

        QuickActionDefinition action = new QuickActionDefinition();
        action.setId(10L);
        action.setUser(user);
        action.setType(QuickActionType.BUILT_IN);
        action.setActive(false);

        when(repository.findByIdAndUser(eq(10L), eq(user))).thenReturn(Optional.of(action));
        when(repository.countByUserAndActiveTrue(eq(user))).thenReturn(10L);
        when(repository.save(any(QuickActionDefinition.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(IllegalStateException.class, () -> service.setActive(user, 10L, true, true));
    }

    @Test
    void createCustomBlocksWithoutPremium() {
        QuickActionDefinitionRepository repository = mock(QuickActionDefinitionRepository.class);
        QuickActionService service = new QuickActionService(repository);

        User user = new User();
        user.setId(4L);

        assertThrows(IllegalStateException.class, () -> service.createCustom(user, "Coach", "Do it", false));
    }
}

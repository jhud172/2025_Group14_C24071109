package uk.ac.cf._5.group14.One_To_One.QuickActions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.cf._5.group14.One_To_One.Users.User;

@Service
public class QuickActionService {

    static final int MAX_ACTIVE = 10;
    static final int MAX_CUSTOM = 2;

    private final QuickActionDefinitionRepository repository;

    public QuickActionService(QuickActionDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<QuickActionDefinition> listForUser(User user) {
        validateUser(user);
        ensureDefaults(user);
        return repository.findByUserOrderBySortOrderAsc(user);
    }

    @Transactional
    public List<QuickActionDefinition> listActive(User user) {
        validateUser(user);
        ensureDefaults(user);
        return repository.findByUserOrderBySortOrderAsc(user).stream()
                .filter(QuickActionDefinition::isActive)
                .sorted(Comparator.comparingInt(QuickActionDefinition::getSortOrder))
                .limit(MAX_ACTIVE)
                .toList();
    }

    @Transactional
    public QuickActionDefinition setActive(User user, Long id, boolean active, boolean isPremium) {
        validateUser(user);
        QuickActionDefinition action = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Action not found"));

        if (action.getType() == QuickActionType.CUSTOM_AI && !isPremium) {
            throw new IllegalStateException("Premium subscription required");
        }

        if (active && !action.isActive()) {
            long activeCount = repository.countByUserAndActiveTrue(user);
            if (activeCount >= MAX_ACTIVE) {
                throw new IllegalStateException("Maximum active quick actions reached");
            }
        }

        action.setActive(active);
        return repository.save(action);
    }

    @Transactional
    public QuickActionDefinition createCustom(User user, String name, String prompt, boolean isPremium) {
        validateUser(user);
        if (!isPremium) {
            throw new IllegalStateException("Premium subscription required");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt is required");
        }

        String resolvedPrompt = prompt.trim();
        String resolvedName = (name == null || name.isBlank())
                ? generateNameFromPrompt(resolvedPrompt)
                : name.trim();

        long customCount = repository.countByUserAndType(user, QuickActionType.CUSTOM_AI);
        if (customCount >= MAX_CUSTOM) {
            throw new IllegalStateException("Maximum custom actions reached");
        }

        QuickActionDefinition action = new QuickActionDefinition();
        action.setUser(user);
        action.setType(QuickActionType.CUSTOM_AI);
        action.setName(resolvedName);
        action.setPrompt(resolvedPrompt);
        action.setActionKey(null);
        action.setSortOrder(nextSortOrder(user));

        long activeCount = repository.countByUserAndActiveTrue(user);
        action.setActive(activeCount < MAX_ACTIVE);

        return repository.save(action);
    }

    private String generateNameFromPrompt(String prompt) {
        String[] words = prompt.replaceAll("[^a-zA-Z0-9\\s-]", " ").trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        int added = 0;
        for (String w : words) {
            if (w == null || w.isBlank()) continue;
            if (added >= 5) break;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase());
            added++;
        }
        String result = sb.length() == 0 ? "Quick AI Action" : sb.toString();
        return result.length() > 120 ? result.substring(0, 120) : result;
    }

    @Transactional
    public List<QuickActionDefinition> reorder(User user, List<Long> orderedIds) {
        validateUser(user);
        if (orderedIds == null || orderedIds.isEmpty()) {
            throw new IllegalArgumentException("Order list is required");
        }

        List<QuickActionDefinition> actions = repository.findByUserOrderBySortOrderAsc(user);
        List<QuickActionDefinition> ordered = new ArrayList<>();
        for (Long id : orderedIds) {
            QuickActionDefinition action = actions.stream()
                    .filter(a -> Objects.equals(a.getId(), id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid action id"));
            ordered.add(action);
        }

        int index = 0;
        for (QuickActionDefinition action : ordered) {
            action.setSortOrder(index++);
        }
        repository.saveAll(ordered);
        return repository.findByUserOrderBySortOrderAsc(user);
    }

    @Transactional
    public void ensureDefaults(User user) {
        validateUser(user);
        if (repository.countByUser(user) > 0) {
            return;
        }

        int order = 0;
        List<QuickActionDefinition> defaults = new ArrayList<>();
        for (QuickActionBuiltIn builtIn : QuickActionBuiltIn.defaults()) {
            QuickActionDefinition action = new QuickActionDefinition();
            action.setUser(user);
            action.setType(QuickActionType.BUILT_IN);
            action.setName(builtIn.label());
            action.setActionKey(builtIn.actionKey());
            action.setPrompt(null);
            action.setSortOrder(order++);
            action.setActive(builtIn.defaultActive());
            defaults.add(action);
        }

        repository.saveAll(defaults);
    }

    private int nextSortOrder(User user) {
        return repository.findByUserOrderBySortOrderAsc(user).stream()
                .map(QuickActionDefinition::getSortOrder)
                .max(Integer::compareTo)
                .orElse(-1) + 1;
    }

    private static void validateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
    }
}

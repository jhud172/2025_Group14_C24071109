package uk.ac.cf._5.group14.One_To_One.ChatTests;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ChatPersistenceMappingContractTest {

    private static final String APPLICATION_PACKAGE = "uk.ac.cf._5.group14.One_To_One";

    @Test
    void explicitlyMappedTablesHaveOneEntityOwner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        List<Class<?>> entities = scanner.findCandidateComponents(APPLICATION_PACKAGE).stream()
                .<Class<?>>map(candidate -> loadClass(candidate.getBeanClassName()))
                .toList();

        Map<String, List<Class<?>>> ownersByTable = entities.stream()
                .filter(type -> type.isAnnotationPresent(Table.class))
                .collect(Collectors.groupingBy(type -> type.getAnnotation(Table.class).name()));

        assertThat(ownersByTable)
                .allSatisfy((table, owners) ->
                        assertThat(owners)
                                .as("entity owners for table %s", table)
                                .hasSize(1));
        assertThat(ownersByTable.get("chat_messages"))
                .containsExactly(uk.ac.cf._5.group14.One_To_One.ChatV2.ChatMessage.class);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to inspect entity " + className, exception);
        }
    }
}

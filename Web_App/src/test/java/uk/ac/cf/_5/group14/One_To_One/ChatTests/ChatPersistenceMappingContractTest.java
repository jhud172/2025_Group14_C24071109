package uk.ac.cf._5.group14.One_To_One.ChatTests;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatPersistenceMappingContractTest {

    private static final String APPLICATION_PACKAGE = "uk.ac.cf._5.group14.One_To_One";

    @Test
    void chatMessagesTableHasOneEntityOwner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        List<Class<?>> tableOwners = scanner.findCandidateComponents(APPLICATION_PACKAGE).stream()
                .<Class<?>>map(candidate -> loadClass(candidate.getBeanClassName()))
                .filter(type -> {
                    Table table = type.getAnnotation(Table.class);
                    return table != null && "chat_messages".equals(table.name());
                })
                .toList();

        assertThat(tableOwners)
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

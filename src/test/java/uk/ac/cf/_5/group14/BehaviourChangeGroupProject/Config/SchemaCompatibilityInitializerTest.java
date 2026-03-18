package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaCompatibilityInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void addsDateOfBirthColumnWhenMissing() {
        SchemaCompatibilityInitializer initializer = new SchemaCompatibilityInitializer(jdbcTemplate);
        when(jdbcTemplate.queryForObject(
            SchemaCompatibilityInitializer.HAS_USERS_DATE_OF_BIRTH_COLUMN_SQL,
            Integer.class
        )).thenReturn(0);

        initializer.ensureUsersDateOfBirthColumn();

        verify(jdbcTemplate).execute(SchemaCompatibilityInitializer.ADD_USERS_DATE_OF_BIRTH_COLUMN_SQL);
    }

    @Test
    void skipsPatchWhenDateOfBirthColumnAlreadyExists() {
        SchemaCompatibilityInitializer initializer = new SchemaCompatibilityInitializer(jdbcTemplate);
        when(jdbcTemplate.queryForObject(
            SchemaCompatibilityInitializer.HAS_USERS_DATE_OF_BIRTH_COLUMN_SQL,
            Integer.class
        )).thenReturn(1);

        initializer.ensureUsersDateOfBirthColumn();

        verify(jdbcTemplate, never()).execute(SchemaCompatibilityInitializer.ADD_USERS_DATE_OF_BIRTH_COLUMN_SQL);
    }
}

package uk.ac.cf._5.group14.One_To_One.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaCompatibilityInitializerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private SchemaCompatibilityInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new SchemaCompatibilityInitializer(jdbcTemplate);
    }

    @Test
    void addsEachCompatibilityColumnWhenMissing() {
        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_TABLE_SQL),
            eq(Integer.class),
            eq(SchemaCompatibilityInitializer.USERS_TABLE)
        )).thenReturn(1);

        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_USERS_COLUMN_SQL),
            eq(Integer.class),
            anyString()
        )).thenReturn(0);

        initializer.ensureUsersCompatibilityColumns();

        ArgumentCaptor<String> ddlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, times(SchemaCompatibilityInitializer.USER_COLUMN_PATCHES.size()))
            .execute(ddlCaptor.capture());

        List<String> executedStatements = ddlCaptor.getAllValues();
        assertThat(executedStatements)
            .containsExactlyElementsOf(
                SchemaCompatibilityInitializer.USER_COLUMN_PATCHES.stream()
                    .map(SchemaCompatibilityInitializer.UserColumnPatch::ddl)
                    .toList()
            );
    }

    @Test
    void skipsPatchesWhenCompatibilityColumnsAlreadyExist() {
        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_TABLE_SQL),
            eq(Integer.class),
            eq(SchemaCompatibilityInitializer.USERS_TABLE)
        )).thenReturn(1);

        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_USERS_COLUMN_SQL),
            eq(Integer.class),
            anyString()
        )).thenReturn(1);

        initializer.ensureUsersCompatibilityColumns();

        verify(jdbcTemplate, never()).execute(anyString());
    }

    @Test
    void createsDevModePageSettingsTableWhenMissing() {
        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_TABLE_SQL),
            eq(Integer.class),
            eq(SchemaCompatibilityInitializer.DEV_MODE_PAGE_SETTINGS_TABLE)
        )).thenReturn(0);

        initializer.ensureDevModePageSettingsTable();

        verify(jdbcTemplate).execute(SchemaCompatibilityInitializer.CREATE_DEV_MODE_PAGE_SETTINGS_TABLE_SQL);
    }

    @Test
    void skipsDevModePageSettingsCreationWhenTableAlreadyExists() {
        when(jdbcTemplate.queryForObject(
            eq(SchemaCompatibilityInitializer.HAS_TABLE_SQL),
            eq(Integer.class),
            eq(SchemaCompatibilityInitializer.DEV_MODE_PAGE_SETTINGS_TABLE)
        )).thenReturn(1);

        initializer.ensureDevModePageSettingsTable();

        verify(jdbcTemplate, never()).execute(SchemaCompatibilityInitializer.CREATE_DEV_MODE_PAGE_SETTINGS_TABLE_SQL);
    }
}

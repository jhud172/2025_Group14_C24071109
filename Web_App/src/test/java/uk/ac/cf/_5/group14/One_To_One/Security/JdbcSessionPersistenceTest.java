package uk.ac.cf._5.group14.One_To_One.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcSessionPersistenceTest {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build();
        jdbcTemplate = new JdbcTemplate(database);
        transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(database));
        jdbcTemplate.execute("""
                CREATE TABLE SPRING_SESSION (
                    PRIMARY_ID CHAR(36) NOT NULL PRIMARY KEY,
                    SESSION_ID CHAR(36) NOT NULL,
                    CREATION_TIME BIGINT NOT NULL,
                    LAST_ACCESS_TIME BIGINT NOT NULL,
                    MAX_INACTIVE_INTERVAL INT NOT NULL,
                    EXPIRY_TIME BIGINT NOT NULL,
                    PRINCIPAL_NAME VARCHAR(100)
                )
                """);
        jdbcTemplate.execute("CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID)");
        jdbcTemplate.execute("CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME)");
        jdbcTemplate.execute("CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME)");
        jdbcTemplate.execute("""
                CREATE TABLE SPRING_SESSION_ATTRIBUTES (
                    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                    ATTRIBUTE_BYTES BINARY LARGE OBJECT NOT NULL,
                    PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK
                        FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION (PRIMARY_ID)
                            ON DELETE CASCADE
                )
                """);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void aNewRepositoryInstanceCanRestoreAnAuthenticatedSession() {
        JdbcIndexedSessionRepository firstInstance =
                new JdbcIndexedSessionRepository(jdbcTemplate, transactionTemplate);
        SessionRepository firstRepository = firstInstance;
        Session session = (Session) firstRepository.createSession();
        session.setAttribute("authenticated-user", "synthetic-client");
        firstRepository.save(session);

        JdbcIndexedSessionRepository restartedInstance =
                new JdbcIndexedSessionRepository(jdbcTemplate, transactionTemplate);
        SessionRepository restartedRepository = restartedInstance;
        Session restored = (Session) restartedRepository.findById(session.getId());

        assertThat(restored).isNotNull();
        assertThat(restored.<String>getAttribute("authenticated-user")).isEqualTo("synthetic-client");
    }
}

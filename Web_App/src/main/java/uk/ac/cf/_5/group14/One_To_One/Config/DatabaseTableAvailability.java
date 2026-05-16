package uk.ac.cf._5.group14.One_To_One.Config;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@Component
public class DatabaseTableAvailability {

    private final DataSource dataSource;

    public DatabaseTableAvailability(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean hasTable(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            return false;
        }
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return hasTable(metaData, tableName)
                    || hasTable(metaData, tableName.toUpperCase(Locale.ROOT))
                    || hasTable(metaData, tableName.toLowerCase(Locale.ROOT));
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean hasTable(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }
}

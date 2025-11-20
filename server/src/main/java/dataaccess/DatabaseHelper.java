package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {

    public static void executeUpdate(String query, String errorMessage, PreparedStatementSetter setter)
            throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            setter.setValues(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

    public static <T> T executeUpdateWithReturn(String query, String errorMessage,
        PreparedStatementSetterWithReturn<T> setter) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            return setter.setValuesAndReturn(preparedStatement);
        } catch (SQLException e) {
            throw new DataAccessException(errorMessage, e);
        }
    }

    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface PreparedStatementSetterWithReturn<T> {
        T setValuesAndReturn(PreparedStatement ps) throws SQLException;
    }
}

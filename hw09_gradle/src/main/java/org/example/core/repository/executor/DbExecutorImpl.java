package org.example.core.repository.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class DbExecutorImpl implements DbExecutor {

    @Override
    public long executeStatement(Connection connection, String sql, List<Object> params) {
        try (var pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (var idx = 0; idx < params.size(); idx++) {
                pst.setObject(idx + 1, params.get(idx));
            }
            pst.executeUpdate();
            try (var rs = pst.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DataBaseOperationException("executeInsert error", ex);
        }
    }

    @Override
    public <T> Optional<T> executeSelect(
            Connection connection, String sql, List<Object> params, ResultSetHandler<T> rsHandler) {
        try (var pst = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (var rs = pst.executeQuery()) {
                return Optional.ofNullable(rsHandler.apply(rs));
            }
        } catch (SQLException ex) {
            throw new DataBaseOperationException("executeSelect error", ex);
        }
    }
}

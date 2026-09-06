package org.example.core.repository.executor;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DbExecutor {

    long executeStatement(Connection connection, String sql, List<Object> params);

    <T> Optional<T> executeSelect(
            Connection connection, String sql, List<Object> params, ResultSetHandler<T> rsHandler);
}

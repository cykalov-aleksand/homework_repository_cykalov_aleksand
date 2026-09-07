package org.example.core.repository.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetHandler<T> {
    T apply(ResultSet rs) throws SQLException;
}

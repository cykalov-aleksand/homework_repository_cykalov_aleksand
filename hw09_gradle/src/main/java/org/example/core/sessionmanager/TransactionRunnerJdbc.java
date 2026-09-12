package org.example.core.sessionmanager;

import org.example.core.repository.executor.DataBaseOperationException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class TransactionRunnerJdbc implements TransactionRunner {
    private final DataSource dataSource;

    public TransactionRunnerJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T doInTransaction(TransactionAction<T> action) {
        return wrapException(() -> {
            try (var connection = dataSource.getConnection()) {
                boolean originalAutoCommit = connection.getAutoCommit(); //  сохраняем
                try {
                    connection.setAutoCommit(false); //  отключаем автокоммит
                    var result = action.apply(connection);
                    connection.commit();
                    return result;
                } catch (SQLException ex) {
                    connection.rollback();
                    throw new DataBaseOperationException("doInTransaction exception", ex);
                } finally {
                    connection.setAutoCommit(originalAutoCommit); //  восстанавливаем
                }
            }
        });
    }

    private <T> T wrapException(Callable<T> action) {
        try {
            return action.call();
        } catch (DataBaseOperationException ex) {
            throw ex; // ⚠️ Не оборачиваем повторно!
        } catch (Exception ex) {
            throw new DataBaseOperationException("exception", ex);
        }
}
}

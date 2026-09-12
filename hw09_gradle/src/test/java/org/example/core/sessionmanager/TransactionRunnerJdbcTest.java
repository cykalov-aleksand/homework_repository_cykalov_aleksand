package org.example.core.sessionmanager;

import org.example.core.repository.executor.DataBaseOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TransactionRunnerJdbc должен корректно управлять транзакциями")
class TransactionRunnerJdbcTest {

    private Connection connection;
    private TransactionRunnerJdbc transactionRunner;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);

        transactionRunner = new TransactionRunnerJdbc(dataSource);
    }

    @Test
    @DisplayName("должен выполнять действие и делать commit при отсутствии исключений")
    void doInTransaction_shouldCommitWhenNoException() throws SQLException {
        TransactionAction<String> action = conn -> {
            assertEquals(connection, conn);
            return "успех";
        };

        String result = transactionRunner.doInTransaction(action);

        assertEquals("успех", result);
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
        verify(connection, never()).rollback();
    }

    @Test
    @DisplayName("должен делать rollback при выбрасывании SQLException в действии")
    void doInTransaction_shouldRollbackWhenSQLException() throws SQLException {
        @SuppressWarnings("unchecked")
        TransactionAction<String> action = mock(TransactionAction.class);

        doAnswer(invocation -> {
            throw new SQLException("Ошибка базы данных");
        }).when(action).apply(connection);

        DataBaseOperationException exception = assertThrows(
                DataBaseOperationException.class,
                () -> transactionRunner.doInTransaction(action)
        );

        assertInstanceOf(SQLException.class, exception.getCause());
        assertEquals("Ошибка базы данных", exception.getCause().getMessage());

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
        verify(connection, never()).commit();
    }


    @Test
    @DisplayName("должен восстанавливать autoCommit при RuntimeException, но не делать rollback")
    void doInTransaction_shouldRestoreAutoCommitButNotRollbackOnRuntimeException() throws SQLException {
        // Arrange
        @SuppressWarnings("unchecked")
        TransactionAction<String> action = mock(TransactionAction.class);

        doThrow(new RuntimeException("Произвольная ошибка"))
                .when(action).apply(connection);

        // Act & Assert
        DataBaseOperationException exception = assertThrows(
                DataBaseOperationException.class,
                () -> transactionRunner.doInTransaction(action)
        );

        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals("Произвольная ошибка", exception.getCause().getMessage());

        // autoCommit был отключён
        verify(connection).setAutoCommit(false);
        // rollback НЕ должен вызываться — потому что это не SQLException
        verify(connection, never()).rollback();
        // autoCommit восстановлен
        verify(connection).setAutoCommit(true);
        // commit не был
        verify(connection, never()).commit();
    }
}

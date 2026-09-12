package org.example.repository.executor;


import org.example.core.repository.executor.DataBaseOperationException;
import org.example.core.repository.executor.DbExecutorImpl;
import org.example.core.repository.executor.ResultSetHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbExecutorImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DbExecutorImpl dbExecutor;

    private final ResultSetHandler<String> stringHandler = rs -> rs.next() ? rs.getString("value") : null;

    @BeforeEach
    void setUp() throws SQLException {
        // Настройка мока PreparedStatement при вызове prepareStatement
        lenient().when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    // ✅ Тест для executeStatement — успешное выполнение с автоинкрементным ID
    @Test
    void executeStatement_ShouldReturnGeneratedKey_WhenInsertSuccess() throws SQLException {
        // Given
        String sql = "INSERT INTO users (name) VALUES (?)";
        List<Object> params = List.of("Alice");

        // ✅ ДОБАВЛЕНО: мокируем prepareStatement с RETURN_GENERATED_KEYS
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);

        when(preparedStatement.executeUpdate()).thenReturn(1); // 1 строка изменена

        // Мокируем сгенерированные ключи
        try (ResultSet generatedKeys = mock(ResultSet.class)) {
            when(generatedKeys.next()).thenReturn(true);
            when(generatedKeys.getInt(1)).thenReturn(42);
            when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);

            // When
            long result = dbExecutor.executeStatement(connection, sql, params);

            // Then
            assertEquals(42, result);
            verify(preparedStatement).setObject(1, "Alice");
            verify(preparedStatement).executeUpdate();
            verify(preparedStatement).getGeneratedKeys();
        }
    }

    // ✅ Тест для executeStatement — ошибка SQL
    @Test
    void executeStatement_ShouldThrowDataBaseOperationException_WhenSQLException() throws SQLException {
        // Given
        String sql = "INSERT INTO users (name) VALUES (?)";
        List<Object> params = List.of("Alice");

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("DB error"));// при вызове executeUpdate выбрасываем SQLException

        // When & Then
        DataBaseOperationException thrown = assertThrows(DataBaseOperationException.class, () -> {
            dbExecutor.executeStatement(connection, sql, params);
        });//проверяем что метод выбрасывает исключение DataBaseOperationException

        assertTrue(thrown.getMessage().contains("executeInsert error"));//проверяем текст выбрасываемого исключения
        assertNotNull(thrown.getCause());// проверяем, что исключение содержит причину (SQLException)
        assertEquals("DB error", thrown.getCause().getMessage());//сравниваем сообщение исходного исключения
    }

    // ✅ Тест для executeSelect — успешное получение результата
    @Test
    void executeSelect_ShouldReturnResult_WhenDataPresent() throws SQLException {
        // Given
        String sql = "SELECT value FROM settings WHERE id = ?";
        List<Object> params = List.of(1);

        // Мокируем: при выполнении запроса — возвращаем мок-результат
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // В результате есть строка
        when(resultSet.next()).thenReturn(true);

        // Значение поля "value" — "test_value"
        when(resultSet.getString("value")).thenReturn("test_value");

        // When
        // Выполняем метод: он должен обработать ResultSet через handler
        Optional<String> result = dbExecutor.executeSelect(connection, sql, params, stringHandler);

        // Then
        // Ожидаем, что значение найдено
        assertTrue(result.isPresent());

        // Проверяем, что оно совпадает
        assertEquals("test_value", result.get());

        // Убеждаемся, что:
        verify(preparedStatement).setObject(1, 1);      // Параметр подставлен
        verify(preparedStatement).executeQuery();       // Запрос выполнен
    }

    // ✅ Тест для executeSelect — нет данных
    @Test
    void executeSelect_ShouldReturnEmpty_WhenNoData() throws SQLException {
        // Given
        String sql = "SELECT value FROM settings WHERE id = ?";
        List<Object> params = List.of(999);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // Нет строк

        // When
        Optional<String> result = dbExecutor.executeSelect(connection, sql, params, stringHandler);

        // Then
        assertFalse(result.isPresent());
        verify(preparedStatement).setObject(1, 999);     // Параметр передан
        verify(preparedStatement).executeQuery();       // Запрос выполнен
    }

    // ✅ Тест для executeSelect — ошибка SQL
    @Test
    void executeSelect_ShouldThrowDataBaseOperationException_WhenSQLException() throws SQLException {
        // Given
        String sql = "SELECT value FROM settings WHERE id = ?";
        List<Object> params = List.of(1);

        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // When & Then
        DataBaseOperationException thrown = assertThrows(DataBaseOperationException.class, () -> {
            dbExecutor.executeSelect(connection, sql, params, stringHandler);
        });

        assertTrue(thrown.getMessage().contains("executeSelect error"));
        assertNotNull(thrown.getCause());
        assertEquals("Query failed", thrown.getCause().getMessage());
    }
}

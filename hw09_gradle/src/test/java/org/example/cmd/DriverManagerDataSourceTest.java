package org.example.cmd;
import com.zaxxer.hikari.HikariDataSource;
import org.example.crm.datasource.DriverManagerDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Тесты для DriverManagerDataSource")
class DriverManagerDataSourceTest {

    private DriverManagerDataSource dataSource;

    @BeforeEach
    void setUp() {
        String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String password = "";

        dataSource = new DriverManagerDataSource(url, user, password);
    }

    @AfterEach
    void tearDown() {
        if (dataSource instanceof AutoCloseable autoCloseable) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                System.err.println("Ошибка при закрытии пула: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Должен создать пул соединений и получить подключение")
    void shouldCreateConnectionPoolAndGetConnection() throws SQLException {
        Connection connection = dataSource.getConnection();

        assertThat(connection).isNotNull();  //убеждаемся что соединение connect не равно Null
        assertThat(connection.isClosed()).isFalse(); // убеждаемся что в открытом соединении метод isClosed озвращает false
        assertThat(connection.isValid(5)).isTrue(); //отправляем тестовый запрос и ожидаем ответа в течение 5с если ответ пришёл принимаем решение, что соединение открыто
    }

    @Test
    @DisplayName("Метод getConnection(username, password) должен выбрасывать SQLFeatureNotSupportedException, так как пул использует фиксированные учётные данные")
    void getConnectionWithCredentialsShouldThrowFeatureNotSupported() {
        assertThatThrownBy(() -> dataSource.getConnection("user", "pass"))
                .isInstanceOf(SQLFeatureNotSupportedException.class);// убеждаемся что в нашей базе данных заданы определенные имя и пароль через конструктор и их замена выбросит исключение
    }

    @Test
    @DisplayName("Метод setLogWriter должен выбрасывать SQLFeatureNotSupportedException, так как HikariCP его не поддерживает")
    void setLogWriterShouldThrowFeatureNotSupported() {
        PrintWriter writer = new PrintWriter(System.out);
        assertThatThrownBy(() -> dataSource.setLogWriter(writer))
                .isInstanceOf(SQLFeatureNotSupportedException.class);
    }

    @Test
    @DisplayName("Должен делегировать setLoginTimeout и getLoginTimeout")
    void shouldDelegateLoginTimeout() throws SQLException {
        dataSource.setLoginTimeout(10);
        assertThat(dataSource.getLoginTimeout()).isEqualTo(10);
    }

    @Test
    @DisplayName("Должен поддерживать unwrap и isWrapperFor")
    void shouldSupportWrapperMethods() throws SQLException {
        boolean isWrapper = dataSource.isWrapperFor(DataSource.class);
        assertThat(isWrapper).isTrue();

        DataSource unwrapped = dataSource.unwrap(DataSource.class);
        assertThat(unwrapped).isNotNull();
        assertThat(unwrapped).isInstanceOf(HikariDataSource.class);
    }

    @Test
    @DisplayName("getParentLogger должен выбрасывать SQLFeatureNotSupportedException")
    void getParentLoggerShouldThrowFeatureNotSupported() {
        assertThatThrownBy(() -> dataSource.getParentLogger())
                .isInstanceOf(SQLFeatureNotSupportedException.class)
                .hasMessage("getParentLogger is not supported");
    }

    @Test
    @DisplayName("Должен инициализировать HikariDataSource с правильными параметрами")
    void shouldConfigureHikariPoolCorrectly() {
        HikariDataSource hikariPool = (HikariDataSource) getField(dataSource, "dataSourcePool");

        assertThat(hikariPool).isNotNull();
        assertThat(hikariPool.getMaximumPoolSize()).isEqualTo(10);
        assertThat(hikariPool.getMinimumIdle()).isEqualTo(5);
        assertThat(hikariPool.getConnectionTimeout()).isEqualTo(3000);
        assertThat(hikariPool.getIdleTimeout()).isEqualTo(60000);
        assertThat(hikariPool.getMaxLifetime()).isEqualTo(600000);
        assertThat(hikariPool.isAutoCommit()).isFalse();
        assertThat(hikariPool.getPoolName()).isEqualTo("DemoHiPool");
        assertThat(hikariPool.isRegisterMbeans()).isTrue();

        var props = hikariPool.getDataSourceProperties();
        assertThat(props.getProperty("cachePrepStmts")).isEqualTo("true");
        assertThat(props.getProperty("prepStmtCacheSize")).isEqualTo("250");
        assertThat(props.getProperty("prepStmtCacheSqlLimit")).isEqualTo("2048");
    }

    // Вспомогательный метод для доступа к приватному полю
    private Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить поле: " + fieldName, e);
        }
    }
}
package org.example.crm.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DriverManagerDataSource implements DataSource {
    private final DataSource dataSourcePool;

    public DriverManagerDataSource(String url, String user, String pwd) {
        this.dataSourcePool = createConnectionPool(url, user, pwd);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSourcePool.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSourcePool.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSourcePool.getLogWriter(); // ← Исправлено: делегируем
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSourcePool.setLogWriter(out); // ← Исправлено
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSourcePool.setLoginTimeout(seconds); // ← Исправлено
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSourcePool.getLoginTimeout(); // ← Исправлено
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger is not supported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSourcePool.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSourcePool.isWrapperFor(iface);
    }

    private DataSource createConnectionPool(String url, String user, String pwd) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pwd);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(600000);
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config); // ← возвращаем пул
    }
}

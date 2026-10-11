package org.example;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class FlywayRunner {
    public static void main(String[] args) {
        String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5430/demoDB";
        String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "usr";
        String pass = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "pwd";

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .schemas("public")
                .baselineOnMigrate(true)  // <-- ключевое изменение
                .load();

        flyway.migrate();
        System.out.println("Migrations applied successfully.");
    }
}

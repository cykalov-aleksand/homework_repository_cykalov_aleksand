package org.example.jdbc.mapper.data_template;

import org.example.core.repository.executor.DbExecutor;
import org.example.jdbc.mapper.entity_class.EntityClassMetaData;
import org.example.jdbc.mapper.entity_sql.EntitySQLMetaData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сохраняет объект в базу, читает объект из базы
 */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectByIdSql(),
                List.of(id),
                rs -> rs.next() ? mapRowToEntity(rs) : null
        );
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectAllSql(),
                List.of(),
                rs -> {
                    List<T> entities = new ArrayList<>();
                    while (rs.next()) {
                        entities.add(mapRowToEntity(rs));
                    }
                    return entities;
                }
        ).orElse(new ArrayList<>());
    }

    @Override
    public long insert(Connection connection, T object) {
        var params = extractFieldValuesWithoutId(object);
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
    }

    @Override
    public void update(Connection connection, T object) {
        var params = extractFieldValuesWithoutId(object);
        var idValue = getIdValue(object);
        params.add(idValue);
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
    }

    private T mapRowToEntity(ResultSet rs) throws SQLException {
        try {
            T entity = entityClassMetaData.getConstructor().newInstance();
            for (var field : entityClassMetaData.getAllFields()) {
                field.setAccessible(true);
                var value = rs.getObject(field.getName());
                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при маппинге строки в объект", e);
        }
    }

    private List<Object> extractFieldValuesWithoutId(T object) {
        List<Object> params = new ArrayList<>();
        try {
            for (var field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(object));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Ошибка при чтении полей объекта", e);
        }
        return params;
    }

    private Object getIdValue(T object) {
        try {
            var idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            return idField.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Ошибка при получении значения ID", e);
        }
    }
}

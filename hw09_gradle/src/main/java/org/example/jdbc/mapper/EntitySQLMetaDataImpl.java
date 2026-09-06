package org.example.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String tableName;
    private final String selectAllSql; //строка запроса в SQL для поиска всех строк таблицы с именем tableName
    private final String selectByIdSql; //строка запроса в SQL для поиска строки с заданным Id
    private final String insertSql;     //строка запроса в SQL для добавления строки в базу данных с пустыми полями
    private final String updateSql;     //строка запроса в SQL для удаления строки из базы данных

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.tableName = entityClassMetaData.getName();
        this.selectAllSql = "SELECT * FROM " + tableName;
        this.selectByIdSql = "SELECT * FROM " + tableName + " WHERE " + getColumnName(entityClassMetaData.getIdField()) + " = ?";
        this.insertSql = generateInsertSql(entityClassMetaData);
        this.updateSql = generateUpdateSql(entityClassMetaData);
    }

    private String generateInsertSql(EntityClassMetaData<?> meta) {
        String columns = meta.getFieldsWithoutId().stream()
                .map(this::getColumnName)
                .collect(Collectors.joining(", "));//создаем строку со списком полей класса кроме поля помеченного аннотацией @Id

        String placeholders = meta.getFieldsWithoutId().stream()
                .map(f -> "?")
                .collect(Collectors.joining(", "));//создаем строку (?, ?...) по количеству вопросов исходя из количества полей

        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";//формируем строку запроса
    }

    private String generateUpdateSql(EntityClassMetaData<?> meta) {
        String setClause = meta.getFieldsWithoutId().stream()
                .map(f -> getColumnName(f) + " = ?")
                .collect(Collectors.joining(", "));//создаем строку (имя поля)=? по всем полям класса за исключением поля помеченного аннотацией @Id

        String idColumn = getColumnName(meta.getIdField());//читаем имя поля помеченное аннотацией @Id

        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumn + " = ?"; //формируем строку запроса на удаление строки с базы данных
    }

    private String getColumnName(Field field) {
        // Можно расширить через @Column(name = "..."), но пока просто имя поля в нижнем регистре
        return field.getName();
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }
}

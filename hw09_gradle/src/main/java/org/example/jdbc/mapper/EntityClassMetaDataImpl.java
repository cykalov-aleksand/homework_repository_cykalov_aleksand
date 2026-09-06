package org.example.jdbc.mapper;

import org.example.crm.model.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> type;        //тип класса
    private final Field idField;        //поле класса помеченное аннотацией @Id
    private final List<Field> allFields;//весь список полей класса
    private final List<Field> fieldsWithoutId;//список полей класса без поля помеченного аннотацией @Id

    public EntityClassMetaDataImpl(Class<T> type) {
        this.type = type;
        this.idField = Arrays.stream(type.getDeclaredFields())      //ищем в классе поля помеченные аннотацией Id
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ошибка класса " + type.getSimpleName() + "- класс не имеет полей помеченных аннотацией @Id"));

        this.allFields = Arrays.stream(type.getDeclaredFields()).collect(Collectors.toList());
        this.fieldsWithoutId = allFields.stream()
                .filter(f -> !f.equals(idField))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return type.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Ошибка класс  " + type.getSimpleName() + " не имеет конструктора по умолчанию ", e);
        }
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}

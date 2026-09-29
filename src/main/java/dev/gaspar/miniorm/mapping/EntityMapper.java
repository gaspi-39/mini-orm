package dev.gaspar.miniorm.mapping;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.gaspar.miniorm.annotation.*;

public class EntityMapper {

  public <T> T mapRow(ResultSet rs, Class<T> clazz) throws SQLException, ReflectiveOperationException {
    Map<String, String> fieldToColumn = this.fieldColumn(clazz);
    T instance = this.createInstance(clazz);

    for (Field field : clazz.getDeclaredFields()) {
      Object value = rs.getObject(fieldToColumn.get(field.getName()));
      field.setAccessible(true);
      if (value != null) {
        field.set(instance, value);
      }
    }
    return instance;
  }

  private Map<String, String> fieldColumn(Class<?> clazz) {
    Map<String, String> map = new HashMap<>();
    Field[] f = clazz.getDeclaredFields();

    for (Field field : f) {
      Column column = field.getAnnotation(Column.class);
      if (column != null && !column.value().isEmpty()) {
        map.put(field.getName(), column.value());
      } else {
        map.put(field.getName(), field.getName());
      }
    }
    return map;
  }

  public <T> T createInstance(Class<T> clazz) throws Exception {
    return clazz.getDeclaredConstructor().newInstance();
  }

  public String buildInsert(Object e) {
    Class<?> clazz = e.getClass();
    String table = clazz.getAnnotation(Table.class).value();
    Map<String, String> fieldToColumn = this.fieldColumn(clazz);

    List<String> placeholders = new ArrayList<>();
    List<String> columns = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      columns.add(fieldToColumn.get(field.getName()));
      placeholders.add("?");
    }

    String result = "INSERT INTO " + table + " (" + String.join(", ", columns) + ")" + " VALUES ("
        + String.join(", ", placeholders) + ")";
    return result;
  }

  private String findIdField(Class<?> clazz) {

    Map<String, String> fieldColumn = this.fieldColumn(clazz);
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Id.class)) {
        return fieldColumn.get(field.getName());
      }
    }

    throw new IllegalArgumentException(
        "Entity " + clazz.getName() + " must have a field annotated with @Id");
  }

  public String buildSelectById(Class<?> clazz) {
    String table = clazz.getAnnotation(Table.class).value();
    String idColumnName = this.findIdField(clazz);
    String result = "SELECT * FROM " + table + " WHERE " + idColumnName + " = ?";
    return result;
  }

  public String buildUpdate(Class<?> clazz) {
    String table = clazz.getAnnotation(Table.class).value();
    Map<String, String> fieldToColumn = this.fieldColumn(clazz);
    String idColumnName = this.findIdField(clazz);

    List<String> set = new ArrayList<>();
    String where = " WHERE " + idColumnName + " = ?";
    for (Field field : clazz.getDeclaredFields()) {
      if (!field.isAnnotationPresent(Id.class)) {
        set.add(fieldToColumn.get(field.getName()) + " = ?");
      }
    }

    String result = "UPDATE " + table + " SET " + String.join(", ", set) + where;
    return result;
  }

}

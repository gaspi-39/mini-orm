package dev.gaspar.miniorm.repository;

import dev.gaspar.miniorm.connection.ConnectionFactory;
import dev.gaspar.miniorm.mapping.EntityMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReflectiveRepository<T, ID> implements Repository<T, ID> {
  private final Class<T> entityClass;
  private final EntityMapper mapper;
  private final ConnectionFactory factory;

  public ReflectiveRepository(Class<T> entityClass, EntityMapper mapper, ConnectionFactory factory) {
    this.entityClass = entityClass;
    this.mapper = mapper;
    this.factory = factory;
  }

  public T findById(ID id) {
    try {
      String sql = this.mapper.buildSelectById(this.entityClass);
      try (Connection conn = this.factory.getConnection();
          PreparedStatement select = conn.prepareStatement(sql)) {
        select.setObject(1, id);
        ResultSet rs = select.executeQuery();

        if(rs.next()) {
          T instance = this.mapper.mapRow(rs, this.entityClass);
          return instance;
        } else {
          return null;    
        }
          }
    } catch (SQLException | ReflectiveOperationException exception) {

      throw new RuntimeException(
          "Didn´t find the entity "
          + this.entityClass.getSimpleName()
          + " with id "
          + id,
          exception
          );
    }
  }

  public void save(T entity) {
    try {
      String sql = this.mapper.buildInsert(entity.getClass());
      try (Connection conn = this.factory.getConnection();
          PreparedStatement insert = conn.prepareStatement(sql)) {
        this.mapper.bind(insert, entity);
        insert.executeUpdate();
          }
    } catch (SQLException | ReflectiveOperationException exception) { 
      throw new RuntimeException("Can´t insert the entity " + this.entityClass.getSimpleName(), exception);
    }
  }

    
}


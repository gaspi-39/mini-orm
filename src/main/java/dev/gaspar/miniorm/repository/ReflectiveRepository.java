package dev.gaspar.miniorm.repository;

import dev.gaspar.miniorm.connection.ConnectionFactory;
import dev.gaspar.miniorm.mapping.EntityMapper;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReflectiveRepository<T> implements Repository<T, Long> {
  private final Class<T> entityClass;
  private final EntityMapper mapper;
  private final ConnectionFactory factory;

  public ReflectiveRepository(Class<T> entityClass, EntityMapper mapper, ConnectionFactory factory) {
    this.entityClass = entityClass;
    this.mapper = mapper;
    this.factory = factory;
  }
  
  public T findById(Long id) {
   String sql = mapper.buildSelectById(this.entityCLass);
   try (Connection conn = factory.getConnection();
       PreparedStatement select = conn.prepareStatement(sql)) {
     select.setLong(id);
     ResultSet rs = select.executeQuery();

     if(rs.next()) {
       T instance = mapper.mapRow(rs, this.entityClass);
       return instance;
     } else {
       return null;    
       }
       }
  }
}


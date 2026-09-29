package dev.gaspar.miniorm.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

import dev.gaspar.miniorm.Cliente;
import dev.gaspar.miniorm.connection.ConnectionFactory;

class EntityMapperMapRowTest {

  @Test
  void mapRowConvierteFilaEnObjeto() throws Exception {
    ConnectionFactory factory = new ConnectionFactory("jdbc:h2:mem:maprow;DB_CLOSE_DELAY=-1");
    EntityMapper mapper = new EntityMapper();

    try (Connection conn = factory.getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute("CREATE TABLE clientes (id BIGINT PRIMARY KEY, razon_social VARCHAR(255))");
      try (PreparedStatement insert = conn
          .prepareStatement("INSERT INTO clientes (id, razon_social) VALUES (?, ?)")) {
        insert.setLong(1, 1L);
        insert.setString(2, "ForjaTech");
        insert.executeUpdate();
      }

      try (PreparedStatement select = conn.prepareStatement("SELECT * FROM clientes WHERE id = ?")) {
        select.setLong(1, 1L);
        ResultSet rs = select.executeQuery();
        rs.next();

        Cliente cliente = mapper.mapRow(rs, Cliente.class);

        assertEquals(1L, cliente.getId());
        assertEquals("ForjaTech", cliente.getRazonSocial());
      }
    }
  }
}

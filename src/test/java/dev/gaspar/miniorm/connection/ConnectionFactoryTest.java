package dev.gaspar.miniorm.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class ConnectionFactoryTest {
  @Test
  void connectionIsAlive() throws SQLException {
    ConnectionFactory factory = new ConnectionFactory("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    try (Connection conn = factory.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute("CREATE TABLE dummy (id INT PRIMARY KEY)");
    }
  }
}

package dev.gaspar.miniorm.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.gaspar.miniorm.Cliente;
import dev.gaspar.miniorm.annotation.Table;
import dev.gaspar.miniorm.connection.ConnectionFactory;
import dev.gaspar.miniorm.mapping.EntityMapper;

class ReflectiveRepositoryTest {
  private static final String DATABASE_URL = "jdbc:h2:mem:reflectiveRepositoryTest;DB_CLOSE_DELAY=-1";

  private ConnectionFactory factory;

  @BeforeEach
  void setUp() throws SQLException {
    factory = new ConnectionFactory(DATABASE_URL);
    try (Connection connection = factory.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS clientes");
      statement.execute("CREATE TABLE clientes (id BIGINT PRIMARY KEY, razon_social VARCHAR(255))");
    }
  }

  @Test
  void findByIdReturnsMappedEntityWhenRowExists() throws SQLException {
    insertCliente(1L, "ForjaTech");

    Cliente cliente = createRepository().findById(1L);

    assertNotNull(cliente);
    assertEquals(1L, cliente.getId());
    assertEquals("ForjaTech", cliente.getRazonSocial());
  }

  @Test
  void findByIdReturnsRequestedRowWhenMultipleRowsExist() throws SQLException {
    insertCliente(1L, "ForjaTech");
    insertCliente(2L, "Acme");

    Cliente cliente = createRepository().findById(2L);

    assertNotNull(cliente);
    assertEquals(2L, cliente.getId());
    assertEquals("Acme", cliente.getRazonSocial());
  }

  @Test
  void findByIdReturnsNullWhenRowDoesNotExist() {
    Cliente cliente = createRepository().findById(99L);

    assertNull(cliente);
  }

  @Test
  void findByIdKeepsNullColumnAsNull() throws SQLException {
    insertCliente(1L, null);

    Cliente cliente = createRepository().findById(1L);

    assertNotNull(cliente);
    assertEquals(1L, cliente.getId());
    assertNull(cliente.getRazonSocial());
  }

  @Test
  void findByIdWrapsConnectionFailureWithContext() {
    ConnectionFactory invalidFactory = new ConnectionFactory("not-a-jdbc-url");
    ReflectiveRepository<Cliente, Long> repository = new ReflectiveRepository<>(
        Cliente.class, new EntityMapper(), invalidFactory);

    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> repository.findById(1L));

    assertTrue(exception.getMessage().contains("Cliente"));
    assertTrue(exception.getMessage().contains("1"));
    assertInstanceOf(SQLException.class, exception.getCause());
  }

  @Test
  void findByIdPropagatesMissingIdAsUncheckedException() {
    ReflectiveRepository<EntityWithoutId, Long> repository = new ReflectiveRepository<>(
        EntityWithoutId.class, new EntityMapper(), factory);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> repository.findById(1L));

    assertTrue(exception.getMessage().contains("must have a field annotated with @Id"));
  }

  @Test
  void savePersistsEntityAndReturnsTheStoredRow() {
    Cliente cliente = new Cliente();
    cliente.setId(1L);
    cliente.setRazonSocial("ForjaTech");

    createRepository().save(cliente);

    Cliente stored = createRepository().findById(1L);
    assertNotNull(stored);
    assertEquals(1L, stored.getId());
    assertEquals("ForjaTech", stored.getRazonSocial());
  }

  @Test
  void savePersistsNullColumns() {
    Cliente cliente = new Cliente();
    cliente.setId(1L);
    cliente.setRazonSocial(null);

    createRepository().save(cliente);

    Cliente stored = createRepository().findById(1L);
    assertNotNull(stored);
    assertEquals(1L, stored.getId());
    assertNull(stored.getRazonSocial());
  }

  @Test
  void saveWrapsConnectionFailureWithContext() {
    ReflectiveRepository<Cliente, Long> repository = new ReflectiveRepository<>(
        Cliente.class, new EntityMapper(), new ConnectionFactory("not-a-jdbc-url"));

    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> repository.save(new Cliente()));

    assertTrue(exception.getMessage().contains("Cliente"));
    assertInstanceOf(SQLException.class, exception.getCause());
  }

  private ReflectiveRepository<Cliente, Long> createRepository() {
    return new ReflectiveRepository<>(Cliente.class, new EntityMapper(), factory);
  }

  private void insertCliente(long id, String razonSocial) throws SQLException {
    try (Connection connection = factory.getConnection();
        PreparedStatement insert = connection.prepareStatement(
            "INSERT INTO clientes (id, razon_social) VALUES (?, ?)")) {
      insert.setLong(1, id);
      insert.setString(2, razonSocial);
      insert.executeUpdate();
    }
  }

  @Table("entities_without_id")
  private static class EntityWithoutId {
  }
}

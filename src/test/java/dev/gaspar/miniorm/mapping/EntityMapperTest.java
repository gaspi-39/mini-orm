package dev.gaspar.miniorm.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import dev.gaspar.miniorm.Cliente;
import dev.gaspar.miniorm.Factura;

class EntityMapperTest {

  @Test
  void buildInsertGeneratesSqlWithPlaceholders() {
    EntityMapper mapper = new EntityMapper();

    String sql = mapper.buildInsert(new Cliente());

    assertEquals("INSERT INTO clientes (id, razon_social) VALUES (?, ?)", sql);
  }

  @Test
  void buildUpdateGeneratesSqlWithSetAndWhere() {
    EntityMapper mapper = new EntityMapper();

    String sql = mapper.buildUpdate(Cliente.class);

    assertEquals("UPDATE clientes SET razon_social = ? WHERE id = ?", sql);
  }

  @Test
  void buildUpdateDoesNotIncludePrimaryKeyInSet() {
    EntityMapper mapper = new EntityMapper();

    String sql = mapper.buildUpdate(Cliente.class);
    String setClause = sql.split("WHERE")[0];

    assertFalse(setClause.contains("id ="));
  }

  @Test
  void buildUpdateUsesRenamedIdColumnInWhere() {
    EntityMapper mapper = new EntityMapper();

    String sql = mapper.buildUpdate(Factura.class);

    assertEquals("UPDATE facturas SET total = ? WHERE factura_id = ?", sql);
  }
}
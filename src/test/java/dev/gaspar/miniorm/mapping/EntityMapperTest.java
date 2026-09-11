package dev.gaspar.miniorm.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dev.gaspar.miniorm.Cliente;

class EntityMapperTest {

  @Test
  void buildInsertGeneratesSqlWithPlaceholders() {
    EntityMapper mapper = new EntityMapper();

    String sql = mapper.buildInsert(new Cliente());

    assertEquals("INSERT INTO clientes (id, razon_social) VALUES (?, ?)", sql);
  }
}
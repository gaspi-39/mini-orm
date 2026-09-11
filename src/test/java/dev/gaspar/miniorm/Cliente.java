package dev.gaspar.miniorm;

import dev.gaspar.miniorm.annotation.Column;
import dev.gaspar.miniorm.annotation.Id;
import dev.gaspar.miniorm.annotation.Table;

@Table("clientes")
public class Cliente {
  @Id
  private Long id;
  @Column("razon_social")
  private String razonSocial;

  public Cliente() {
  }
}
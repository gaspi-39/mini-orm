package dev.gaspar.miniorm;

import dev.gaspar.miniorm.annotation.Column;
import dev.gaspar.miniorm.annotation.Id;
import dev.gaspar.miniorm.annotation.Table;

@Table("facturas")
public class Factura {
  @Id
  @Column("factura_id")
  private Long id;
  private Double total;

  public Factura() {
  }
}

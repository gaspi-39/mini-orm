package dev.gaspar.miniorm.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

  private String url;

  public ConnectionFactory(String url) {
    this.url = url;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(this.url);

  }

}

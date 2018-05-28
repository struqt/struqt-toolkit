package struqt.tool.jdbc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Embedded Mysql")
public class EmbeddedMysqlTest {

  private static final Logger log = LoggerFactory.getLogger(EmbeddedMysqlTest.class);
  private static JdbcServer jdbcServer;
  private static String jdbcUrl;

  @BeforeAll
  public static void beforeAll() {
    String databaseName = "db_classic_models";
    JdbcServerConfig config =
        JdbcServerConfig.builder()
            .urlPort(0)
            .database(databaseName, "example.sql")
            .database(databaseName, "example.data.sql")
            .build();
    jdbcServer = new EmbeddedMysql().start(config);
    jdbcUrl = "jdbc:mysql://localhost:" + jdbcServer.getUrlPort() + "/" + databaseName;
  }

  @AfterAll
  public static void afterAll() {
    if (jdbcServer != null) {
      jdbcServer.stop();
      jdbcServer = null;
    }
  }

  @Test
  public void testConnection() throws SQLException {
    assertTrue(jdbcServer.getUrlPort() > 0);
    log.info("jdbcUrl: {}", jdbcUrl);
    Connection conn = DriverManager.getConnection(jdbcUrl);
    assertTrue(conn.isValid(5));
    conn.close();
  }

  @Test
  public void testShowDatabases() throws SQLException {
    try (Connection conn = DriverManager.getConnection(jdbcUrl);
        PreparedStatement statement = conn.prepareStatement("show databases");
        ResultSet resultSet = statement.executeQuery()) {
      int count = 0;
      while (resultSet.next()) {
        ++count;
        log.info("database name: {}", resultSet.getString(1));
      }
      assertTrue(count > 0);
    }
  }

  @Test
  public void testShowTables() throws SQLException {
    try (Connection conn = DriverManager.getConnection(jdbcUrl);
        PreparedStatement statement = conn.prepareStatement("show tables");
        ResultSet resultSet = statement.executeQuery()) {
      int count = 0;
      while (resultSet.next()) {
        ++count;
        String table = resultSet.getString(1);
        log.info("table name: {}", table);
      }
      assertTrue(count > 0);
    }
  }

  @Test
  public void testDescribeTables() throws SQLException {
    final List<String> tables = new LinkedList<>();
    try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
      try (PreparedStatement statement = conn.prepareStatement("show tables");
          ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String table = resultSet.getString(1);
          tables.add(table);
        }
      }
      for (String table : tables) {
        try (PreparedStatement statement = conn.prepareStatement("desc " + table);
            ResultSet resultSet = statement.executeQuery()) {
          log.info("----- table name: {} -----", table);
          while (resultSet.next()) {
            String f1 = resultSet.getString(1);
            String f2 = resultSet.getString(2);
            log.info("table field desc: {} {}", f2, f1);
          }
        }
      }
    }
  }

  @Test
  public void testQueryForOrders() throws SQLException {
    String sql =
        "select `orderDate` `orderDate`, count(*) `orderCount` "
            + "from `orders` "
            + "group by `orderDate` "
            + "having `orderCount`>1 "
            + "order by `orderDate` asc "
            + "limit ?";
    try (Connection conn = DriverManager.getConnection(jdbcUrl);
        PreparedStatement statement = conn.prepareStatement(sql)) {
      final int limit = 20;
      statement.setInt(1, limit);
      try (ResultSet resultSet = statement.executeQuery()) {
        int count = 0;
        while (resultSet.next()) {
          ++count;
          Date f1 = resultSet.getDate(1);
          Long f2 = resultSet.getLong(2);
          log.info("record { orderDate:{}, orderCount:{} }", f1, f2);
        }
        assertEquals(limit, count);
      }
    }
  }
}

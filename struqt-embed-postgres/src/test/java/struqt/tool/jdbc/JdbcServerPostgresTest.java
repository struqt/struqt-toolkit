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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Embedded Postgres")
public class JdbcServerPostgresTest {

  private static final Logger log = LoggerFactory.getLogger(JdbcServerPostgresTest.class);
  private static JdbcServer jdbcServer;
  private static String jdbcUrl;

  @BeforeAll
  public static void beforeAll() {
    JdbcDatabaseConfig config =
        JdbcDatabaseConfig.builder()
            .name("db_classic_models")
            .addInitSqlPath("src/test/resources/example.sql")
            .addInitSqlPath("src/test/resources/example.data.sql")
            .addAttribute("version", "V9_6")
            .urlPort(0)
            .build();
    jdbcServer = new JdbcServerPostgres().start(config);
    jdbcUrl = jdbcServer.jdbcUrl();
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
    assertNotNull(jdbcServer.jdbcUrl());
    log.info("jdbcUrl: {}", jdbcUrl);
    Connection conn = DriverManager.getConnection(jdbcUrl);
    assertTrue(conn.isValid(5));
    conn.close();
  }

  @Test
  public void testShowDatabases() throws SQLException {
    try (Connection conn = DriverManager.getConnection(jdbcUrl);
        PreparedStatement statement =
            conn.prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false");
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
        PreparedStatement statement =
            conn.prepareStatement("SELECT * FROM pg_catalog.pg_tables WHERE schemaname='public'");
        ResultSet resultSet = statement.executeQuery()) {
      int count = 0;
      while (resultSet.next()) {
        ++count;
        String table = resultSet.getString(2);
        log.info("table name: {}", table);
      }
      assertTrue(count >= 0);
    }
  }

  @Test
  public void testQueryForOrders() throws SQLException {
    String sql =
        ""
            + "select orderDate, count(*) orderCount\n"
            + "from orders\n"
            + "group by orderDate\n"
            + "having count(*)>1\n"
            + "order by orderDate asc\n"
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

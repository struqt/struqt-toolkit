package struqt.tool.jdbc;

public interface JdbcServer {

  JdbcServer start(JdbcDatabaseConfig config);

  void stop();

  String jdbcUrl();
}

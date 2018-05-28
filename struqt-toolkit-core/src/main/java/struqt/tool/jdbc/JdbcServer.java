package struqt.tool.jdbc;

public interface JdbcServer {

  JdbcServer start(JdbcServerConfig config);

  void stop();

  int getUrlPort();
}

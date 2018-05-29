package struqt.tool.jdbc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedMysql implements JdbcServer {

  private static final Logger log = LoggerFactory.getLogger(EmbeddedMysql.class);

  private DB server = null;
  private volatile String jdbcUrl = null;

  @Override
  public String jdbcUrl() {
    return jdbcUrl;
  }

  @Override
  public synchronized JdbcServer start(JdbcDatabaseConfig config) {
    if (config == null) {
      return this;
    }
    if (startServer(config)) {
      createDatabase(config.getName());
      for (String res : config.getInitSqlPaths()) {
        source(res);
      }
    }
    return this;
  }

  @Override
  public synchronized void stop() {
    if (server == null) {
      return;
    }
    DB db = server;
    server = null;
    try {
      db.stop();
    } catch (ManagedProcessException e) {
      log.error(e.getMessage(), e);
    }
  }

  private boolean startServer(JdbcDatabaseConfig config) {
    if (server != null) {
      return false;
    }
    int port = config.getUrlPort();
    DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder();
    builder
        .addArg("--user=root") /* Fix fatal error in docker environment */
        .setPort(port) /* Port 0: detect free urlPort */;
    try {
      server = DB.newEmbeddedDB(builder.build());
      server.start();
      this.jdbcUrl = builder.getURL(config.getName());
      return true;
    } catch (ManagedProcessException e) {
      log.error(e.getMessage(), e);
      server = null;
      return false;
    }
  }

  private void createDatabase(String name) {
    if (server == null) {
      return;
    }
    if (name != null && name.length() > 0) {
      try {
        server.createDB(name);
      } catch (ManagedProcessException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private void source(String resource) {
    if (server == null) {
      return;
    }
    try {
      server.source(resource);
    } catch (ManagedProcessException e) {
      log.error(e.getMessage(), e);
    }
  }
}

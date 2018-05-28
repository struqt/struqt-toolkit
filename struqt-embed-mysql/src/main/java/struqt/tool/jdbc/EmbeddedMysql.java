package struqt.tool.jdbc;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedMysql implements JdbcServer {

  private static final Logger log = LoggerFactory.getLogger(EmbeddedMysql.class);

  private DB server = null;
  private volatile int port = -1;

  @Override
  public int getUrlPort() {
    return port;
  }

  @Override
  public JdbcServer start(JdbcServerConfig config) {
    if (config == null) {
      return this;
    }
    if (startServer(config.getUrlPort())) {
      for (JdbcServerConfig.JdbcDatabaseConfig database : config.getDatabases()) {
        createDatabase(database.getName());
        for (String res : database.getInitResources()) {
          source(res);
        }
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

  private synchronized boolean startServer(int port) {
    if (server != null) {
      return false;
    }
    DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder();
    builder
        .addArg("--user=root") /* Fix fatal error in docker environment */
        .setPort(port) /* Port 0: detect free urlPort */;
    try {
      server = DB.newEmbeddedDB(builder.build());
      server.start();
      this.port = server.getConfiguration().getPort();
      return true;
    } catch (ManagedProcessException e) {
      log.error(e.getMessage(), e);
      server = null;
      return false;
    }
  }

  private synchronized void createDatabase(String name) {
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

  private synchronized void source(String resource) {
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

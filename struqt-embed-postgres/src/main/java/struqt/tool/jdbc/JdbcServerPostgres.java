package struqt.tool.jdbc;

import de.flapdoodle.embed.process.distribution.IVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import java.io.File;
import java.io.IOException;

import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_HOST;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_PASSWORD;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.DEFAULT_USER;
import static ru.yandex.qatools.embed.postgresql.util.SocketUtil.findFreePort;

public class JdbcServerPostgres implements JdbcServer {

  private static final Logger log = LoggerFactory.getLogger(JdbcServerPostgres.class);

  private EmbeddedPostgres server = null;
  private volatile String jdbcUrl = null;

  @Override
  public String jdbcUrl() {
    return jdbcUrl;
  }

  @Override
  public JdbcServer start(JdbcDatabaseConfig config) {
    if (config == null) {
      return this;
    }
    if (startServer(config)) {
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
    server.stop();
    server = null;
  }

  private synchronized boolean startServer(JdbcDatabaseConfig config) {
    if (server != null) {
      return false;
    }
    String name = config.getName();
    int port = config.getUrlPort();
    if (port <= 0) {
      port = findFreePort();
    }
    try {
      IVersion version = version(config.getAttribute("version"));
      this.server = new EmbeddedPostgres(version);
      this.jdbcUrl = server.start(DEFAULT_HOST, port, name, DEFAULT_USER, DEFAULT_PASSWORD);
      return true;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      server = null;
      return false;
    }
  }

  private IVersion version(String v) {
    IVersion version;
    try {
      version = Version.Main.valueOf(v);
      return version;
    } catch (Throwable ignored) {
      log.warn("Version is invalid: {}", v);
    }
    return Version.Main.PRODUCTION;
  }

  private synchronized void source(String resource) {
    if (server == null) {
      return;
    }
    if (server.getProcess().isPresent()) {
      File file = new File(resource);
      server.getProcess().get().importFromFile(file);
    }
  }
}

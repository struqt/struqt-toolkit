package struqt.tool.jdbc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JdbcServerConfig {

  private final int urlPort;
  private final List<JdbcDatabaseConfig> databases;

  private JdbcServerConfig(int port, List<JdbcDatabaseConfig> databases) {
    this.urlPort = port;
    this.databases = databases;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getUrlPort() {
    return this.urlPort;
  }

  public List<JdbcDatabaseConfig> getDatabases() {
    return Collections.unmodifiableList(databases);
  }

  public static class JdbcDatabaseConfig {
    private final String name;
    private final List<String> initResources;

    public String getName() {
      return name;
    }

    public List<String> getInitResources() {
      return initResources;
    }

    private void addInitResource(String resource) {
      if (resource == null) {
        return;
      }
      resource = resource.trim();
      if (resource.length() <= 0) {
        return;
      }
      initResources.add(resource);
    }

    private JdbcDatabaseConfig(String name) {
      this.name = name;
      this.initResources = new LinkedList<>();
    }
  }

  public static class Builder {
    private int urlPort;
    private Map<String, JdbcDatabaseConfig> databases;

    Builder() {
      databases = new LinkedHashMap<>();
    }

    public Builder urlPort(int port) {
      this.urlPort = port;
      return this;
    }

    public Builder database(String name) {
      return database(name, null);
    }

    public Builder database(String name, String resource) {
      JdbcDatabaseConfig database = databases.get(name);
      if (database == null) {
        database = new JdbcDatabaseConfig(name);
        databases.put(name, database);
      }
      if (resource != null && resource.length() > 0) {
        database.addInitResource(resource);
      }
      return this;
    }

    public JdbcServerConfig build() {
      List<JdbcDatabaseConfig> configs = new LinkedList<>();
      configs.addAll(databases.values());
      return new JdbcServerConfig(urlPort, configs);
    }

    public String toString() {
      return "JdbcServerConfig.Builder(urlPort="
          + this.urlPort
          + ", databases="
          + this.databases
          + ")";
    }
  }
}

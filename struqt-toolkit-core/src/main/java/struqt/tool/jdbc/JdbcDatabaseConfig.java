package struqt.tool.jdbc;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JdbcDatabaseConfig {

  private final String name;
  private final int urlPort;
  private final Map<String, String> urlParams;
  private final Map<String, String> attributes;
  private final List<String> initSqlPaths;

  @ConstructorProperties({"name", "urlPort", "urlParams", "attributes", "initSqlPaths"})
  private JdbcDatabaseConfig(
      String name,
      int urlPort,
      Map<String, String> urlParams,
      Map<String, String> attributes,
      List<String> initSqlPaths) {
    this.name = name;
    this.urlPort = urlPort;
    this.urlParams = urlParams;
    this.attributes = attributes;
    this.initSqlPaths = initSqlPaths;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getName() {
    return this.name;
  }

  public int getUrlPort() {
    return this.urlPort;
  }

  public String getUrlParam(String key) {
    return this.urlParams.get(key);
  }

  public String getAttribute(String key) {
    return this.attributes.get(key);
  }

  public Map<String, String> getUrlParams() {
    return this.urlParams;
  }

  public Map<String, String> getAttributes() {
    return this.attributes;
  }

  public List<String> getInitSqlPaths() {
    return this.initSqlPaths;
  }

  public String toString() {
    return "JdbcDatabaseConfig(name="
        + this.getName()
        + ", urlPort="
        + this.getUrlPort()
        + ", urlParams="
        + this.getUrlParams()
        + ", attributes="
        + this.getAttributes()
        + ", initSqlPaths="
        + this.getInitSqlPaths()
        + ")";
  }

  public static class Builder {

    private String name = "db_unnamed";
    private int urlPort = 0;
    private Map<String, String> urlParams;
    private Map<String, String> attributes;
    private List<String> initSqlPaths;

    Builder() {
      this.urlParams = new LinkedHashMap<>();
      this.attributes = new LinkedHashMap<>();
      this.initSqlPaths = new LinkedList<>();
    }

    public JdbcDatabaseConfig.Builder name(String name) {
      this.name = name;
      return this;
    }

    public JdbcDatabaseConfig.Builder urlPort(int urlPort) {
      this.urlPort = urlPort;
      return this;
    }

    public JdbcDatabaseConfig.Builder addUrlParam(String key, String value) {
      this.urlParams.put(key, value);
      return this;
    }

    public JdbcDatabaseConfig.Builder addAttribute(String key, String value) {
      this.attributes.put(key, value);
      return this;
    }

    public JdbcDatabaseConfig.Builder addInitSqlPath(String path) {
      this.initSqlPaths.add(path);
      return this;
    }

    public JdbcDatabaseConfig build() {
      return new JdbcDatabaseConfig(
          name,
          urlPort,
          Collections.unmodifiableMap(urlParams),
          Collections.unmodifiableMap(attributes),
          Collections.unmodifiableList(initSqlPaths));
    }

    public String toString() {
      return "JdbcDatabaseConfig.Builder(name="
          + this.name
          + ", urlPort="
          + this.urlPort
          + ", urlParams="
          + this.urlParams
          + ", attributes="
          + this.attributes
          + ", initSqlPaths="
          + this.initSqlPaths
          + ")";
    }
  }
}

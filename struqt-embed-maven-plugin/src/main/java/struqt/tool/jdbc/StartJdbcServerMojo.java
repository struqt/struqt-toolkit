package struqt.tool.jdbc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;

import static struqt.tool.jdbc.Constants.CONTEXT_SERVER_NAME;
import static struqt.tool.jdbc.Constants.MOJO_NAME_START;

@Mojo(name = MOJO_NAME_START, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class StartJdbcServerMojo extends AbstractMojo {

  private static final String NAME = MOJO_NAME_START;

  @Parameter(property = NAME + ".skipExecution", defaultValue = "false")
  private boolean skip = false;

  @Parameter(property = NAME + ".port", defaultValue = "0")
  private int port = -1;

  @Parameter(property = NAME + ".database", required = true)
  private String database;

  @Parameter(property = NAME + ".urlParams")
  private Map<String, String> urlParams = null;

  @Parameter(property = NAME + ".attributes")
  private Map<String, String> attributes = null;

  @Parameter(property = NAME + ".initSqlPaths")
  private String[] initSqlPaths = null;

  private void checkConfig() throws MojoExecutionException {
    if (database == null) {
      throw new MojoExecutionException("Element 'database' in configuration is required");
    } else {
      database = database.trim();
      if (database.length() <= 0) {
        throw new MojoExecutionException("Element 'database' in configuration is required");
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Execution skipped...");
      return;
    }
    checkConfig();
    JdbcServer jdbcServer = null;
    ServiceLoader services = ServiceLoader.load(JdbcServer.class);
    if (services.iterator().hasNext()) {
      jdbcServer = (JdbcServer) services.iterator().next();
    }
    if (jdbcServer == null) {
      throw new MojoExecutionException("No service provider: " + JdbcServer.class);
    } else {
      getLog().info("Load service: " + jdbcServer.toString());
    }
    JdbcDatabaseConfig.Builder builder = JdbcDatabaseConfig.builder().name(database).urlPort(port);
    if (attributes != null && attributes.size() > 0) {
      attributes.forEach(builder::addAttribute);
    }
    if (urlParams != null && urlParams.size() > 0) {
      urlParams.forEach(builder::addUrlParam);
    }
    if (initSqlPaths != null && initSqlPaths.length > 0) {
      Arrays.stream(initSqlPaths).forEach(builder::addInitSqlPath);
    }
    JdbcDatabaseConfig config = builder.build();
    getLog().info("StartJdbcServerMojo: " + config.toString());
    jdbcServer.start(config);
    Map<Object, Object> context = getPluginContext();
    context.put(CONTEXT_SERVER_NAME, jdbcServer);
  }
}

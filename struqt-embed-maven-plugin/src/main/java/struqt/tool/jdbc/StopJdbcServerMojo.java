package struqt.tool.jdbc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Map;

import static struqt.tool.jdbc.Constants.CONTEXT_SERVER_NAME;
import static struqt.tool.jdbc.Constants.MOJO_NAME_STOP;

@Mojo(name = MOJO_NAME_STOP, defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopJdbcServerMojo extends AbstractMojo {

  @SuppressWarnings("unchecked")
  @Override
  public void execute() {
    getLog().info(StopJdbcServerMojo.class.toString());
    Map<Object, Object> context = getPluginContext();
    JdbcServer jdbcServer = (JdbcServer) context.get(CONTEXT_SERVER_NAME);
    if (jdbcServer == null) {
      return;
    }
    try {
      context.remove(CONTEXT_SERVER_NAME);
      jdbcServer.stop();
      getLog().info("StopJdbcServerMojo: " + jdbcServer.toString());
    } catch (Exception e) {
      getLog().warn(e);
    }
  }
}

package org.toya.core.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

/**
 * Created by rmerriman on 1/16/14.
 */
public class TomcatTest {

  public static void main(String[] args) throws ServletException {
    /*ConfigurationFactory confFactory = new DefaultConfigurationFactory();
    Configuration conf = confFactory.createConfiguration("tomcat7x", ContainerType.INSTALLED, ConfigurationType.EXISTING, "/Users/rmerriman/Projects/Hortonworks/Training/YARN/custom-yarn-app/apache-tomcat-7.0.501");
    conf.setProperty(ServletPropertySet.PORT, "8280");
    ContainerFactory factory = new DefaultContainerFactory();
    InstalledLocalContainer tomcat = (InstalledLocalContainer) factory.createContainer("tomcat7x", ContainerType.INSTALLED, conf);
    */
    /*
    LocalConfiguration conf = new TomcatExistingLocalConfiguration("/Users/rmerriman/Projects/Hortonworks/Training/YARN/custom-yarn-app/apache-tomcat-7.0.50");
    conf.setProperty("cargo.servlet.port", "8280");
    InstalledLocalContainer container = new Tomcat7xInstalledLocalContainer(conf);

    container.setHome("/Users/rmerriman/Projects/Hortonworks/Training/YARN/custom-yarn-app/apache-tomcat-7.0.50");

    container.start();

    container.stop();
    */

    String appBase = "/Users/rmerriman/Projects/Hortonworks/Training/YARN/apache-tomcat-7.0.50/webapps";
    File f = new File(appBase);
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8280);
    tomcat.setBaseDir(".");
    tomcat.getHost().setAppBase(appBase);
    //StandardServer server = (StandardServer)tomcat.getServer();
    //AprLifecycleListener listener = new AprLifecycleListener();
    //server.addLifecycleListener(listener);
    tomcat.addWebapp("/examples", appBase + "/examples");
    tomcat.addWebapp("/", appBase + "/ROOT");
    try {
      tomcat.start();
      tomcat.getServer().await();
    } catch (LifecycleException e) {
      e.printStackTrace();
    }

  }
}

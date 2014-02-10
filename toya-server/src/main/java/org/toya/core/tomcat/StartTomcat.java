package org.toya.core.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.toya.core.yarn.TConstants;
import zookeeper.groups.JoinGroup;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by rmerriman on 1/18/14.
 */
public class StartTomcat extends Configured implements Tool {

  private static final Log LOG = LogFactory.getLog(StartTomcat.class);

  public int run(String[] args) throws ServletException, IOException {
    Map<String, String> envs = System.getenv();
    String hdfsWebappRoot = envs.get(TConstants.HDFSWEBAPPROOT);
    int tomcatPort = Integer.parseInt(envs.get(TConstants.STARTPORT)) + Integer.parseInt(args[0]);

    Configuration conf = getConf();
    conf.addResource(new Path(envs.get("HADOOP_CONF_DIR"), "core-site.xml"));
    conf.set("fs.hdfs.impl",
            org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
    );
    FileSystem fs = FileSystem.get(conf);
    File webappsDir = new File("webapps");
    FileUtil.copy(fs, new Path(hdfsWebappRoot), webappsDir, false, conf);

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(tomcatPort);
    tomcat.setBaseDir(".");
    tomcat.getHost().setAppBase(webappsDir.getAbsolutePath());
    //StandardServer server = (StandardServer)tomcat.getServer();
    //AprLifecycleListener listener = new AprLifecycleListener();
    //server.addLifecycleListener(listener);
    for (File child: webappsDir.listFiles())
      if (child.isDirectory()) {
        tomcat.addWebapp("/" + child.getName(), child.getAbsolutePath());
      } else {
        FileUtil.unZip(child, webappsDir);
        String childName = child.getName();
        String webappName = childName.substring(0, childName.lastIndexOf("."));
        tomcat.addWebapp("/" + webappName, webappsDir.getAbsolutePath() + "/" + webappName);
      }

    try {
      tomcat.start();
      addToZookeeper(envs, tomcatPort);
      tomcat.getServer().await();
    } catch (LifecycleException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static void main(String[] args) throws Exception {

    ToolRunner.run(new Configuration(), new StartTomcat(), args);
    LOG.info("Exiting StartMemcached");
  }

  public void addToZookeeper(Map<String, String> envs, int port) {
    LOG.debug("Getting ENV Settings and ZK Servers");
    String ZKHosts = "";

    if (envs.containsKey(TConstants.ZOOKEEPERHOSTS)) {
      ZKHosts = envs.get(TConstants.ZOOKEEPERHOSTS);
    }

    System.out.println("ZKH = "+ ZKHosts);
    try {
      // Add self in zookeer /moya/ group
      //Hostname string is comma seperated list of host:port of ZK servers
      JoinGroup.main(new String[]{
              ZKHosts,
              "toya",
              InetAddress.getLocalHost().getHostName() + ":"
                      + Integer.toString(port)});
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

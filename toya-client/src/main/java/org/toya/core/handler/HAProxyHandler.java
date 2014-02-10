package org.toya.core.handler;

import org.apache.zookeeper.AsyncCallback;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by rmerriman on 2/5/14.
 */
public class HAProxyHandler implements AsyncCallback.ChildrenCallback {

  String configPath = "/Users/rmerriman/Projects/Hortonworks/Training/YARN/toya/haproxy/tomcat.cfg";

  private String template = "global\n" +
          "    daemon\n" +
          "    maxconn 256\n" +
          "\n" +
          "defaults\n" +
          "    mode http\n" +
          "    timeout connect 5000ms\n" +
          "    timeout client 50000ms\n" +
          "    timeout server 50000ms\n" +
          "\n" +
          "frontend http-in\n" +
          "    bind *:8100\n" +
          "    default_backend tomcat\n" +
          "\n" +
          "backend tomcat\n" +
          "    option httpchk GET /db/manage/server/ha/available\n" +
          "    ##SERVERS##\n" +
          "\n" +
          "listen admin\n" +
          "    bind *:8101\n" +
          "    stats enable";


  @Override
  public void processResult(int i, String s, Object o, List<String> strings) {
    System.out.println("toya nodes");
    StringBuffer serverReplaceString = new StringBuffer();
    int index = 1;
    if (strings == null || strings.size() == 0) return;
    for (String server : strings) {
      System.out.println(server);
      serverReplaceString.append("server s" + index + " " + server + " maxconn 32\n");
      index++;
    }
    try {
      FileWriter writer = new FileWriter(configPath);
      writer.write(template.replaceAll("##SERVERS##", serverReplaceString.toString()));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      Process stop = Runtime.getRuntime().exec("ps -ef | grep 'haproxy' | grep -v grep | awk '{print \"kill \" $2}' | sh");
      stop.waitFor();
      Process start = Runtime.getRuntime().exec("haproxy -f " + configPath);
      String test = "";
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

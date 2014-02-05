package org.toya.core.memcached;

import zookeeper.groups.ListGroup;

import java.util.List;

/**
 * Created by rmerriman on 1/17/14.
 */
public class ZookeeperTest {

  public static void main(String[] args) throws Exception {
    List list = ListGroup.main(new String[]{"sandbox.hortonworks.com:2181", "toya"});
    String test = "";
  }
}
